package com.eduardo.studymind.dto.output.materia;

import com.eduardo.studymind.domain.materia.Materia;

public record DadosDetalhamentoMateria(
        Long id,
        String nome,
        String descricao,
        Boolean ativa
) {
    public DadosDetalhamentoMateria(Materia materia) {
        this(materia.getId(), materia.getNome(),  materia.getDescricao(), materia.getAtiva());
    }
}
