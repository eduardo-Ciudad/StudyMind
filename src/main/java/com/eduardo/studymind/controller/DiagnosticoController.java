package com.eduardo.studymind.controller;

import com.eduardo.studymind.dto.output.performance.DadosDesempenhoTopico;
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

import java.util.List;

@Tag(name = "Diagnóstico", description = "Análise de desempenho por tópico do estudante")
@RestController
@RequestMapping("/diagnostico")
@RequiredArgsConstructor
public class DiagnosticoController {

    private final PerformanceAnalyzerService performanceAnalyzerService;

    @Operation(summary = "Obter diagnóstico do usuário", description = "Retorna a análise de desempenho por tópico do usuário informado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Diagnóstico retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado ou sem permissão"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<DadosDesempenhoTopico>> getDiagnostico(
            @PathVariable Long usuarioId,
            Authentication authentication) {

        SecurityUtils.verificarOwnership(usuarioId, authentication);
        var desempenho = performanceAnalyzerService.analisarDesempenho(usuarioId);
        return ResponseEntity.ok(desempenho.desempenhoPorTopico());
    }
}
