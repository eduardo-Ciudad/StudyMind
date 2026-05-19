package com.eduardo.studymind.controller;


import com.eduardo.studymind.domain.usuario.Usuario;
import com.eduardo.studymind.dto.input.login.DadosLogin;
import com.eduardo.studymind.dto.input.usuario.DadosCadastroUsuario;
import com.eduardo.studymind.dto.output.jwt.DadosTokenJwt;
import com.eduardo.studymind.dto.output.usuario.DadosDetalhamentoUsuario;
import com.eduardo.studymind.infra.security.JwtService;
import com.eduardo.studymind.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<DadosTokenJwt> login(@RequestBody @Valid DadosLogin dados) {
        var authToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
        var authentication = authenticationManager.authenticate(authToken);
        var usuario = (Usuario) authentication.getPrincipal();
        var token = jwtService.gerarToken(usuario);
        return ResponseEntity.ok(new DadosTokenJwt(token));
    }

    @PostMapping("/registro")
    public ResponseEntity<DadosDetalhamentoUsuario> registrar(@RequestBody @Valid DadosCadastroUsuario dados) {
        var usuario = usuarioService.cadastrarUsuario(dados);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }
}
