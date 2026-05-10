package com.eduardo.studymind.dto.output.recomendacao;

import java.util.List;

public record DadosRecomendacao(
        Long usuarioId,
        String diagnostico,
        List<String> topicosPrioritarios,
        List<String> dicasPraticas,
        String mensagemMotivacional,
        double taxaAcertoGeral
) {
}
