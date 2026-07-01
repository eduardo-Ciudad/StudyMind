package com.eduardo.studymind.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Deve retornar 404 ao tratar RecursoNaoEncontradoException")
    void handleRecursoNaoEncontrado_retorna404() {
        var resposta = handler.handleRecursoNaoEncontrado(new RecursoNaoEncontradoException("Usuario nao encontrado"));

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resposta.getBody().status()).isEqualTo(404);
        assertThat(resposta.getBody().mensagem()).isEqualTo("Usuario nao encontrado");
    }

    @Test
    @DisplayName("Deve retornar 422 ao tratar RegrasDeNegocioException")
    void handleRegrasDeNegocio_retorna422() {
        var resposta = handler.handleRegrasDeNegocio(new RegrasDeNegocioException("E-mail já cadastrado"));

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(resposta.getBody().status()).isEqualTo(422);
        assertThat(resposta.getBody().mensagem()).isEqualTo("E-mail já cadastrado");
    }

    @Test
    @DisplayName("Deve retornar 502 ao tratar ErroIntegracaoIAException")
    void handleErroIA_retorna502() {
        var resposta = handler.handleErroIA(new ErroIntegracaoIAException("Erro ao processar resposta da IA", new RuntimeException()));

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(resposta.getBody().status()).isEqualTo(502);
        assertThat(resposta.getBody().mensagem()).isEqualTo("Serviço de IA indisponível. Tente novamente em instantes.");
    }

    @Test
    @DisplayName("Deve retornar 500 ao tratar exceção genérica")
    void handleErroGenerico_retorna500() {
        var resposta = handler.handleErroGenerico(new RuntimeException("erro inesperado"));

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(resposta.getBody().status()).isEqualTo(500);
        assertThat(resposta.getBody().mensagem()).isEqualTo("Erro interno do servidor");
    }
}
