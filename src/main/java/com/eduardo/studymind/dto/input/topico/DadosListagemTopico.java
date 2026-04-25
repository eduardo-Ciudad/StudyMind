package com.eduardo.studymind.dto.input.topico;

import com.eduardo.studymind.domain.materia.Materia;
import com.eduardo.studymind.domain.topico.NivelDificuldade;
import com.eduardo.studymind.domain.topico.Topico;

public record DadosListagemTopico(
        Long id,
        String nome,
        NivelDificuldade nivel,
        String materia,
        Boolean ativo
) {
    public DadosListagemTopico(Topico topico){
        this(topico.getId(), topico.getNome(), topico.getNivel(), topico.getMateria().getNome(), topico.getAtivo());
    }


}
