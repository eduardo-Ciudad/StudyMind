package com.eduardo.studymind;

import com.eduardo.studymind.domain.questao.Questao;
import com.eduardo.studymind.domain.questao.QuestaoRepository;
import com.eduardo.studymind.domain.questao.TipoQuestao;
import com.eduardo.studymind.domain.topico.Topico;
import com.eduardo.studymind.domain.topico.TopicoRepository;
import com.eduardo.studymind.dto.input.questao.DadosAtualizacaoQuestao;
import com.eduardo.studymind.dto.input.questao.DadosCadastroQuestao;
import com.eduardo.studymind.exception.RecursoNaoEncontradoException;
import com.eduardo.studymind.service.QuestaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestaoServiceTest {

    @Mock
    private QuestaoRepository questaoRepository;

    @Mock
    private TopicoRepository topicoRepository;

    @InjectMocks
    private QuestaoService questaoService;

    @Test
    @DisplayName("Deve cadastrar questao e retornar detalhamento")
    void cadastrar_sucesso() {
        var dados = new DadosCadastroQuestao("Qual é a fórmula da água?", TipoQuestao.DISSERTATIVA, 1L);

        var topico = new Topico();
        topico.setId(1L);
        topico.setNome("Química");

        var questaoSalva = new Questao();
        questaoSalva.setId(1L);
        questaoSalva.setEnunciado("Qual é a fórmula da água?");
        questaoSalva.setTipo(TipoQuestao.DISSERTATIVA);
        questaoSalva.setTopico(topico);
        questaoSalva.setAtiva(true);

        when(topicoRepository.findById(1L)).thenReturn(Optional.of(topico));
        when(questaoRepository.save(any(Questao.class))).thenReturn(questaoSalva);

        var resultado = questaoService.cadastrar(dados);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.enunciado()).isEqualTo("Qual é a fórmula da água?");
        verify(questaoRepository).save(any(Questao.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar questao com topico inexistente")
    void cadastrar_topicoNaoEncontrado() {
        var dados = new DadosCadastroQuestao("Enunciado", TipoQuestao.DISSERTATIVA, 99L);

        when(topicoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> questaoService.cadastrar(dados))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Tópico nao encontrado");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar questao inexistente para atualizar")
    void atualizar_naoEncontrado() {
        var dados = new DadosAtualizacaoQuestao("Novo enunciado", null, null);

        when(questaoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> questaoService.atualizar(99L, dados))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Questao nao encontrado");
    }

    @Test
    @DisplayName("Deve inativar questao existente")
    void inativar_sucesso() {
        var questao = new Questao();
        questao.setId(1L);
        questao.setAtiva(true);

        when(questaoRepository.findById(1L)).thenReturn(Optional.of(questao));

        questaoService.inativar(1L);

        assertThat(questao.getAtiva()).isFalse();
    }

    @Test
    @DisplayName("Deve lançar exceção ao inativar questao inexistente")
    void inativar_naoEncontrado() {
        when(questaoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> questaoService.inativar(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Questao nao encontrado");
    }
}