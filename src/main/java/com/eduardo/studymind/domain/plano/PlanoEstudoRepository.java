package com.eduardo.studymind.domain.plano;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanoEstudoRepository extends JpaRepository<PlanoEstudo, Long> {
    Optional<PlanoEstudo> findByUsuarioId(Long usuarioId);
    boolean existsByUsuarioId(Long usuarioId);
}

