package com.eduardo.studymind.controller;

import com.eduardo.studymind.dto.input.materia.DadosAtualizacaoMateria;
import com.eduardo.studymind.dto.input.materia.DadosCadastroMateria;
import com.eduardo.studymind.dto.output.materia.DadosDetalhamentoMateria;
import com.eduardo.studymind.dto.output.materia.DadosListagemMateria;
import com.eduardo.studymind.infra.security.SecurityUtils;
import com.eduardo.studymind.service.MateriaService;
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

@Tag(name = "Matérias", description = "Gerenciamento de matérias do usuário")
@RestController
@RequestMapping("/materias")
@RequiredArgsConstructor
public class MateriaController {

    private final MateriaService materiaService;

    @Operation(summary = "Cadastrar matéria", description = "Cria uma nova matéria associada ao usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Matéria criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @PostMapping
    public ResponseEntity<DadosDetalhamentoMateria> cadastrar(
            @RequestBody @Valid DadosCadastroMateria dados,
            Authentication authentication,
            UriComponentsBuilder uriBuilder) {
        var usuario = SecurityUtils.getUsuarioAuthenticado(authentication);
        var materia = materiaService.cadastrarMateria(dados, usuario.getId());
        var uri = uriBuilder.path("/materias/{id}").buildAndExpand(materia.id()).toUri();
        return ResponseEntity.created(uri).body(materia);
    }

    @Operation(summary = "Listar matérias", description = "Retorna todas as matérias do usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @GetMapping
    public ResponseEntity<List<DadosListagemMateria>> listar(Authentication authentication) {
        var usuario = SecurityUtils.getUsuarioAuthenticado(authentication);
        return ResponseEntity.ok(materiaService.listarMateria(usuario.getId()));
    }

    @Operation(summary = "Buscar matéria por ID", description = "Retorna os dados detalhados de uma matéria pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Matéria encontrada"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Matéria não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoMateria> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(materiaService.buscarPorID(id));
    }

    @Operation(summary = "Atualizar matéria", description = "Atualiza os dados de uma matéria existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Matéria atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Matéria não encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoMateria> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid DadosAtualizacaoMateria dados,
            Authentication authentication) {
        SecurityUtils.verificarOwnership(materiaService.buscarDono(id), authentication);
        return ResponseEntity.ok(materiaService.atualizarMateria(id, dados));
    }

    @Operation(summary = "Desativar matéria", description = "Desativa uma matéria pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Matéria desativada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Matéria não encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id, Authentication authentication) {
        SecurityUtils.verificarOwnership(materiaService.buscarDono(id), authentication);
        materiaService.desativarMateria(id);
        return ResponseEntity.noContent().build();
    }
}