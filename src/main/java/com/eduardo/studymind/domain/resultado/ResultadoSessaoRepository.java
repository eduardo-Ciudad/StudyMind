package com.eduardo.studymind.domain.resultado;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResultadoSessaoRepository extends JpaRepository<ResultadoSessao, Long> {
    List<ResultadoSessao> findByUsuarioIdOrderByRespondidoEmDesc(Long usuarioId);
}
