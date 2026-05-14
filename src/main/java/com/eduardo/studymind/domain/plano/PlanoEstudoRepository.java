package com.eduardo.studymind.domain.plano;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlanoEstudoRepository extends JpaRepository<PlanoEstudo, Long> {
    Optional<PlanoEstudo> findByUsuarioIdAndAtivoTrue(Long usuarioId);
    List<PlanoEstudo> findAllByUsuarioIdOrderByVersaoAsc(Long usuarioId);

    boolean existsByUsuarioId(Long usuarioId);
}

