package com.eduardo.studymind.infra.security;

import com.eduardo.studymind.domain.usuario.Role;
import com.eduardo.studymind.domain.usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;
    private Usuario usuario;

    @BeforeEach
    void setup() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "segredo-de-teste-123");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Eduardo");
        usuario.setEmail("edu@email.com");
        usuario.setRole(Role.ALUNO);
    }

    @Test
    @DisplayName("Deve gerar e validar um token de acesso corretamente")
    void gerarEValidarToken_sucesso() {
        var token = jwtService.gerarToken(usuario);

        assertThat(token).isNotBlank();
        assertThat(jwtService.validarToken(token)).isEqualTo("edu@email.com");
    }

    @Test
    @DisplayName("Deve retornar nulo ao validar um token inválido")
    void validarToken_tokenInvalido() {
        assertThat(jwtService.validarToken("token-aleatorio-invalido")).isNull();
    }

    @Test
    @DisplayName("Deve retornar nulo ao validar token assinado com outro secret")
    void validarToken_secretDiferente() {
        var outroJwtService = new JwtService();
        ReflectionTestUtils.setField(outroJwtService, "secret", "outro-segredo");
        var token = outroJwtService.gerarToken(usuario);

        assertThat(jwtService.validarToken(token)).isNull();
    }

    @Test
    @DisplayName("Deve gerar e validar um refresh token corretamente")
    void gerarEValidarRefreshToken_sucesso() {
        var refreshToken = jwtService.gerarRefreshToken(usuario);

        assertThat(refreshToken).isNotBlank();
        assertThat(jwtService.validarRefreshToken(refreshToken)).isEqualTo("edu@email.com");
    }

    @Test
    @DisplayName("Não deve validar um token de acesso como refresh token")
    void validarRefreshToken_tokenDeAcessoNaoEhAceito() {
        var tokenDeAcesso = jwtService.gerarToken(usuario);

        assertThat(jwtService.validarRefreshToken(tokenDeAcesso)).isNull();
    }

    @Test
    @DisplayName("Não deve validar um refresh token como token de acesso")
    void validarToken_refreshTokenNaoEhAceito() {
        var refreshToken = jwtService.gerarRefreshToken(usuario);

        assertThat(jwtService.validarToken(refreshToken)).isNull();
    }
}
