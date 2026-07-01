package com.eduardo.studymind;

import com.eduardo.studymind.domain.token.TokenVerificacao;
import com.eduardo.studymind.domain.token.TokenVerificacaoRepository;
import com.eduardo.studymind.domain.usuario.Usuario;
import com.eduardo.studymind.domain.usuario.UsuarioRepository;
import com.eduardo.studymind.exception.RegrasDeNegocioException;
import com.eduardo.studymind.service.EmailService;
import com.eduardo.studymind.service.TokenVerificacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenVerificacaoServiceTest {

    @Mock
    private TokenVerificacaoRepository tokenRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private TokenVerificacaoService tokenVerificacaoService;

    private Usuario usuario;

    @BeforeEach
    void setup() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Eduardo");
        usuario.setEmail("edu@email.com");
        usuario.setAtivo(false);
    }

    @Test
    @DisplayName("Deve gerar e enviar token de verificação")
    void gerarEEnviarToken_sucesso() {
        tokenVerificacaoService.gerarEEnviarToken(usuario);

        verify(tokenRepository).save(any(TokenVerificacao.class));
        verify(emailService).enviarEmailVerificacao(eq(usuario), any(String.class));
    }

    @Test
    @DisplayName("Deve ativar a conta ao verificar um token válido")
    void verificarToken_sucesso() {
        var token = new TokenVerificacao(usuario);
        when(tokenRepository.findByToken("token-valido")).thenReturn(Optional.of(token));

        tokenVerificacaoService.verificarToken("token-valido");

        assertThat(usuario.getAtivo()).isTrue();
        assertThat(token.getUtilizado()).isTrue();
    }

    @Test
    @DisplayName("Deve lançar exceção ao verificar token inexistente")
    void verificarToken_tokenInvalido() {
        when(tokenRepository.findByToken("token-inexistente")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tokenVerificacaoService.verificarToken("token-inexistente"))
                .isInstanceOf(RegrasDeNegocioException.class)
                .hasMessage("Token inválido");
    }

    @Test
    @DisplayName("Deve lançar exceção ao verificar token expirado")
    void verificarToken_tokenExpirado() {
        var token = new TokenVerificacao(usuario);
        ReflectionTestUtils.setField(token, "expiracao", LocalDateTime.now().minusHours(1));

        when(tokenRepository.findByToken("token-expirado")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> tokenVerificacaoService.verificarToken("token-expirado"))
                .isInstanceOf(RegrasDeNegocioException.class)
                .hasMessage("Token expirado");
    }

    @Test
    @DisplayName("Deve lançar exceção ao verificar token já utilizado")
    void verificarToken_tokenJaUtilizado() {
        var token = new TokenVerificacao(usuario);
        token.marcarComoUtilizado();

        when(tokenRepository.findByToken("token-usado")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> tokenVerificacaoService.verificarToken("token-usado"))
                .isInstanceOf(RegrasDeNegocioException.class)
                .hasMessage("Token já utilizado");
    }
}
