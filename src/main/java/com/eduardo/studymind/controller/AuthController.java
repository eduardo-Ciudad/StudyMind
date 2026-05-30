package com.eduardo.studymind.controller;


import com.eduardo.studymind.domain.usuario.Usuario;
import com.eduardo.studymind.dto.input.login.DadosLogin;
import com.eduardo.studymind.dto.input.usuario.DadosCadastroUsuario;
import com.eduardo.studymind.dto.output.jwt.DadosTokenJwt;
import com.eduardo.studymind.dto.output.usuario.DadosDetalhamentoUsuario;
import com.eduardo.studymind.infra.security.JwtService;
import com.eduardo.studymind.service.TokenVerificacaoService;
import com.eduardo.studymind.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticação", description = "Endpoints para autenticação e registro de usuários")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;
    private final TokenVerificacaoService tokenVerificacaoService;

    @Operation(summary = "Realizar login", description = "Autentica o usuário com email e senha e retorna um token JWT")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<DadosTokenJwt> login(@RequestBody @Valid DadosLogin dados) {
        var authToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
        var authentication = authenticationManager.authenticate(authToken);
        var usuario = (Usuario) authentication.getPrincipal();
        var token = jwtService.gerarToken(usuario);
        return ResponseEntity.ok(new DadosTokenJwt(token));
    }

    @Operation(summary = "Registrar novo usuário", description = "Cria uma nova conta de usuário na plataforma")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos")
    })
    @PostMapping("/registro")
    public ResponseEntity<DadosDetalhamentoUsuario> registrar(@RequestBody @Valid DadosCadastroUsuario dados) {
        var usuario = usuarioService.cadastrarUsuario(dados);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @Operation(summary = "Verificar email", description = "Ativa a conta do usuário a partir do token enviado por email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email verificado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Token inválido ou expirado")
    })
    @GetMapping("/verificar")
    public ResponseEntity<String> verificar(@RequestParam String token) {
        tokenVerificacaoService.verificarToken(token);
        return ResponseEntity.ok("Email verificado com sucesso! Você já pode fazer login.");
    }


}
