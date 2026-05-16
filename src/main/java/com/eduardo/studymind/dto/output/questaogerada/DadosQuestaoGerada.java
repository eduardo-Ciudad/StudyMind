package com.eduardo.studymind.dto.output.questaogerada;

import java.util.List;

public record DadosQuestaoGerada(
        int numero,
        String enunciado,
        List<String> alternativas,
        int alternativaCorreta,  // índice 0-based
        String explicacao
) {
}
