package com.eduardo.studymind.dto.input.materia;

import jakarta.validation.constraints.NotBlank;

public record DadosCadastroMateria(
        @NotBlank
        String nome,
        String descricao
) {
}
