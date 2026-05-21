package com.eduardo.studymind.controller;

import com.eduardo.studymind.dto.output.performance.DadosDesempenhoUsuario;
import com.eduardo.studymind.infra.security.SecurityUtils;
import com.eduardo.studymind.service.PerformanceAnalyzerService;
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

@Tag(name = "Dashboard", description = "Dados consolidados de desempenho geral do usuário")
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final PerformanceAnalyzerService performanceAnalyzerService;

    @Operation(summary = "Obter dashboard do usuário", description = "Retorna os dados consolidados de desempenho geral do usuário informado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dashboard retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado ou sem permissão"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<DadosDesempenhoUsuario> getDashboard(
            @PathVariable Long usuarioId,
            Authentication authentication) {

        SecurityUtils.verificarOwnership(usuarioId, authentication);
        return ResponseEntity.ok(performanceAnalyzerService.analisarDesempenho(usuarioId));
    }
}