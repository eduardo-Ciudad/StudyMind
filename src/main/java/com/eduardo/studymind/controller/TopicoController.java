package com.eduardo.studymind.controller;


import com.eduardo.studymind.domain.materia.MateriaRepository;
import com.eduardo.studymind.dto.input.topico.DadosAtualizacaoTopico;
import com.eduardo.studymind.dto.input.topico.DadosCadastroTopico;
import com.eduardo.studymind.dto.output.topico.DadosDetalhamentoTopico;
import com.eduardo.studymind.dto.output.topico.DadosListagemTopico;
import com.eduardo.studymind.service.TopicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@RestController
@RequestMapping("/topicos")
@RequiredArgsConstructor
public class TopicoController {
    private final TopicoService topicoService;

    @PostMapping
    public ResponseEntity<DadosDetalhamentoTopico> cadastrar (@RequestBody @Valid DadosCadastroTopico dados, UriComponentsBuilder uriBuilder){
        var topico = topicoService.cadastrarTopico(dados);
        var uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.id()).toUri();
        return ResponseEntity.created(uri).body(topico);
    }

    @GetMapping
    public ResponseEntity<List<DadosListagemTopico>> listar (@RequestParam(required = false) Long materiaID){
        return ResponseEntity.ok(topicoService.listarTopicos(materiaID));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoTopico> detalhar (@PathVariable Long id){
        return ResponseEntity.ok(topicoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoTopico> atualizar (@PathVariable Long id, @RequestBody @Valid DadosAtualizacaoTopico dados){
        return ResponseEntity.ok(topicoService.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar (@PathVariable Long id){
        topicoService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
