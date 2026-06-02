package com.eduardo.studymind.domain.topico;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TopicoRepository extends JpaRepository<Topico, Long> {

    List<Topico> findAllByAtivoTrue();

    List<Topico> findAllByUsuarioIdAndAtivoTrue(Long usuarioId);

    List<Topico> findAllByMateriaIdAndAtivoTrue(Long materiaId);

    Optional<Topico> findByNomeAndMateriaIdAndUsuarioId(String nome, Long materiaId, Long usuarioId);

    boolean existsByNomeAndMateriaId(String nome, Long materiaId);

    Optional<Topico> findByNomeAndMateriaNome(String nome, String materiaNome);

    Optional<Topico> findByNomeAndMateriaNomeAndUsuarioId(
            String nome, String materiaNome, Long usuarioId
    );

    @Query("SELECT t FROM Topico t JOIN FETCH t.materia WHERE t.ativo = true AND t.usuario.id = :usuarioId")
    List<Topico> findAllByAtivoTrueWithMateriaAndUsuarioId(@Param("usuarioId") Long usuarioId);
}