package com.eduardo.studymind.dto.input.resultado;

import com.eduardo.studymind.domain.resultado.RespostaStatus;
import jakarta.validation.constraints.NotNull;

public record DadosCadastroResultado(
        @NotNull Long usuarioId,
        @NotNull Long questaoId,
        @NotNull RespostaStatus status,
        String respostaUsuario
) {
}
