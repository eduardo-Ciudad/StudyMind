package com.eduardo.studymind.service;

import com.eduardo.studymind.domain.token.TokenVerificacao;
import com.eduardo.studymind.domain.token.TokenVerificacaoRepository;
import com.eduardo.studymind.domain.usuario.Usuario;
import com.eduardo.studymind.domain.usuario.UsuarioRepository;
import com.eduardo.studymind.exception.RegrasDeNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenVerificacaoService {
    private final TokenVerificacaoRepository tokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;

    @Transactional
    public void gerarEEnviarToken(Usuario usuario){
        TokenVerificacao token = new TokenVerificacao(usuario);
        tokenRepository.save(token);
        emailService.enviarEmailVerificacao(usuario, token.getToken());
    }

    @Transactional
    public void verificarToken(String tokenString){
        TokenVerificacao token = tokenRepository.findByToken(tokenString)
                .orElseThrow(() -> new RegrasDeNegocioException("Token inválido"));

        if (token.isExpirado()) {
            throw new RegrasDeNegocioException("Token expirado");
        }

        if (token.getUtilizado()) {
            throw new RegrasDeNegocioException("Token já utilizado");
        }

        token.getUsuario().setAtivo(true);
        token.marcarComoUtilizado();
    }

}


