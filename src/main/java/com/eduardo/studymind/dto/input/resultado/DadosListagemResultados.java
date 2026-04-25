package com.eduardo.studymind.dto.input.resultado;

import com.eduardo.studymind.domain.resultado.RespostaStatus;
import com.eduardo.studymind.domain.resultado.Resultado;

import java.time.LocalDateTime;

public record DadosListagemResultados(
        Long id,
        Long questaoId,
        RespostaStatus status,
        LocalDateTime respondidoEm
) {
    public DadosListagemResultados(Resultado r) {
        this(r.getId(), r.getQuestao().getId(), r.getStatus(), r.getRespondidoEm());
    }
}
