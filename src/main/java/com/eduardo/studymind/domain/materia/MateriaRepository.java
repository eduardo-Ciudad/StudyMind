package com.eduardo.studymind.domain.materia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MateriaRepository extends JpaRepository<Materia, Long> {

    List<Materia> findAllByAtivaTrue();

    List<Materia> findAllByUsuarioIdAndAtivaTrue(Long usuarioId);

    Optional<Materia> findByNomeAndUsuarioId(String nome, Long usuarioId);

    boolean existsByNomeAndUsuarioId(String nome, Long usuarioId);
}