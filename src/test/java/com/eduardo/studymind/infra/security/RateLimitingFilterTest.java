package com.eduardo.studymind.infra.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RateLimitingFilterTest {

    @Mock
    private FilterChain filterChain;

    private RateLimitingFilter rateLimitingFilter;

    @BeforeEach
    void setup() {
        rateLimitingFilter = new RateLimitingFilter();
    }

    private MockHttpServletResponse executarRequisicao(String uri, String ip) throws Exception {
        var request = new MockHttpServletRequest();
        request.setRequestURI(uri);
        request.setRemoteAddr(ip);
        var response = new MockHttpServletResponse();

        rateLimitingFilter.doFilter(request, response, filterChain);

        return response;
    }

    @Test
    @DisplayName("Deve permitir requisições dentro do limite padrão")
    void doFilter_dentroDoLimitePadrao() throws Exception {
        for (int i = 0; i < 30; i++) {
            var response = executarRequisicao("/materia", "10.0.0.1");
            assertThat(response.getStatus()).isEqualTo(200);
        }

        verify(filterChain, times(30)).doFilter(any(), any());
    }

    @Test
    @DisplayName("Deve bloquear requisições que excedem o limite padrão")
    void doFilter_excedeLimitePadrao() throws Exception {
        for (int i = 0; i < 30; i++) {
            executarRequisicao("/materia", "10.0.0.2");
        }

        var response = executarRequisicao("/materia", "10.0.0.2");

        assertThat(response.getStatus()).isEqualTo(429);
        assertThat(response.getContentAsString()).contains("Muitas requisições");
    }

    @Test
    @DisplayName("Deve aplicar limite mais restrito para rotas de autenticação")
    void doFilter_excedeLimiteAuth() throws Exception {
        for (int i = 0; i < 10; i++) {
            var response = executarRequisicao("/auth/login", "10.0.0.3");
            assertThat(response.getStatus()).isEqualTo(200);
        }

        var response = executarRequisicao("/auth/login", "10.0.0.3");

        assertThat(response.getStatus()).isEqualTo(429);
    }

    @Test
    @DisplayName("Deve aplicar limite mais restrito para rotas de IA")
    void doFilter_excedeLimiteIA() throws Exception {
        for (int i = 0; i < 5; i++) {
            var response = executarRequisicao("/onboarding/mensagem", "10.0.0.4");
            assertThat(response.getStatus()).isEqualTo(200);
        }

        var response = executarRequisicao("/onboarding/mensagem", "10.0.0.4");

        assertThat(response.getStatus()).isEqualTo(429);
    }

    @Test
    @DisplayName("Deve manter buckets separados por IP")
    void doFilter_bucketsSeparadosPorIp() throws Exception {
        for (int i = 0; i < 10; i++) {
            executarRequisicao("/auth/login", "10.0.0.5");
        }

        var response = executarRequisicao("/auth/login", "10.0.0.6");

        assertThat(response.getStatus()).isEqualTo(200);
    }
}
