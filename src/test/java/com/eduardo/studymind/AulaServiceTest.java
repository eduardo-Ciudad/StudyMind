package com.eduardo.studymind;

import com.eduardo.studymind.domain.materia.Materia;
import com.eduardo.studymind.domain.topico.NivelDificuldade;
import com.eduardo.studymind.domain.topico.Topico;
import com.eduardo.studymind.domain.topico.TopicoRepository;
import com.eduardo.studymind.exception.ErroIntegracaoIAException;
import com.eduardo.studymind.infra.ia.AIClient;
import com.eduardo.studymind.service.AulaService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
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
class AulaServiceTest {

    @Mock
    private AIClient aiClient;

    @Mock
    private TopicoRepository topicoRepository;

    @InjectMocks
    private AulaService aulaService;

    private Topico topico;

    private static final String JSON_AULA_VALIDA = """
            {
              "titulo": "Introdução a Funções",
              "materia": "Matemática",
              "nivelDificuldade": "MEDIO",
              "conteudo": "Conteúdo detalhado sobre funções quadráticas.",
              "recomendacoes": ["Praticar exercícios", "Revisar teoria"]
            }
            """;

    private static final String JSON_QUESTOES_VALIDA = """
            {
              "topico": "Funções",
              "total": 1,
              "questoes": [
                {
                  "numero": 1,
                  "enunciado": "Qual é o vértice da parábola y = x²?",
                  "alternativas": ["A) (0,0)", "B) (1,1)", "C) (0,1)", "D) (1,0)", "E) (-1,0)"],
                  "alternativaCorreta": 0,
                  "explicacao": "O vértice de y = x² está na origem."
                }
              ]
            }
            """;

    @BeforeEach
    void setup() {
        var materia = new Materia();
        materia.setId(1L);
        materia.setNome("Matemática");

        topico = new Topico();
        topico.setId(1L);
        topico.setNome("Funções");
        topico.setMateria(materia);
        topico.setNivel(NivelDificuldade.MEDIO);
    }

    @Test
    @DisplayName("Deve gerar conteúdo de aula com sucesso")
    void gerarConteudoAula_sucesso() {
        when(topicoRepository.findById(1L)).thenReturn(Optional.of(topico));
        when(aiClient.gerarResposta(anyString())).thenReturn(JSON_AULA_VALIDA);

        var resultado = aulaService.gerarConteudoAula(1L);

        assertThat(resultado.titulo()).isEqualTo("Introdução a Funções");
        assertThat(resultado.materia()).isEqualTo("Matemática");
        assertThat(resultado.recomendacoes()).containsExactly("Praticar exercícios", "Revisar teoria");
    }

    @Test
    @DisplayName("Deve lançar exceção ao gerar aula para topico inexistente")
    void gerarConteudoAula_topicoNaoEncontrado() {
        when(topicoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> aulaService.gerarConteudoAula(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Tópico não encontrado");
    }

    @Test
    @DisplayName("Deve lançar ErroIntegracaoIAException quando JSON de aula é inválido")
    void gerarConteudoAula_jsonInvalido() {
        when(topicoRepository.findById(1L)).thenReturn(Optional.of(topico));
        when(aiClient.gerarResposta(anyString())).thenReturn("resposta inválida");

        assertThatThrownBy(() -> aulaService.gerarConteudoAula(1L))
                .isInstanceOf(ErroIntegracaoIAException.class)
                .hasMessage("Erro ao processar resposta da IA para aula");
    }

    @Test
    @DisplayName("Deve gerar questões com sucesso")
    void gerarQuestoes_sucesso() {
        when(topicoRepository.findById(1L)).thenReturn(Optional.of(topico));
        when(aiClient.gerarResposta(anyString())).thenReturn(JSON_QUESTOES_VALIDA);

        var resultado = aulaService.gerarQuestoes(1L, 1);

        assertThat(resultado.topico()).isEqualTo("Funções");
        assertThat(resultado.total()).isEqualTo(1);
        assertThat(resultado.questoes()).hasSize(1);
        assertThat(resultado.questoes().get(0).alternativaCorreta()).isZero();
    }

    @Test
    @DisplayName("Deve lançar exceção ao gerar questões para topico inexistente")
    void gerarQuestoes_topicoNaoEncontrado() {
        when(topicoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> aulaService.gerarQuestoes(99L, 5))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Tópico não encontrado");
    }

    @Test
    @DisplayName("Deve lançar ErroIntegracaoIAException quando JSON de questões é inválido")
    void gerarQuestoes_jsonInvalido() {
        when(topicoRepository.findById(1L)).thenReturn(Optional.of(topico));
        when(aiClient.gerarResposta(anyString())).thenReturn("resposta inválida");

        assertThatThrownBy(() -> aulaService.gerarQuestoes(1L, 5))
                .isInstanceOf(ErroIntegracaoIAException.class)
                .hasMessage("Erro ao processar resposta da IA para questões");
    }

    @Test
    @DisplayName("Deve gerar conteúdo de aula por nome sem consultar o repositório")
    void gerarConteudoPorNome_sucesso() {
        when(aiClient.gerarResposta(anyString())).thenReturn(JSON_AULA_VALIDA);

        var resultado = aulaService.gerarConteudoPorNome("Funções", "Matemática", "MEDIO");

        assertThat(resultado.titulo()).isEqualTo("Introdução a Funções");
    }

    @Test
    @DisplayName("Deve gerar questões por nome sem consultar o repositório")
    void gerarQuestoesPorNome_sucesso() {
        when(aiClient.gerarResposta(anyString())).thenReturn(JSON_QUESTOES_VALIDA);

        var resultado = aulaService.gerarQuestoesPorNome("Funções", "Matemática", 1);

        assertThat(resultado.topico()).isEqualTo("Funções");
        assertThat(resultado.questoes()).hasSize(1);
    }
}
