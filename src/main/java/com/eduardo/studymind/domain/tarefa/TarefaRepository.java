package com.eduardo.studymind.domain.tarefa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    List<Tarefa> findAllByUsuarioIdAndStatusNot(Long usuarioId, TarefaStatus status);

    List<Tarefa> findAllByUsuarioIdAndStatus(Long usuarioId, TarefaStatus status);

    List<Tarefa> findAllByUsuarioIdAndTopicoId(Long usuarioId, Long topicoId);

    boolean existsByUsuarioIdAndTopicoIdAndStatus(Long usuarioId, Long topicoId, TarefaStatus status);

    @Query("SELECT t FROM Tarefa t LEFT JOIN FETCH t.topico tp LEFT JOIN FETCH tp.materia WHERE t.id = :id")
    Optional<Tarefa> findByIdWithTopico(@Param("id") Long id);
}