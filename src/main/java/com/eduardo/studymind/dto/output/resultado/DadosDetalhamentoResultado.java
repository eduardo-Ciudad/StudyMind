package com.eduardo.studymind.dto.output.resultado;

import com.eduardo.studymind.domain.resultado.RespostaStatus;
import com.eduardo.studymind.domain.resultado.Resultado;

import java.time.LocalDateTime;

public record DadosDetalhamentoResultado(
        Long id,
        Long usuarioId,
        Long questaoId,
        String questaoEnunciado,
        RespostaStatus status,
        String respostaUsuario,
        LocalDateTime respondidoEm
) {
    public DadosDetalhamentoResultado(Resultado r) {
        this(r.getId(), r.getUsuario().getId(),
                r.getQuestao().getId(), r.getQuestao().getEnunciado(),
                r.getStatus(), r.getRespostaUsuario(), r.getRespondidoEm());
    }
}