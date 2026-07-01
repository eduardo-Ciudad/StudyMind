package com.eduardo.studymind;

import com.eduardo.studymind.domain.materia.Materia;
import com.eduardo.studymind.domain.tarefa.Tarefa;
import com.eduardo.studymind.domain.tarefa.TarefaRepository;
import com.eduardo.studymind.domain.tarefa.TipoTarefa;
import com.eduardo.studymind.domain.topico.Topico;
import com.eduardo.studymind.exception.ErroIntegracaoIAException;
import com.eduardo.studymind.exception.RecursoNaoEncontradoException;
import com.eduardo.studymind.infra.ia.AIClient;
import com.eduardo.studymind.service.TarefaDescricaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TarefaDescricaoServiceTest {

    @Mock
    private AIClient aiClient;

    @Mock
    private TarefaRepository tarefaRepository;

    @InjectMocks
    private TarefaDescricaoService tarefaDescricaoService;

    private static final String JSON_DESCRICAO_VALIDA = """
            {
              "titulo": "Revisar Funções",
              "descricaoDetalhada": "Você vai revisar os principais conceitos de funções quadráticas.",
              "passos": ["Ler a teoria", "Fazer 5 exercícios", "Conferir o gabarito"]
            }
            """;

    @Test
    @DisplayName("Deve gerar descrição da tarefa com sucesso")
    void gerarDescricao_sucesso() {
        var materia = new Materia();
        materia.setId(1L);
        materia.setNome("Matemática");

        var topico = new Topico();
        topico.setId(1L);
        topico.setNome("Funções");
        topico.setMateria(materia);

        var tarefa = new Tarefa();
        tarefa.setId(1L);
        tarefa.setTipo(TipoTarefa.REVISAO);
        tarefa.setDescricao("Revisar conteúdo");
        tarefa.setMeta(10);
        tarefa.setTopico(topico);

        when(tarefaRepository.findByIdWithTopico(1L)).thenReturn(Optional.of(tarefa));
        when(aiClient.gerarResposta(anyString())).thenReturn(JSON_DESCRICAO_VALIDA);

        var resultado = tarefaDescricaoService.gerarDescricao(1L);

        assertThat(resultado.titulo()).isEqualTo("Revisar Funções");
        assertThat(resultado.passos()).containsExactly("Ler a teoria", "Fazer 5 exercícios", "Conferir o gabarito");
    }

    @Test
    @DisplayName("Deve gerar descrição da tarefa mesmo sem topico associado")
    void gerarDescricao_semTopico() {
        var tarefa = new Tarefa();
        tarefa.setId(1L);
        tarefa.setTipo(TipoTarefa.META_ACERTO);
        tarefa.setDescricao("Alcançar 80% de acerto");
        tarefa.setMeta(80);
        tarefa.setTopico(null);

        when(tarefaRepository.findByIdWithTopico(1L)).thenReturn(Optional.of(tarefa));
        when(aiClient.gerarResposta(anyString())).thenReturn(JSON_DESCRICAO_VALIDA);

        var resultado = tarefaDescricaoService.gerarDescricao(1L);

        assertThat(resultado.titulo()).isEqualTo("Revisar Funções");
    }

    @Test
    @DisplayName("Deve lançar exceção ao gerar descrição de tarefa inexistente")
    void gerarDescricao_tarefaNaoEncontrada() {
        when(tarefaRepository.findByIdWithTopico(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tarefaDescricaoService.gerarDescricao(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Tarefa não encontrada");
    }

    @Test
    @DisplayName("Deve lançar ErroIntegracaoIAException quando JSON de descrição é inválido")
    void gerarDescricao_jsonInvalido() {
        var tarefa = new Tarefa();
        tarefa.setId(1L);
        tarefa.setTipo(TipoTarefa.REVISAO);
        tarefa.setDescricao("Revisar conteúdo");
        tarefa.setMeta(10);
        tarefa.setTopico(null);

        when(tarefaRepository.findByIdWithTopico(1L)).thenReturn(Optional.of(tarefa));
        when(aiClient.gerarResposta(anyString())).thenReturn("resposta inválida");

        assertThatThrownBy(() -> tarefaDescricaoService.gerarDescricao(1L))
                .isInstanceOf(ErroIntegracaoIAException.class)
                .hasMessage("Erro ao processar resposta da IA para tarefa");
    }
}
