package com.eduardo.studymind.controller;


import com.eduardo.studymind.domain.plano.PlanoEstudoRepository;
import com.eduardo.studymind.dto.output.plano.DadosPlanoEstudo;
import com.eduardo.studymind.infra.security.Utils.SecurityUtils;
import com.eduardo.studymind.service.PlanoEstudoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plano-estudo/usuario/{usuarioId}")
public class PlanoEstudoController {
    private final PlanoEstudoService planoEstudoService;

    @GetMapping
    public ResponseEntity<DadosPlanoEstudo> getPlano(
            @PathVariable Long usuarioId,
            Authentication authentication
    ) {
        SecurityUtils.verificarOwnership(usuarioId, authentication);
        return ResponseEntity.ok(planoEstudoService.buscarPorUsuario(usuarioId));
    }
}
