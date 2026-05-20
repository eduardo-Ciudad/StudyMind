package com.eduardo.studymind.controller;

import com.eduardo.studymind.dto.input.resultado.DadosCadastroResultadoSessao;
import com.eduardo.studymind.dto.output.resultado.DadosResultadoSessaoOutput;
import com.eduardo.studymind.service.ResultadoSessaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Resultado de Sessão", description = "Registro de resultados de sessões de estudo com questões")
@RestController
@RequestMapping("/resultado-sessao")
@RequiredArgsConstructor
public class ResultadoSessaoController {

    private final ResultadoSessaoService service;

    @Operation(summary = "Salvar resultado de sessão", description = "Registra o resultado de uma sessão de estudo com as respostas do usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Resultado da sessão salvo com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @PostMapping
    public ResponseEntity<DadosResultadoSessaoOutput> salvar(
            @RequestBody @Valid DadosCadastroResultadoSessao dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(dados));
    }

    @Operation(summary = "Listar resultados de sessão por usuário", description = "Retorna todas as sessões de estudo registradas do usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })


    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<DadosResultadoSessaoOutput>> listarPorUsuario(
            @PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.listarPorUsuario(usuarioId));
    }
}
