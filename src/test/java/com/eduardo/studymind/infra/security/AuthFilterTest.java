package com.eduardo.studymind.infra.security;

import com.eduardo.studymind.domain.usuario.Role;
import com.eduardo.studymind.domain.usuario.Usuario;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private AuthFilter authFilter;

    @AfterEach
    void limparContextoSeguranca() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Deve seguir a cadeia de filtros sem autenticar quando não há token")
    void doFilterInternal_semToken() throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();

        authFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Deve autenticar o usuario quando o token é válido")
    void doFilterInternal_tokenValido() throws Exception {
        var usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("edu@email.com");
        usuario.setRole(Role.ALUNO);
        usuario.setAtivo(true);

        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-valido");
        var response = new MockHttpServletResponse();

        when(jwtService.validarToken("token-valido")).thenReturn("edu@email.com");
        when(userDetailsService.loadUserByUsername("edu@email.com")).thenReturn(usuario);

        authFilter.doFilterInternal(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isEqualTo(usuario);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Não deve autenticar quando o token é inválido")
    void doFilterInternal_tokenInvalido() throws Exception {
        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-invalido");
        var response = new MockHttpServletResponse();

        when(jwtService.validarToken("token-invalido")).thenReturn(null);

        authFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Não deve autenticar usuario desativado")
    void doFilterInternal_usuarioDesativado() throws Exception {
        var usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("edu@email.com");
        usuario.setRole(Role.ALUNO);
        usuario.setAtivo(false);

        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token-valido");
        var response = new MockHttpServletResponse();

        when(jwtService.validarToken("token-valido")).thenReturn("edu@email.com");
        when(userDetailsService.loadUserByUsername("edu@email.com")).thenReturn(usuario);

        authFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }
}
