package com.eduardo.studymind.dto.input.usuario;

import jakarta.validation.constraints.Email;

public record DadosAtualizacaoUsuario(
        String nome,

        @Email
        String email,

        String senha
) {

}
