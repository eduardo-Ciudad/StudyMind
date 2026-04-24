package com.eduardo.studymind.domain.tarefa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    List<Tarefa> findAllByUsuarioIdAndStatus(Long usuarioId, TarefaStatus status);

    List<Tarefa> findAllByUsuarioIdAndTopicoId(Long usuarioId, Long topicoId);

    boolean existsByUsuarioIdAndTopicoIdAndStatus(Long usuarioId, Long topicoId, TarefaStatus status);
}