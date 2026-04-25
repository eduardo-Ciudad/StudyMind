package com.eduardo.studymind.dto.input.topico;

import com.eduardo.studymind.domain.topico.NivelDificuldade;
import com.eduardo.studymind.domain.topico.Topico;

public record DadosDetalhamentoTopico(
        Long id,
        String nome,
        String descricao,
        NivelDificuldade nivel,
        Long materiaId,
        String materiaNome,
        Boolean ativo
) {
    public DadosDetalhamentoTopico (Topico topico){
        this(topico.getId(), topico.getNome(), topico.getDescricao(), topico.getNivel(), topico.getMateria().getId(),  topico.getMateria().getNome(), topico.getAtivo());
    }
}
