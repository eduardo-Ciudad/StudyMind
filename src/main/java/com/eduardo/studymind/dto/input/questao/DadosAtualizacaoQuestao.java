package com.eduardo.studymind.dto.input.questao;

import com.eduardo.studymind.domain.questao.TipoQuestao;

public record DadosAtualizacaoQuestao(
        String enunciado,
        TipoQuestao tipo,
        Boolean ativa
) {
}
