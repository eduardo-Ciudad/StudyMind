package com.eduardo.studymind.dto.input.questao;

import com.eduardo.studymind.domain.questao.Questao;
import com.eduardo.studymind.domain.questao.TipoQuestao;

public record DadosDetalhamentoQuestao(
        Long id,
        String enunciado,
        TipoQuestao tipo,
        Long topicoId,
        String topicoNome,
        Boolean ativa
) {
    public DadosDetalhamentoQuestao(Questao questao) {
        this(questao.getId(), questao.getEnunciado(), questao.getTipo(),
                questao.getTopico().getId(), questao.getTopico().getNome(), questao.getAtiva());
    }
}
