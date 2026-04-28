package com.eduardo.studymind;

import com.eduardo.studymind.domain.usuario.Role;
import com.eduardo.studymind.domain.usuario.Usuario;
import com.eduardo.studymind.domain.usuario.UsuarioRepository;
import com.eduardo.studymind.dto.input.usuario.DadosAtualizacaoUsuario;
import com.eduardo.studymind.dto.input.usuario.DadosCadastroUsuario;
import com.eduardo.studymind.exception.RecursoNaoEncontradoException;
import com.eduardo.studymind.exception.RegrasDeNegocioException;
import com.eduardo.studymind.service.UsuarioService;
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
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Deve cadastrar usuario e retornar detalhamento")
    void cadastrarUsuario_sucesso() {
        var dados = new DadosCadastroUsuario("Eduardo", "edu@email.com", "senha123", Role.ALUNO);

        var usuarioSalvo = new Usuario();
        usuarioSalvo.setId(1L);
        usuarioSalvo.setNome("Eduardo");
        usuarioSalvo.setEmail("edu@email.com");
        usuarioSalvo.setAtivo(true);

        when(usuarioRepository.existsByEmail("edu@email.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);

        var resultado = usuarioService.cadastrarUsuario(dados);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nome()).isEqualTo("Eduardo");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar usuario com email duplicado")
    void cadastrarUsuario_emailDuplicado() {
        var dados = new DadosCadastroUsuario("Eduardo", "edu@email.com", "senha123", Role.ALUNO);

        when(usuarioRepository.existsByEmail("edu@email.com")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.cadastrarUsuario(dados))
                .isInstanceOf(RegrasDeNegocioException.class)
                .hasMessage("E-mail já cadastrado");
    }

    @Test
    @DisplayName("Deve retornar usuario ao buscar por ID existente")
    void buscarPorId_sucesso() {
        var usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Eduardo");
        usuario.setAtivo(true);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        var resultado = usuarioService.buscarPorId(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nome()).isEqualTo("Eduardo");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuario inexistente")
    void buscarPorId_naoEncontrado() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Usuario nao encontrado");
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com email já cadastrado")
    void atualizarUsuario_emailDuplicado() {
        var usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("edu@email.com");

        var dados = new DadosAtualizacaoUsuario("Eduardo", "novo@email.com", "123456");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByEmail("novo@email.com")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.atualizarUsuario(1L, dados))
                .isInstanceOf(RegrasDeNegocioException.class)
                .hasMessage("E-mail já cadastrado");
    }

    @Test
    @DisplayName("Deve desativar usuario existente")
    void desativar_sucesso() {
        var usuario = new Usuario();
        usuario.setId(1L);
        usuario.setAtivo(true);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        usuarioService.desativar(1L);

        assertThat(usuario.getAtivo()).isFalse();
    }
}
