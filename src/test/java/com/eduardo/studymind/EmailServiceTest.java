package com.eduardo.studymind;

import com.eduardo.studymind.domain.usuario.Usuario;
import com.eduardo.studymind.exception.RegrasDeNegocioException;
import com.eduardo.studymind.service.EmailService;
import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender enviadorEmail;

    @InjectMocks
    private EmailService emailService;

    private Usuario usuario;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(emailService, "urlSite", "http://localhost:8080");

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Eduardo");
        usuario.setEmail("edu@email.com");
    }

    @Test
    @DisplayName("Deve enviar email de verificação com sucesso")
    void enviarEmailVerificacao_sucesso() {
        var mimeMessage = new MimeMessage((Session) null);
        when(enviadorEmail.createMimeMessage()).thenReturn(mimeMessage);

        emailService.enviarEmailVerificacao(usuario, "token-123");

        verify(enviadorEmail).send(mimeMessage);
    }

    @Test
    @DisplayName("Deve lançar exceção ao falhar o envio do email")
    void enviarEmailVerificacao_erroAoEnviar() throws MessagingException {
        var mimeMessage = new MimeMessage((Session) null) {
            @Override
            public void setFrom(Address address) throws MessagingException {
                throw new MessagingException("erro ao definir remetente");
            }
        };
        when(enviadorEmail.createMimeMessage()).thenReturn(mimeMessage);

        assertThatThrownBy(() -> emailService.enviarEmailVerificacao(usuario, "token-123"))
                .isInstanceOf(RegrasDeNegocioException.class)
                .hasMessage("Erro ao enviar email de verificação");
    }
}
