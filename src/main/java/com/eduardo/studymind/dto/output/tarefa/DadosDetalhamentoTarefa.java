package com.eduardo.studymind.dto.output.tarefa;

import com.eduardo.studymind.domain.tarefa.Tarefa;
import com.eduardo.studymind.domain.tarefa.TarefaStatus;
import com.eduardo.studymind.domain.tarefa.TipoTarefa;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record DadosDetalhamentoTarefa(

        Long id,
        Long usuarioId,
        String usuarioNome,
        Long topicoId,
        String topicoNome,
        TipoTarefa tipo,
        String descricao,
        Integer meta,
        LocalDate prazo,
        TarefaStatus status,
        LocalDateTime criadaEm
) {
    public DadosDetalhamentoTarefa(Tarefa tarefa) {
        this(
                tarefa.getId(),
                tarefa.getUsuario().getId(),
                tarefa.getUsuario().getNome(),
                tarefa.getTopico() != null ? tarefa.getTopico().getId() : null,
                tarefa.getTopico() != null ? tarefa.getTopico().getNome() : null,
                tarefa.getTipo(),
                tarefa.getDescricao(),
                tarefa.getMeta(),
                tarefa.getPrazo(),
                tarefa.getStatus(),
                tarefa.getCriadaEm()
        );
    }
}