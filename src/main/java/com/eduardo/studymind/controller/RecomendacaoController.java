package com.eduardo.studymind.controller;

import com.eduardo.studymind.dto.output.recomendacao.DadosRecomendacao;
import com.eduardo.studymind.infra.security.SecurityUtils;
import com.eduardo.studymind.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Recomendação", description = "Geração de recomendações personalizadas de estudo via IA")
@RestController
@RequestMapping("/recomendacao")
@RequiredArgsConstructor
public class RecomendacaoController {

    private final RecommendationService recommendationService;

    @Operation(summary = "Obter recomendação de estudo", description = "Gera recomendações personalizadas de estudo para o usuário com base em seu desempenho")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Recomendação gerada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado ou sem permissão"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<DadosRecomendacao> getRecomendacao(
            @PathVariable Long usuarioId,
            Authentication authentication) {

        SecurityUtils.verificarOwnership(usuarioId, authentication);
        return ResponseEntity.ok(recommendationService.gerarRecomendacao(usuarioId));
    }
}