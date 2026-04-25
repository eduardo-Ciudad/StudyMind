package com.eduardo.studymind.dto.input.tarefa;

import com.eduardo.studymind.domain.tarefa.TipoTarefa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record DadosCadastroTarefa(

        @NotNull
        Long usuarioId,

        @NotNull
        Long topicoId,

        @NotNull
        TipoTarefa tipo,

        @NotBlank
        String descricao,

        @NotNull
        @Positive
        Integer meta,

        LocalDate time
) {
}
