package com.eduardo.studymind.controller;

import com.eduardo.studymind.dto.output.performace.DadosDesempenhoTopico;
import com.eduardo.studymind.infra.security.Utils.SecurityUtils;
import com.eduardo.studymind.service.PerformanceAnalyzerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/diagnostico")
@RequiredArgsConstructor
public class DiagnosticoController {

    private final PerformanceAnalyzerService performanceAnalyzerService;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<DadosDesempenhoTopico>> getDiagnostico(
            @PathVariable Long usuarioId,
            Authentication authentication) {

        SecurityUtils.verificarOwnership(usuarioId, authentication);
        var desempenho = performanceAnalyzerService.analisarDesempenho(usuarioId);
        return ResponseEntity.ok(desempenho.desempenhoPorTopico());
    }
}
