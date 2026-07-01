package com.eduardo.studymind.infra.security;

import com.eduardo.studymind.domain.usuario.Role;
import com.eduardo.studymind.domain.usuario.Usuario;
import com.eduardo.studymind.domain.usuario.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("Deve carregar usuario existente pelo email")
    void loadUserByUsername_sucesso() {
        var usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Eduardo");
        usuario.setEmail("edu@email.com");
        usuario.setRole(Role.ALUNO);
        usuario.setAtivo(true);

        when(usuarioRepository.findByEmail("edu@email.com")).thenReturn(Optional.of(usuario));

        var resultado = userDetailsService.loadUserByUsername("edu@email.com");

        assertThat(resultado.getUsername()).isEqualTo("edu@email.com");
        assertThat(resultado).isEqualTo(usuario);
    }

    @Test
    @DisplayName("Deve lançar exceção ao carregar usuario com email inexistente")
    void loadUserByUsername_naoEncontrado() {
        when(usuarioRepository.findByEmail("inexistente@email.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("inexistente@email.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Usuário não encontrado: inexistente@email.com");
    }
}
