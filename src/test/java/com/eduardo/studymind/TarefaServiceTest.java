package com.eduardo.studymind;

import com.eduardo.studymind.domain.tarefa.Tarefa;
import com.eduardo.studymind.domain.tarefa.TarefaRepository;
import com.eduardo.studymind.domain.tarefa.TarefaStatus;
import com.eduardo.studymind.domain.tarefa.TipoTarefa;
import com.eduardo.studymind.domain.topico.Topico;
import com.eduardo.studymind.domain.topico.TopicoRepository;
import com.eduardo.studymind.domain.usuario.Usuario;
import com.eduardo.studymind.domain.usuario.UsuarioRepository;
import com.eduardo.studymind.dto.input.tarefa.DadosAtualizacaoTarefa;
import com.eduardo.studymind.dto.input.tarefa.DadosCadastroTarefa;
import com.eduardo.studymind.exception.RecursoNaoEncontradoException;
import com.eduardo.studymind.exception.RegrasDeNegocioException;
import com.eduardo.studymind.service.TarefaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TarefaServiceTest {

    @Mock
    private TarefaRepository tarefaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TopicoRepository topicoRepository;

    @InjectMocks
    private TarefaService tarefaService;

    private Usuario usuario;
    private Topico topico;

    @BeforeEach
    void setup() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Eduardo");

        topico = new Topico();
        topico.setId(1L);
        topico.setNome("Geometria");
    }

    @Test
    @DisplayName("Deve cadastrar tarefa e retornar detalhamento")
    void cadastrar_sucesso() {
        var dados = new DadosCadastroTarefa(1L, 1L, TipoTarefa.REVISAO, "Revisar conteúdo", 10, LocalDate.now().plusDays(7));

        var tarefaSalva = new Tarefa();
        tarefaSalva.setId(1L);
        tarefaSalva.setUsuario(usuario);
        tarefaSalva.setTopico(topico);
        tarefaSalva.setTipo(TipoTarefa.REVISAO);
        tarefaSalva.setDescricao("Revisar conteúdo");
        tarefaSalva.setMeta(10);
        tarefaSalva.setStatus(TarefaStatus.PENDENTE);
        tarefaSalva.setCriadaEm(LocalDateTime.now());

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(topicoRepository.findById(1L)).thenReturn(Optional.of(topico));
        when(tarefaRepository.existsByUsuarioIdAndTopicoIdAndStatus(1L, 1L, TarefaStatus.PENDENTE)).thenReturn(false);
        when(tarefaRepository.save(any(Tarefa.class))).thenReturn(tarefaSalva);

        var resultado = tarefaService.cadastrar(dados);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.status()).isEqualTo(TarefaStatus.PENDENTE);
        verify(tarefaRepository).save(any(Tarefa.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar tarefa duplicada pendente")
    void cadastrar_tarefaDuplicada() {
        var dados = new DadosCadastroTarefa(1L, 1L, TipoTarefa.REVISAO, "Revisar conteúdo", 10, LocalDate.now().plusDays(7));

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(topicoRepository.findById(1L)).thenReturn(Optional.of(topico));
        when(tarefaRepository.existsByUsuarioIdAndTopicoIdAndStatus(1L, 1L, TarefaStatus.PENDENTE)).thenReturn(true);

        assertThatThrownBy(() -> tarefaService.cadastrar(dados))
                .isInstanceOf(RegrasDeNegocioException.class)
                .hasMessage("Já existe essa tarefa pendente para esse tópico");
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar tarefa com usuario inexistente")
    void cadastrar_usuarioNaoEncontrado() {
        var dados = new DadosCadastroTarefa(99L, 1L, TipoTarefa.REVISAO, "Revisar conteúdo", 10, null);

        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tarefaService.cadastrar(dados))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Usuario nao encontrado");
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar tarefa concluida")
    void atualizar_tarefaConcluida() {
        var tarefa = new Tarefa();
        tarefa.setId(1L);
        tarefa.setStatus(TarefaStatus.CONCLUIDA);

        var dados = new DadosAtualizacaoTarefa("Nova descrição", null, null, null);

        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));

        assertThatThrownBy(() -> tarefaService.atualizarTarefa(1L, dados))
                .isInstanceOf(RegrasDeNegocioException.class);
    }

    @Test
    @DisplayName("Deve cancelar tarefa pendente")
    void cancelar_sucesso() {
        var tarefa = new Tarefa();
        tarefa.setId(1L);
        tarefa.setStatus(TarefaStatus.PENDENTE);

        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));

        tarefaService.cancelar(1L);

        assertThat(tarefa.getStatus()).isEqualTo(TarefaStatus.CANCELADA);
    }

    @Test
    @DisplayName("Deve lançar exceção ao cancelar tarefa já concluida")
    void cancelar_tarefaConcluida() {
        var tarefa = new Tarefa();
        tarefa.setId(1L);
        tarefa.setStatus(TarefaStatus.CONCLUIDA);

        when(tarefaRepository.findById(1L)).thenReturn(Optional.of(tarefa));

        assertThatThrownBy(() -> tarefaService.cancelar(1L))
                .isInstanceOf(RegrasDeNegocioException.class)
                .hasMessage("Não é possivel cancelar uma tarefa já cadastrada");
    }
}