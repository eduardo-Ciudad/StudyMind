package com.eduardo.studymind.domain.materia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MateriaRepository extends JpaRepository<Materia, Long> {

    List<Materia> findAllByAtivaTrue();

    boolean existsByNome(String nome);

}
