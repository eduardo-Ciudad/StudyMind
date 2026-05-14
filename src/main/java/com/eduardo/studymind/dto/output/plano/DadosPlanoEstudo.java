package com.eduardo.studymind.dto.output.plano;

import java.time.LocalDateTime;

public record DadosPlanoEstudo(
        Long id,
        Long usuarioId,
        String conteudoJson,
        Integer versao,
        LocalDateTime criadoEm
) {}