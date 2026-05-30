package com.eduardo.studymind.service;


import com.eduardo.studymind.domain.usuario.Usuario;
import com.eduardo.studymind.exception.RegrasDeNegocioException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender enviadorEmail;
    @Value("${app.url}")
    private String urlSite;

    private static final String EMAIL_ORIGEM = "studymind@gmail.com";
    private static final String NOME_ENVIADOR = "StudyMind";

    @Async
    public void enviarEmailVerificacao(Usuario usuario, String token){
        String assunto = "Verifique seu email - StudyMind";
        String url = urlSite + "/verificar.html?token=" + token;
        String conteudo = gerarConteudo(usuario.getNome(), url);
        enviarEmail(usuario.getEmail(), assunto, conteudo);
    }

    private void enviarEmail(String emailUsuario, String assunto, String conteudo) {
        MimeMessage message = enviadorEmail.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setFrom(EMAIL_ORIGEM, NOME_ENVIADOR);
            helper.setTo(emailUsuario);
            helper.setSubject(assunto);
            helper.setText(conteudo, true);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RegrasDeNegocioException("Erro ao enviar email de verificação");
        }

        enviadorEmail.send(message);
    }

    private String gerarConteudo(String nome, String url) {
        return "<p>Olá, <strong>" + nome + "</strong>!</p>"
                + "<p>Clique no link abaixo para verificar sua conta:</p>"
                + "<h3><a href=\"" + url + "\">VERIFICAR CONTA</a></h3>"
                + "<p>O link expira em 24 horas.</p>"
                + "<p>StudyMind</p>";
    }

}
