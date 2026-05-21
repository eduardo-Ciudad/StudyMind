package com.eduardo.studymind.controller;



import com.eduardo.studymind.dto.input.topico.DadosAtualizacaoTopico;
import com.eduardo.studymind.dto.input.topico.DadosCadastroTopico;
import com.eduardo.studymind.dto.output.topico.DadosDetalhamentoTopico;
import com.eduardo.studymind.dto.output.topico.DadosListagemTopico;
import com.eduardo.studymind.infra.security.SecurityUtils;
import com.eduardo.studymind.service.TopicoService;
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

@Tag(name = "Tópicos", description = "Gerenciamento de tópicos das matérias")
@RestController
@RequestMapping("/topicos")
@RequiredArgsConstructor
public class TopicoController {

    private final TopicoService topicoService;

    @Operation(summary = "Cadastrar tópico", description = "Cria um novo tópico associado a uma matéria do usuário autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tópico criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @PostMapping
    public ResponseEntity<DadosDetalhamentoTopico> cadastrar(
            @RequestBody @Valid DadosCadastroTopico dados,
            Authentication authentication,
            UriComponentsBuilder uriBuilder) {
        var usuario = SecurityUtils.getUsuarioAuthenticado(authentication);
        var topico = topicoService.cadastrarTopico(dados, usuario.getId());
        var uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.id()).toUri();
        return ResponseEntity.created(uri).body(topico);
    }

    @Operation(summary = "Listar tópicos", description = "Retorna os tópicos do usuário autenticado, opcionalmente filtrados por matéria")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    @GetMapping
    public ResponseEntity<List<DadosListagemTopico>> listar(
            @RequestParam(required = false) Long materiaID,
            Authentication authentication) {
        var usuario = SecurityUtils.getUsuarioAuthenticado(authentication);
        return ResponseEntity.ok(topicoService.listarTopicos(materiaID, usuario.getId()));
    }

    @Operation(summary = "Detalhar tópico", description = "Retorna os dados detalhados de um tópico pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tópico encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Tópico não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoTopico> detalhar(@PathVariable Long id) {
        return ResponseEntity.ok(topicoService.buscarPorId(id));
    }

    @Operation(summary = "Atualizar tópico", description = "Atualiza os dados de um tópico existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tópico atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Tópico não encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DadosDetalhamentoTopico> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid DadosAtualizacaoTopico dados,
            Authentication authentication) {
        SecurityUtils.verificarOwnership(topicoService.buscarDono(id), authentication);
        return ResponseEntity.ok(topicoService.atualizar(id, dados));
    }

    @Operation(summary = "Desativar tópico", description = "Desativa um tópico pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Tópico desativado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Tópico não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id, Authentication authentication) {
        SecurityUtils.verificarOwnership(topicoService.buscarDono(id), authentication);
        topicoService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}