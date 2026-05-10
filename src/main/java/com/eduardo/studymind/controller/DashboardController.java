package com.eduardo.studymind.controller;

import com.eduardo.studymind.dto.output.performace.DadosDesempenhoUsuario;
import com.eduardo.studymind.infra.security.Utils.SecurityUtils;
import com.eduardo.studymind.service.PerformanceAnalyzerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final PerformanceAnalyzerService performanceAnalyzerService;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<DadosDesempenhoUsuario> getDashboard(
            @PathVariable Long usuarioId,
            Authentication authentication) {

        SecurityUtils.verificarOwnership(usuarioId, authentication);
        return ResponseEntity.ok(performanceAnalyzerService.analisarDesempenho(usuarioId));
    }
}