package com.eduardo.studymind.controller;

import com.eduardo.studymind.dto.input.questao.DadosAtualizacaoQuestao;
import com.eduardo.studymind.dto.input.questao.DadosCadastroQuestao;
import com.eduardo.studymind.dto.output.questao.DadosDetalhamentoQuestao;
import com.eduardo.studymind.dto.output.questao.DadosListagemQuestao;
import com.eduardo.studymind.service.QuestaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


@RestController
@RequestMapping("/questoes")
@RequiredArgsConstructor
public class QuestaoController {

    private final QuestaoService questaoService;

    @PostMapping
    public ResponseEntity<DadosDetalhamentoQuestao> cadastrar(
            @RequestBody @Valid DadosCadastroQuestao dados,
            UriComponentsBuilder uriBuilder) {

        var questao = questaoService.cadastrar(dados);
        var uri = uriBuilder.path("/questoes/{id}").buildAndExpand(questao.id()).toUri();
        return ResponseEntity.created(uri).body(questao);
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemQuestao>> listar(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(questaoService.listar(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoQuestao> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid DadosAtualizacaoQuestao dados) {
        return ResponseEntity.ok(questaoService.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        questaoService.inativar(id);
        return ResponseEntity.noContent().build();
    }
}