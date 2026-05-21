package com.eduardo.studymind.dto.output.performance;

import java.util.List;

public record DadosDesempenhoUsuario(
        Long usuarioId,
        int totalRespostas,
        int totalAcertos,
        double taxaAcertoGeral,
        List<DadosDesempenhoTopico> desempenhoPorTopico,
        List<DadosDesempenhoTopico> topicosMaisFracos
) {}