package com.eduardo.studymind.controller;

import com.eduardo.studymind.dto.output.aulaoutput.DadosAulaOutput;
import com.eduardo.studymind.dto.output.questoesoutput.DadosQuestoesOutput;
import com.eduardo.studymind.dto.output.tarefadescricaooutput.DadosTarefaDescricao;
import com.eduardo.studymind.service.AulaService;
import com.eduardo.studymind.service.TarefaDescricaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/aula")
@RequiredArgsConstructor
public class AulaController {

    private final AulaService aulaService;
    private final TarefaDescricaoService tarefaDescricaoService;

    @GetMapping("/topico/{topicoId}/conteudo")
    public ResponseEntity<DadosAulaOutput> gerarConteudo(@PathVariable Long topicoId) {
        return ResponseEntity.ok(aulaService.gerarConteudoAula(topicoId));
    }

    @GetMapping("/topico/{topicoId}/questoes")
    public ResponseEntity<DadosQuestoesOutput> gerarQuestoes(
            @PathVariable Long topicoId,
            @RequestParam(defaultValue = "5") int quantidade
    ) {
        return ResponseEntity.ok(aulaService.gerarQuestoes(topicoId, quantidade));
    }

    @GetMapping("/tarefa/{tarefaId}/descricao")
    public ResponseEntity<DadosTarefaDescricao> gerarDescricaoTarefa(@PathVariable Long tarefaId) {
        return ResponseEntity.ok(tarefaDescricaoService.gerarDescricao(tarefaId));
    }
}