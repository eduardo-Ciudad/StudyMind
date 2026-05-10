package com.eduardo.studymind.controller;

import com.eduardo.studymind.dto.output.recomendacao.DadosRecomendacao;
import com.eduardo.studymind.infra.security.Utils.SecurityUtils;
import com.eduardo.studymind.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recomendacao")
@RequiredArgsConstructor
public class RecomendacaoController {

    private final RecommendationService recommendationService;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<DadosRecomendacao> getRecomendacao(
            @PathVariable Long usuarioId,
            Authentication authentication) {

        SecurityUtils.verificarOwnership(usuarioId, authentication);
        return ResponseEntity.ok(recommendationService.gerarRecomendacao(usuarioId));
    }
}