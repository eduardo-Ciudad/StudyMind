package com.eduardo.studymind.controller;

import com.eduardo.studymind.dto.output.aulaoutput.DadosAulaOutput;
import com.eduardo.studymind.dto.output.questoesoutput.DadosQuestoesOutput;
import com.eduardo.studymind.dto.output.tarefadescricaooutput.DadosTarefaDescricao;
import com.eduardo.studymind.service.AulaService;
import com.eduardo.studymind.service.TarefaDescricaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Aula", description = "Geração de conteúdo e questões de aula via IA")
@RestController
@RequestMapping("/aula")
@RequiredArgsConstructor
public class AulaController {

    private final AulaService aulaService;
    private final TarefaDescricaoService tarefaDescricaoService;

    @Operation(summary = "Gerar conteúdo de aula por ID do tópico", description = "Gera o conteúdo de uma aula para o tópico informado usando IA")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conteúdo gerado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Tópico não encontrado")
    })
    @GetMapping("/topico/{topicoId}/conteudo")
    public ResponseEntity<DadosAulaOutput> gerarConteudo(@PathVariable Long topicoId) {
        return ResponseEntity.ok(aulaService.gerarConteudoAula(topicoId));
    }

    @Operation(summary = "Gerar questões por ID do tópico", description = "Gera questões de prática para o tópico informado usando IA")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Questões geradas com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Tópico não encontrado")
    })
    @GetMapping("/topico/{topicoId}/questoes")
    public ResponseEntity<DadosQuestoesOutput> gerarQuestoes(
            @PathVariable Long topicoId,
            @RequestParam(defaultValue = "5") int quantidade
    ) {
        return ResponseEntity.ok(aulaService.gerarQuestoes(topicoId, quantidade));
    }

    @Operation(summary = "Gerar descrição de tarefa", description = "Gera uma descrição detalhada para uma tarefa usando IA")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Descrição gerada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    })
    @GetMapping("/tarefa/{tarefaId}/descricao")
    public ResponseEntity<DadosTarefaDescricao> gerarDescricaoTarefa(@PathVariable Long tarefaId) {
        return ResponseEntity.ok(tarefaDescricaoService.gerarDescricao(tarefaId));
    }

    @Operation(summary = "Gerar conteúdo de aula por nome", description = "Gera o conteúdo de uma aula a partir do nome do tópico, matéria e nível de dificuldade")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conteúdo gerado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @GetMapping("/topico/por-nome/conteudo")
    public ResponseEntity<DadosAulaOutput> gerarConteudoPorNome(
            @RequestParam String topicoNome,
            @RequestParam String materiaNome,
            @RequestParam String nivel
    ) {
        return ResponseEntity.ok(aulaService.gerarConteudoPorNome(topicoNome, materiaNome, nivel));
    }

    @Operation(summary = "Gerar questões por nome do tópico", description = "Gera questões de prática a partir do nome do tópico e matéria usando IA")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Questões geradas com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @GetMapping("/topico/por-nome/questoes")
    public ResponseEntity<DadosQuestoesOutput> gerarQuestoesPorNome(
            @RequestParam String topicoNome,
            @RequestParam String materiaNome,
            @RequestParam(defaultValue = "5") int quantidade
    ) {
        return ResponseEntity.ok(aulaService.gerarQuestoesPorNome(topicoNome, materiaNome, quantidade));
    }
}