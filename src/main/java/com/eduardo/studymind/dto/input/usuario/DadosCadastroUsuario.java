package com.eduardo.studymind.dto.input.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.tomcat.util.digester.Rule;

public record DadosCadastroUsuario(

        @NotBlank
        String nome,

        @NotBlank
        @Email
        String email,

        @NotBlank
        String senha,

        @NotNull
        Rule rule
) {
}
