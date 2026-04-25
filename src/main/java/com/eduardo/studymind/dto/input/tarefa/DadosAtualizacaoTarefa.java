package com.eduardo.studymind.dto.input.tarefa;

import com.eduardo.studymind.domain.tarefa.TarefaStatus;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record DadosAtualizacaoTarefa(

        String descricao,

        @Positive
        Integer meta,

        LocalDate prazo,

        TarefaStatus status
) {}