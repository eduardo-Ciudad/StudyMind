package com.eduardo.studymind.dto.output.questoesoutput;

import com.eduardo.studymind.dto.output.questaogerada.DadosQuestaoGerada;

import java.util.List;

public record DadosQuestoesOutput(
        String topico,
        int total,
        List<DadosQuestaoGerada> questoes
) {
}
