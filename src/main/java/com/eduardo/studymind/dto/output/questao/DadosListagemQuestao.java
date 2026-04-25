package com.eduardo.studymind.dto.output.questao;

import com.eduardo.studymind.domain.questao.Questao;
import com.eduardo.studymind.domain.questao.TipoQuestao;

public record DadosListagemQuestao(
        Long id,
        String enunciado,
        TipoQuestao tipo,
        String topicoNome,
        Boolean ativa

) {
    public DadosListagemQuestao(Questao questao){
        this(questao.getId(), questao.getEnunciado(), questao.getTipo(), questao.getTopico().getNome(), questao.getAtiva());
    }
}
