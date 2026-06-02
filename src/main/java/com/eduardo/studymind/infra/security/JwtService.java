package com.eduardo.studymind.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.eduardo.studymind.domain.usuario.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Value("${api.security.token.secret}")
    private String secret;

    private static final long EXPIRATION_TIME = 2 * 60 * 60 * 1000; // 2 horas

    public String gerarToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("studymind-api")
                    .withSubject(usuario.getEmail())
                    .withClaim("role", usuario.getRole().name())
                    .withClaim("id", usuario.getId())
                    .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new RuntimeException("Erro ao gerar token JWT", e);
        }
    }

    public String validarToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("studymind-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            return null;
        }
    }

    private static final long REFRESH_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000L; // 7 dias

    public String gerarRefreshToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("studymind-api")
                    .withSubject(usuario.getEmail())
                    .withClaim("tipo", "refresh")
                    .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME))
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new RuntimeException("Erro ao gerar refresh token JWT", e);
        }
    }

    public String validarRefreshToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            DecodedJWT jwt = JWT.require(algorithm)
                    .withIssuer("studymind-api")
                    .withClaim("tipo", "refresh")
                    .build()
                    .verify(token);
            return jwt.getSubject();
        } catch (JWTVerificationException e) {
            return null;
        }
    }
}
