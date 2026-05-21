package com.eduardo.studymind.controller;


import com.eduardo.studymind.dto.output.plano.DadosPlanoEstudo;
import com.eduardo.studymind.infra.security.SecurityUtils;
import com.eduardo.studymind.service.PlanoEstudoService;
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

@Tag(name = "Plano de Estudo", description = "Consulta do plano de estudo personalizado do usuário")
@RestController
@RequiredArgsConstructor
@RequestMapping("/plano-estudo/usuario/{usuarioId}")
public class PlanoEstudoController {
    private final PlanoEstudoService planoEstudoService;

    @Operation(summary = "Buscar plano de estudo", description = "Retorna o plano de estudo personalizado do usuário informado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plano de estudo retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado ou sem permissão"),
            @ApiResponse(responseCode = "404", description = "Plano de estudo não encontrado para o usuário")
    })
    @GetMapping
    public ResponseEntity<DadosPlanoEstudo> getPlano(
            @PathVariable Long usuarioId,
            Authentication authentication
    ) {
        SecurityUtils.verificarOwnership(usuarioId, authentication);
        return ResponseEntity.ok(planoEstudoService.buscarPorUsuario(usuarioId));
    }

    @Operation(summary = "Buscar histórico de planos", description = "Retorna todos os planos de estudo do usuário ordenados por versão")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado ou sem permissão")
    })
    @GetMapping("/historico")
    public ResponseEntity<List<DadosPlanoEstudo>> getHistorico(
            @PathVariable Long usuarioId,
            Authentication authentication
    ) {
        SecurityUtils.verificarOwnership(usuarioId, authentication);
        return ResponseEntity.ok(planoEstudoService.buscarHistoricoPorUsuario(usuarioId));
    }
}
