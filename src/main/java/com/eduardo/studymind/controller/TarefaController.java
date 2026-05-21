package com.eduardo.studymind.controller;

import com.eduardo.studymind.domain.tarefa.TarefaStatus;
import com.eduardo.studymind.dto.input.tarefa.DadosAtualizacaoTarefa;
import com.eduardo.studymind.dto.input.tarefa.DadosCadastroTarefa;
import com.eduardo.studymind.dto.output.tarefa.DadosDetalhamentoTarefa;
import com.eduardo.studymind.dto.output.tarefa.DadosListagemTarefa;
import com.eduardo.studymind.infra.security.SecurityUtils;
import com.eduardo.studymind.service.TarefaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Tag(name = "Tarefas", description = "Gerenciamento de tarefas de estudo")
@RestController
@RequestMapping("/tarefas")
@RequiredArgsConstructor
public class TarefaController {

    private final TarefaService tarefaService;

    @Operation(summary = "Cadastrar tarefa", description = "Cria uma nova tarefa de estudo")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @PostMapping
    public ResponseEntity<DadosDetalhamentoTarefa> cadastrar(
            @RequestBody @Valid DadosCadastroTarefa dados,
            UriComponentsBuilder uriBuilder) {

        var tarefa = tarefaService.cadastrar(dados);
        var uri = uriBuilder.path("/tarefas/{id}").buildAndExpand(tarefa.id()).toUri();
        return ResponseEntity.created(uri).body(tarefa);
    }

    @Operation(summary = "Listar tarefas por usuário", description = "Retorna as tarefas de um usuário, opcionalmente filtradas por status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado ou sem permissão")
    })
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<DadosListagemTarefa>> listarPorUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(required = false) TarefaStatus status,
            Authentication authentication) {

        SecurityUtils.verificarOwnership(usuarioId, authentication);
        return ResponseEntity.ok(tarefaService.listarPorUsuario(usuarioId, status));
    }

    @Operation(summary = "Atualizar tarefa", description = "Atualiza os dados de uma tarefa existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tarefa atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoTarefa> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid DadosAtualizacaoTarefa dados,
            Authentication authentication) {
        SecurityUtils.verificarOwnership(tarefaService.buscarDono(id), authentication);
        return ResponseEntity.ok(tarefaService.atualizarTarefa(id, dados));
    }

    @Operation(summary = "Cancelar tarefa", description = "Cancela uma tarefa pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tarefa cancelada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id, Authentication authentication) {
        SecurityUtils.verificarOwnership(tarefaService.buscarDono(id), authentication);
        tarefaService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}