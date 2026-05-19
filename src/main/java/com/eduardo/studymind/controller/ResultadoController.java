package com.eduardo.studymind.controller;

import com.eduardo.studymind.dto.input.resultado.DadosCadastroResultado;
import com.eduardo.studymind.dto.output.resultado.DadosDetalhamentoResultado;
import com.eduardo.studymind.dto.output.resultado.DadosListagemResultados;
import com.eduardo.studymind.infra.security.SecurityUtils;
import com.eduardo.studymind.service.ResultadoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(name = "Resultados", description = "Registro e consulta de resultados de sessões de estudo")
@RestController
@RequestMapping("/resultados")
@RequiredArgsConstructor
public class ResultadoController {

    private final ResultadoService resultadoService;

    @Operation(summary = "Cadastrar resultado", description = "Registra o resultado de uma sessão de estudo")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Resultado registrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @PostMapping
    public ResponseEntity<DadosDetalhamentoResultado> cadastrar(
            @RequestBody @Valid DadosCadastroResultado dados,
            UriComponentsBuilder uriBuilder) {

        var resultado = resultadoService.cadastrar(dados);
        var uri = uriBuilder.path("/resultados/{id}").buildAndExpand(resultado.id()).toUri();
        return ResponseEntity.created(uri).body(resultado);
    }

    @Operation(summary = "Listar resultados por usuário", description = "Retorna uma página com os resultados de estudo de um usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado ou sem permissão")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<DadosListagemResultados>> listarPorUsuario(
            @PathVariable Long usuarioId,
            @PageableDefault(size = 10) Pageable pageable,
            Authentication authentication) {

        SecurityUtils.verificarOwnership(usuarioId, authentication);
        return ResponseEntity.ok(resultadoService.listarPorUsuario(usuarioId, pageable));
    }

    @Operation(summary = "Detalhar resultado", description = "Retorna os dados detalhados de um resultado pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultado encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado ou sem permissão"),
            @ApiResponse(responseCode = "404", description = "Resultado não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoResultado> detalhar(
            @PathVariable Long id,
            Authentication authentication) {

        var resultado = resultadoService.detalharResultado(id);
        SecurityUtils.verificarOwnership(resultado.usuarioId(), authentication);
        return ResponseEntity.ok(resultado);
    }
}