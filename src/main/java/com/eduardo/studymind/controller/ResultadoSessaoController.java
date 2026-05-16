package com.eduardo.studymind.controller;

import com.eduardo.studymind.dto.input.resultado.DadosCadastroResultadoSessao;
import com.eduardo.studymind.dto.output.resultado.DadosResultadoSessaoOutput;
import com.eduardo.studymind.service.ResultadoSessaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resultado-sessao")
@RequiredArgsConstructor
public class ResultadoSessaoController {

    private final ResultadoSessaoService service;

    @PostMapping
    public ResponseEntity<DadosResultadoSessaoOutput> salvar(
            @RequestBody @Valid DadosCadastroResultadoSessao dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(dados));
    }
}
