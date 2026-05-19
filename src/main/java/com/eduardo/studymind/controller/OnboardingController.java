package com.eduardo.studymind.controller;


import com.eduardo.studymind.dto.input.onboarding.DadosMensagemChat;
import com.eduardo.studymind.dto.output.onboarding.DadosRespostaChat;
import com.eduardo.studymind.dto.output.onboarding.DadosStatusOnboarding;
import com.eduardo.studymind.infra.security.SecurityUtils;
import com.eduardo.studymind.service.OnboardingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Onboarding", description = "Gerenciamento do processo de onboarding guiado por IA")
@RestController
@RequiredArgsConstructor
@RequestMapping("/onboarding")
public class OnboardingController {

    private final OnboardingService onboardingService;

    @Operation(summary = "Obter status do onboarding", description = "Retorna o status atual do processo de onboarding do usuário")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado ou sem permissão"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/status/{usuarioId}")
    public ResponseEntity<DadosStatusOnboarding> getStatus(
            @PathVariable Long usuarioId,
            Authentication authentication
    ) {
        SecurityUtils.verificarOwnership(usuarioId, authentication);
        return ResponseEntity.ok(onboardingService.getStatus(usuarioId));

    }

    @Operation(summary = "Enviar mensagem no onboarding", description = "Envia uma mensagem ao assistente de onboarding e recebe uma resposta")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mensagem processada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado ou sem permissão")
    })
    @PostMapping("/mensagem/{usuarioId}")
    public ResponseEntity<DadosRespostaChat> enviarMensagem(
            @PathVariable Long usuarioId,
            @RequestBody @Valid DadosMensagemChat dados,
            Authentication authentication
            ) {
        SecurityUtils.verificarOwnership(usuarioId, authentication);
        return ResponseEntity.ok(onboardingService.enviarMensagem(usuarioId, dados.mensagem()));
    }

    @Operation(summary = "Enviar mensagem de review no onboarding", description = "Envia uma mensagem para a etapa de revisão do onboarding")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mensagem de review processada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado ou sem permissão")
    })
    @PostMapping("/review/{usuarioId}")
    public ResponseEntity<DadosRespostaChat> enviarMensagemReview(
            @PathVariable Long usuarioId,
            @RequestBody @Valid DadosMensagemChat dados,
            Authentication authentication
    ) {
        SecurityUtils.verificarOwnership(usuarioId, authentication);
        return ResponseEntity.ok(onboardingService.enviarMensagemReview(usuarioId, dados.mensagem()));
    }

}
