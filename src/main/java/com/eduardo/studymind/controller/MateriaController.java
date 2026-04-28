package com.eduardo.studymind.controller;

import com.eduardo.studymind.domain.materia.MateriaRepository;
import com.eduardo.studymind.dto.input.materia.DadosCadastroMateria;
import com.eduardo.studymind.dto.output.materia.DadosDetalhamentoMateria;
import com.eduardo.studymind.dto.output.materia.DadosListagemMateria;
import com.eduardo.studymind.service.MateriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/materias")
@RequiredArgsConstructor
public class MateriaController {
    private final MateriaService materiaService;

    @PostMapping
    public ResponseEntity<DadosDetalhamentoMateria> cadastrar (@RequestBody @Valid DadosCadastroMateria dados, UriComponentsBuilder uriBuilder){
        var materia = materiaService.cadastrarMateria(dados);
        var uri = uriBuilder.path("/materias/{id}").buildAndExpand(materia.id()).toUri();
        return ResponseEntity.created(uri).body(materia);
    }

    @GetMapping
    public ResponseEntity<List<DadosListagemMateria>> listar(){
        return ResponseEntity.ok(materiaService.listarMateria());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoMateria> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(materiaService.buscarPorID(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id){
        materiaService.desativarMateria(id);
        return ResponseEntity.noContent().build();
    }
}
