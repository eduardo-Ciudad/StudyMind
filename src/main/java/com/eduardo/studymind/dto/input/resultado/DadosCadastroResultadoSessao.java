package com.eduardo.studymind.dto.input.resultado;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosCadastroResultadoSessao(
        @NotBlank String topicoNome,
        @NotBlank String materiaNome,
        @NotNull Integer totalQuestoes,
        @NotNull Integer acertos
) {
}
