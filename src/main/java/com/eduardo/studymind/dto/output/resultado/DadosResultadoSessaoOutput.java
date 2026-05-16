package com.eduardo.studymind.dto.output.resultado;

import java.time.LocalDateTime;

public record DadosResultadoSessaoOutput(
        Long id,
        String topicoNome,
        String materiaNome,
        Integer totalQuestoes,
        Integer acertos,
        Double taxaAcerto,
        LocalDateTime respondidoEm
) {
}
