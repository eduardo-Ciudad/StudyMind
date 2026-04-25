package com.eduardo.studymind.dto.output.tarefa;

import com.eduardo.studymind.domain.tarefa.Tarefa;
import com.eduardo.studymind.domain.tarefa.TarefaStatus;
import com.eduardo.studymind.domain.tarefa.TipoTarefa;

import java.time.LocalDate;

public record DadosListagemTarefa(

        Long id,
        Long usuarioId,
        String usuarioNome,
        Long topicoId,
        String topicoNome,
        TipoTarefa tipo,
        String descricao,
        Integer meta,
        LocalDate prazo,
        TarefaStatus status
) {
    public DadosListagemTarefa(Tarefa tarefa) {
        this(
                tarefa.getId(),
                tarefa.getUsuario().getId(),
                tarefa.getUsuario().getNome(),
                tarefa.getTopico().getId(),
                tarefa.getTopico().getNome(),
                tarefa.getTipo(),
                tarefa.getDescricao(),
                tarefa.getMeta(),
                tarefa.getPrazo(),
                tarefa.getStatus()
        );
    }
}
