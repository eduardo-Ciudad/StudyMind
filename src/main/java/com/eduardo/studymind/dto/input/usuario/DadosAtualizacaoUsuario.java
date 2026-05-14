package com.eduardo.studymind.dto.input.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record DadosAtualizacaoUsuario(
        String nome,

        @Email
        String email,

        @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
        String senha
) {

}
