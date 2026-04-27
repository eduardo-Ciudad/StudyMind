package com.eduardo.studymind.domain.questao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestaoRepository extends JpaRepository<Questao, Long> {
    List<Questao> findAllByTopicoIdAndAtivaTrue(Long topicoId);
    Page<Questao> findByAtivaTrue(Pageable pageable);
}
