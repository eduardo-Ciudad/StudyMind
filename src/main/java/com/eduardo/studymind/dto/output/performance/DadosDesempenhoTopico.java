package com.eduardo.studymind.dto.output.performance;

public record DadosDesempenhoTopico(
        Long topicoId,
        String topicoNome,
        String materiaNome,
        int totalRespostas,
        int totalAcertos,
        double taxaAcerto
) {}