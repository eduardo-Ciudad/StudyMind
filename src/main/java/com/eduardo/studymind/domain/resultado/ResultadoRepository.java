package com.eduardo.studymind.domain.resultado;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResultadoRepository extends JpaRepository<Resultado, Long> {

    List<Resultado> findAllByUsuarioId(Long usuarioId);

    List<Resultado> findAllByUsuarioIdAndQuestaoTopicoId(Long usuarioId, Long topicoId);

    long countByUsuarioIdAndQuestaoTopicoIdAndStatus(Long usuarioId, Long topicoId, RespostaStatus status);
    /**
     * Conta quantas respostas de um usuário em um determinado tópico
     * tiveram um status específico (CORRETO, INCORRETO ou PULADO).
     *
     * Usado para calcular o desempenho do usuário por tópico.
     * Exemplo: countBy(..., CORRETO) / total de questões = taxa de acerto.
     * Essa taxa alimenta a lógica de geração de Tarefas pela IA.
     */

}
