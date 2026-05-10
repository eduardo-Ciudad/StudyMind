package com.eduardo.studymind.dto.output.performace;

public record DadosDesempenhoTopico(
        Long topicoId,
        String topicoNome,
        String materiaNome,
        int totalRespostas,
        int totalAcertos,
        double taxaAcerto
) {}