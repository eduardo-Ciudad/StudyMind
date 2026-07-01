package com.eduardo.studymind.infra.security;

import com.eduardo.studymind.domain.usuario.Role;
import com.eduardo.studymind.domain.usuario.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityUtilsTest {

    @Mock
    private Authentication authentication;

    @Test
    @DisplayName("Deve retornar o usuario autenticado a partir da Authentication")
    void getUsuarioAuthenticado_sucesso() {
        var usuario = new Usuario();
        usuario.setId(1L);
        when(authentication.getPrincipal()).thenReturn(usuario);

        var resultado = SecurityUtils.getUsuarioAuthenticado(authentication);

        assertThat(resultado).isEqualTo(usuario);
    }

    @Test
    @DisplayName("Não deve lançar exceção quando usuario acessa os próprios dados")
    void verificarOwnership_proprioUsuario() {
        var usuario = new Usuario();
        usuario.setId(1L);
        usuario.setRole(Role.ALUNO);
        when(authentication.getPrincipal()).thenReturn(usuario);

        assertThatNoException()
                .isThrownBy(() -> SecurityUtils.verificarOwnership(1L, authentication));
    }

    @Test
    @DisplayName("Não deve lançar exceção quando administrador acessa dados de outro usuario")
    void verificarOwnership_admin() {
        var admin = new Usuario();
        admin.setId(1L);
        admin.setRole(Role.ADMIN);
        when(authentication.getPrincipal()).thenReturn(admin);

        assertThatNoException()
                .isThrownBy(() -> SecurityUtils.verificarOwnership(99L, authentication));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuario comum tenta acessar dados de outro usuario")
    void verificarOwnership_acessoNegado() {
        var usuario = new Usuario();
        usuario.setId(1L);
        usuario.setRole(Role.ALUNO);
        when(authentication.getPrincipal()).thenReturn(usuario);

        assertThatThrownBy(() -> SecurityUtils.verificarOwnership(99L, authentication))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Acesso negado");
    }
}
