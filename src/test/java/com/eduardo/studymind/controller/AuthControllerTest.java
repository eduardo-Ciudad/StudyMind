package com.eduardo.studymind.controller;

import com.eduardo.studymind.domain.usuario.Role;
import com.eduardo.studymind.domain.usuario.Usuario;
import com.eduardo.studymind.dto.input.login.DadosLogin;
import com.eduardo.studymind.dto.input.usuario.DadosCadastroUsuario;
import com.eduardo.studymind.dto.output.usuario.DadosDetalhamentoUsuario;
import com.eduardo.studymind.infra.security.JwtService;
import com.eduardo.studymind.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioService usuarioService;

    // remove UserDetailsServiceImpl e AuthFilter — não precisa mais

    @Test
    @DisplayName("Deve retornar token JWT ao fazer login com credenciais válidas")
    void login_sucesso() throws Exception {
        var dados = new DadosLogin("edu@email.com", "senha123");

        var usuario = new Usuario();
        usuario.setEmail("edu@email.com");

        var authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(usuario);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.gerarToken(usuario)).thenReturn("token-jwt-fake");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-jwt-fake"));
    }

    @Test
    @DisplayName("Deve retornar 400 ao fazer login sem email")
    void login_semEmail() throws Exception {
        var dados = new DadosLogin("", "senha123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 201 ao registrar usuário válido")
    void registro_sucesso() throws Exception {
        var dados = new DadosCadastroUsuario("Eduardo", "edu@email.com", "senha123");

        var detalhamento = new DadosDetalhamentoUsuario(1L, "Eduardo", "edu@email.com", Role.ALUNO, true, null);
        when(usuarioService.cadastrarUsuario(any())).thenReturn(detalhamento);

        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("edu@email.com"));
    }

    @Test
    @DisplayName("Deve retornar 400 ao registrar com senha fraca")
    void registro_senhaFraca() throws Exception {
        var dados = new DadosCadastroUsuario("Eduardo", "edu@email.com", "123");

        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dados)))
                .andExpect(status().isBadRequest());
    }
}