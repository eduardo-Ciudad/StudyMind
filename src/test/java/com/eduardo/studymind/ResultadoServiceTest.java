package com.eduardo.studymind;

import com.eduardo.studymind.domain.questao.Questao;
import com.eduardo.studymind.domain.questao.QuestaoRepository;
import com.eduardo.studymind.domain.resultado.RespostaStatus;
import com.eduardo.studymind.domain.resultado.Resultado;
import com.eduardo.studymind.domain.resultado.ResultadoRepository;
import com.eduardo.studymind.domain.topico.Topico;
import com.eduardo.studymind.domain.usuario.Usuario;
import com.eduardo.studymind.domain.usuario.UsuarioRepository;
import com.eduardo.studymind.dto.input.resultado.DadosCadastroResultado;
import com.eduardo.studymind.exception.RecursoNaoEncontradoException;
import com.eduardo.studymind.service.ResultadoService;
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
class ResultadoServiceTest {

    @Mock
    private ResultadoRepository resultadoRepository;

    @Mock
    private QuestaoRepository questaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ResultadoService resultadoService;

    @Test
    @DisplayName("Deve cadastrar resultado e retornar detalhamento")
    void cadastrar_sucesso() {
        var dados = new DadosCadastroResultado(1L, 1L, RespostaStatus.CORRETO, "H2O");

        var usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Eduardo");

        var topico = new Topico();
        topico.setId(1L);
        topico.setNome("Química");

        var questao = new Questao();
        questao.setId(1L);
        questao.setEnunciado("Qual é a fórmula da água?");
        questao.setTopico(topico);

        var resultadoSalvo = new Resultado();
        resultadoSalvo.setId(1L);
        resultadoSalvo.setUsuario(usuario);
        resultadoSalvo.setQuestao(questao);
        resultadoSalvo.setStatus(RespostaStatus.CORRETO);
        resultadoSalvo.setRespostaUsuario("H2O");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(questaoRepository.findById(1L)).thenReturn(Optional.of(questao));
        when(resultadoRepository.save(any(Resultado.class))).thenReturn(resultadoSalvo);

        var resultado = resultadoService.cadastrar(dados);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.status()).isEqualTo(RespostaStatus.CORRETO);
        verify(resultadoRepository).save(any(Resultado.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar resultado com usuario inexistente")
    void cadastrar_usuarioNaoEncontrado() {
        var dados = new DadosCadastroResultado(99L, 1L, RespostaStatus.CORRETO, "H2O");

        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resultadoService.cadastrar(dados))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Usuario nao encontrado");
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar resultado com questao inexistente")
    void cadastrar_questaoNaoEncontrada() {
        var dados = new DadosCadastroResultado(1L, 99L, RespostaStatus.CORRETO, "H2O");

        var usuario = new Usuario();
        usuario.setId(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(questaoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resultadoService.cadastrar(dados))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Questao nao encontrado");
    }

    @Test
    @DisplayName("Deve lançar exceção ao detalhar resultado inexistente")
    void detalhar_naoEncontrado() {
        when(resultadoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resultadoService.detalharResultado(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Resultado nao encontrado");
    }
}