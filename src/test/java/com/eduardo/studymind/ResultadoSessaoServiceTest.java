package com.eduardo.studymind;

import com.eduardo.studymind.domain.materia.MateriaRepository;
import com.eduardo.studymind.domain.resultado.ResultadoSessao;
import com.eduardo.studymind.domain.resultado.ResultadoSessaoRepository;
import com.eduardo.studymind.domain.topico.TopicoRepository;
import com.eduardo.studymind.domain.usuario.Usuario;
import com.eduardo.studymind.domain.usuario.UsuarioRepository;
import com.eduardo.studymind.dto.input.resultado.DadosCadastroResultadoSessao;
import com.eduardo.studymind.exception.RecursoNaoEncontradoException;
import com.eduardo.studymind.service.ResultadoSessaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResultadoSessaoServiceTest {

    @Mock
    private ResultadoSessaoRepository repository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TopicoRepository topicoRepository;

    @Mock
    private MateriaRepository materiaRepository;

    @InjectMocks
    private ResultadoSessaoService resultadoSessaoService;

    private Usuario usuario;

    @BeforeEach
    void setup() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Eduardo");
    }

    @Test
    @DisplayName("Deve salvar resultado de sessão e calcular taxa de acerto")
    void salvar_sucesso() {
        var dados = new DadosCadastroResultadoSessao("Funções", "Matemática", 10, 7);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(topicoRepository.findByNomeAndMateriaNomeAndUsuarioId("Funções", "Matemática", 1L))
                .thenReturn(Optional.empty());
        when(materiaRepository.findByNomeAndUsuarioId("Matemática", 1L)).thenReturn(Optional.empty());

        var resultado = resultadoSessaoService.salvar(dados, 1L);

        assertThat(resultado.topicoNome()).isEqualTo("Funções");
        assertThat(resultado.materiaNome()).isEqualTo("Matemática");
        assertThat(resultado.totalQuestoes()).isEqualTo(10);
        assertThat(resultado.acertos()).isEqualTo(7);
        assertThat(resultado.taxaAcerto()).isEqualTo(70.0);
        verify(repository).save(any(ResultadoSessao.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar resultado com usuario inexistente")
    void salvar_usuarioNaoEncontrado() {
        var dados = new DadosCadastroResultadoSessao("Funções", "Matemática", 10, 7);

        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resultadoSessaoService.salvar(dados, 99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Usuário não encontrado");
    }

    @Test
    @DisplayName("Deve calcular taxa de acerto zerada quando total de questões é zero")
    void salvar_totalQuestoesZero() {
        var dados = new DadosCadastroResultadoSessao("Funções", "Matemática", 0, 0);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(topicoRepository.findByNomeAndMateriaNomeAndUsuarioId("Funções", "Matemática", 1L))
                .thenReturn(Optional.empty());
        when(materiaRepository.findByNomeAndUsuarioId("Matemática", 1L)).thenReturn(Optional.empty());

        var resultado = resultadoSessaoService.salvar(dados, 1L);

        assertThat(resultado.taxaAcerto()).isZero();
    }

    @Test
    @DisplayName("Deve arredondar a taxa de acerto com duas casas decimais")
    void salvar_arredondaTaxaAcerto() {
        var dados = new DadosCadastroResultadoSessao("Funções", "Matemática", 3, 1);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(topicoRepository.findByNomeAndMateriaNomeAndUsuarioId("Funções", "Matemática", 1L))
                .thenReturn(Optional.empty());
        when(materiaRepository.findByNomeAndUsuarioId("Matemática", 1L)).thenReturn(Optional.empty());

        var resultado = resultadoSessaoService.salvar(dados, 1L);

        assertThat(resultado.taxaAcerto()).isEqualTo(33.33);
    }

    @Test
    @DisplayName("Deve retornar lista de sessões do usuário ordenadas por data")
    void listarPorUsuario_sucesso() {
        var sessao = new ResultadoSessao();
        sessao.setId(1L);
        sessao.setUsuario(usuario);
        sessao.setTopicoNome("Funções");
        sessao.setMateriaNome("Matemática");
        sessao.setTotalQuestoes(10);
        sessao.setAcertos(8);
        sessao.setTaxaAcerto(80.0);

        when(repository.findByUsuarioIdOrderByRespondidoEmDesc(1L)).thenReturn(List.of(sessao));

        var resultado = resultadoSessaoService.listarPorUsuario(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).topicoNome()).isEqualTo("Funções");
        assertThat(resultado.get(0).taxaAcerto()).isEqualTo(80.0);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando usuário não possui sessões")
    void listarPorUsuario_semSessoes() {
        when(repository.findByUsuarioIdOrderByRespondidoEmDesc(1L)).thenReturn(List.of());

        var resultado = resultadoSessaoService.listarPorUsuario(1L);

        assertThat(resultado).isEmpty();
    }
}
