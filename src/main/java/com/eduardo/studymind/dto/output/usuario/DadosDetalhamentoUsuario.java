package com.eduardo.studymind.dto.output.usuario;

import com.eduardo.studymind.domain.usuario.Role;
import com.eduardo.studymind.domain.usuario.Usuario;

import java.time.LocalDateTime;

public record DadosDetalhamentoUsuario(
        Long id,
        String nome,
        String email,
        Role role,
        Boolean ativo,
        LocalDateTime criadoEm
) {
    public DadosDetalhamentoUsuario(Usuario usuario) {
        this(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.getAtivo(),
                usuario.getCriadoEm()
        );
    }
}
