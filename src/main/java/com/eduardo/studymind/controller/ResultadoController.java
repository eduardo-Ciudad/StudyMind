package com.eduardo.studymind.controller;

import com.eduardo.studymind.dto.input.resultado.DadosCadastroResultado;
import com.eduardo.studymind.dto.output.resultado.DadosDetalhamentoResultado;
import com.eduardo.studymind.dto.output.resultado.DadosListagemResultados;
import com.eduardo.studymind.service.ResultadoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/resultados")
@RequiredArgsConstructor
public class ResultadoController {

    private final ResultadoService resultadoService;

    @PostMapping
    public ResponseEntity<DadosDetalhamentoResultado> cadastrar(
            @RequestBody @Valid DadosCadastroResultado dados,
            UriComponentsBuilder uriBuilder) {

        var resultado = resultadoService.cadastrar(dados);
        var uri = uriBuilder.path("/resultados/{id}").buildAndExpand(resultado.id()).toUri();
        return ResponseEntity.created(uri).body(resultado);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<DadosListagemResultados>> listarPorUsuario(
            @PathVariable Long usuarioId,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(resultadoService.listarPorUsuario(usuarioId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoResultado> detalhar(@PathVariable Long id) {
        return ResponseEntity.ok(resultadoService.detalharResultado(id));
    }
}