package com.eduardo.studymind.dto.input.materia;

import jakarta.validation.constraints.NotBlank;

public record DadosAtualizacaoMateria(
        @NotBlank
        String nome,
        String descricao,
        Boolean ativa
) {
}
