package com.eduardo.studymind.dto.output.aulaoutput;

import java.util.List;

public record DadosAulaOutput(
        String titulo,
        String materia,
        String nivelDificuldade,
        String conteudo,
        List<String> recomendacoes
) {
}
