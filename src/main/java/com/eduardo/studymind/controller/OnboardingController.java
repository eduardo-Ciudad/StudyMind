package com.eduardo.studymind.controller;


import com.eduardo.studymind.dto.input.onboarding.DadosMensagemChat;
import com.eduardo.studymind.dto.output.onboarding.DadosRespostaChat;
import com.eduardo.studymind.dto.output.onboarding.DadosStatusOnboarding;
import com.eduardo.studymind.infra.security.SecurityUtils;
import com.eduardo.studymind.service.OnboardingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/onboarding")
public class OnboardingController {

    private final OnboardingService onboardingService;

    @GetMapping("/status/{usuarioId}")
    public ResponseEntity<DadosStatusOnboarding> getStatus(
            @PathVariable Long usuarioId,
            Authentication authentication
    ) {
        SecurityUtils.verificarOwnership(usuarioId, authentication);
        return ResponseEntity.ok(onboardingService.getStatus(usuarioId));

    }

    @PostMapping("/mensagem/{usuarioId}")
    public ResponseEntity<DadosRespostaChat> enviarMensagem(
            @PathVariable Long usuarioId,
            @RequestBody @Valid DadosMensagemChat dados,
            Authentication authentication
            ) {
        SecurityUtils.verificarOwnership(usuarioId, authentication);
        return ResponseEntity.ok(onboardingService.enviarMensagem(usuarioId, dados.mensagem()));
    }

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
