package com.eduardo.studymind.dto.input.questao;

import com.eduardo.studymind.domain.questao.TipoQuestao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosCadastroQuestao(
        @NotBlank
        String enunciado,
        @NotNull
        TipoQuestao tipo,
        @NotNull
        Long topicoId
) {

}
