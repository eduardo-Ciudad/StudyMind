package com.eduardo.studymind.dto.output.jwt;

public record DadosTokenRefresh(
        String accessToken,
        String refreshToken,
        String tipo
) {}
