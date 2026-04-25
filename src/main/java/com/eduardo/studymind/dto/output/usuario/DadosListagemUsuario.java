package com.eduardo.studymind.dto.output.usuario;


import com.eduardo.studymind.domain.usuario.Role;
import com.eduardo.studymind.domain.usuario.Usuario;

public record DadosListagemUsuario(
        Long id,
        String nome,
        String email,
        Role role,
        Boolean ativo
) {
    public DadosListagemUsuario(Usuario usuario) {
        this(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.getAtivo()
        );
    }
}