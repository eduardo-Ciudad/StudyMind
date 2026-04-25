package com.eduardo.studymind.dto.input.topico;

import com.eduardo.studymind.domain.topico.NivelDificuldade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosCadastroTopico(
        @NotBlank
        String nome,
        String descricao,
        @NotNull
        Long materiaId,
        @NotNull
        NivelDificuldade nivelDificuldade

) {
}
