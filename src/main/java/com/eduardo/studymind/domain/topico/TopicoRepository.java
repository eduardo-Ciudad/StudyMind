package com.eduardo.studymind.domain.topico;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TopicoRepository extends JpaRepository<Topico, Long> {
    List<Topico> findAllByAtivoTrue();
    List<Topico> findAllByMateriaIdAndAtivoTrue(Long materiaId);

    boolean existsByNomeAndMateriaId(String nome, Long materiaId);

    @Query("SELECT t FROM Topico t JOIN FETCH t.materia WHERE t.ativo = true")
    List<Topico> findAllByAtivoTrueWithMateria();
}
