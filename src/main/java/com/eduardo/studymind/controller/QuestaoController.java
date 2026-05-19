package com.eduardo.studymind.controller;

import com.eduardo.studymind.dto.input.questao.DadosAtualizacaoQuestao;
import com.eduardo.studymind.dto.input.questao.DadosCadastroQuestao;
import com.eduardo.studymind.dto.output.questao.DadosDetalhamentoQuestao;
import com.eduardo.studymind.dto.output.questao.DadosListagemQuestao;
import com.eduardo.studymind.service.QuestaoService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(name = "Questões", description = "Gerenciamento de questões de estudo")
@RestController
@RequestMapping("/questoes")
@RequiredArgsConstructor
public class QuestaoController {

    private final QuestaoService questaoService;

    @Operation(summary = "Cadastrar questão", description = "Cria uma nova questão de estudo")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Questão criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @PostMapping
    public ResponseEntity<DadosDetalhamentoQuestao> cadastrar(
            @RequestBody @Valid DadosCadastroQuestao dados,
            UriComponentsBuilder uriBuilder) {

        var questao = questaoService.cadastrar(dados);
        var uri = uriBuilder.path("/questoes/{id}").buildAndExpand(questao.id()).toUri();
        return ResponseEntity.created(uri).body(questao);
    }

    @Operation(summary = "Listar questões", description = "Retorna uma página com todas as questões cadastradas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @GetMapping
    public ResponseEntity<Page<DadosListagemQuestao>> listar(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(questaoService.listar(pageable));
    }

    @Operation(summary = "Atualizar questão", description = "Atualiza os dados de uma questão existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Questão atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Questão não encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoQuestao> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid DadosAtualizacaoQuestao dados) {
        return ResponseEntity.ok(questaoService.atualizar(id, dados));
    }

    @Operation(summary = "Inativar questão", description = "Inativa uma questão pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Questão inativada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Questão não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        questaoService.inativar(id);
        return ResponseEntity.noContent().build();
    }
}