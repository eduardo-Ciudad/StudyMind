package com.eduardo.studymind.controller;

import com.eduardo.studymind.domain.tarefa.TarefaStatus;
import com.eduardo.studymind.dto.input.tarefa.DadosAtualizacaoTarefa;
import com.eduardo.studymind.dto.input.tarefa.DadosCadastroTarefa;
import com.eduardo.studymind.dto.output.tarefa.DadosDetalhamentoTarefa;
import com.eduardo.studymind.dto.output.tarefa.DadosListagemTarefa;
import com.eduardo.studymind.service.TarefaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/tarefas")
@RequiredArgsConstructor
public class TarefaController {

    private final TarefaService tarefaService;

    @PostMapping
    public ResponseEntity<DadosDetalhamentoTarefa> cadastrar(
            @RequestBody @Valid DadosCadastroTarefa dados,
            UriComponentsBuilder uriBuilder) {

        var tarefa = tarefaService.cadastrar(dados);
        var uri = uriBuilder.path("/tarefas/{id}").buildAndExpand(tarefa.id()).toUri();
        return ResponseEntity.created(uri).body(tarefa);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<DadosListagemTarefa>> listarPorUsuario(
            @PathVariable Long usuarioId,
            @RequestParam(required = false) TarefaStatus status) {
        return ResponseEntity.ok(tarefaService.listarPorUsuario(usuarioId, status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoTarefa> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid DadosAtualizacaoTarefa dados) {
        return ResponseEntity.ok(tarefaService.atualizarTarefa(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        tarefaService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}