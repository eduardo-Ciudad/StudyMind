package com.eduardo.studymind.dto.input.topico;

import com.eduardo.studymind.domain.topico.NivelDificuldade;

public record DadosAtualizacaoTopico(
        String nome,
        String descricao,
        NivelDificuldade nivelDificuldade,
        Boolean ativo
) {
}
