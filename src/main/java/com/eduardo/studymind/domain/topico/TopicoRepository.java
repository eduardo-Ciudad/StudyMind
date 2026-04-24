package com.eduardo.studymind.domain.topico;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicoRepository extends JpaRepository<Topico, Long> {

    List<Topico> findAllByMateriaIdAndAtivoTrue(Long materiaId);

    boolean existsByNomeAndMateriaId(String nome, Long materiaId);
}
