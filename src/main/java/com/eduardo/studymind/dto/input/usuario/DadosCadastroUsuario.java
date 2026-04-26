package com.eduardo.studymind.dto.input.usuario;

import com.eduardo.studymind.domain.usuario.Role;
import com.eduardo.studymind.domain.usuario.Usuario;
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
        Role role
) {
    public DadosCadastroUsuario(Usuario usuario) {
        this(
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getSenha(),
                usuario.getRole()
        );
    }
}
