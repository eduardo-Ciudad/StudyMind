package com.eduardo.studymind.dto.output.materia;

import com.eduardo.studymind.domain.materia.Materia;

public record DadosListagemMateria(
        Long id,
        String nome,
        Boolean ativa
) {
    public DadosListagemMateria(Materia materia){
        this(materia.getId(), materia.getNome(), materia.getAtiva());
    }
}
