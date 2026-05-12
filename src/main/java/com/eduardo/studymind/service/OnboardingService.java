package com.eduardo.studymind.service;

import com.eduardo.studymind.domain.chat.ChatMensagem;
import com.eduardo.studymind.domain.chat.ChatMensagemRepository;
import com.eduardo.studymind.domain.chat.RoleChat;
import com.eduardo.studymind.domain.plano.PlanoEstudo;
import com.eduardo.studymind.domain.plano.PlanoEstudoRepository;
import com.eduardo.studymind.domain.usuario.Usuario;
import com.eduardo.studymind.domain.usuario.UsuarioRepository;
import com.eduardo.studymind.dto.output.onboarding.DadosRespostaChat;
import com.eduardo.studymind.dto.output.onboarding.DadosStatusOnboarding;
import com.eduardo.studymind.exception.RecursoNaoEncontradoException;
import com.eduardo.studymind.exception.RegrasDeNegocioException;
import com.eduardo.studymind.infra.ia.AIClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OnboardingService {

    private final ChatMensagemRepository chatMensagemRepository;
    private final PlanoEstudoRepository planoEstudoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AIClient aiClient;
    private final ObjectMapper objectMapper;

    private static final String SYSTEM_PROMPT = """
        Você é um assistente educacional do StudyMind, especializado em vestibular brasileiro.
        Seu objetivo é conhecer o aluno e criar um plano de estudos personalizado.
        
        Conduza uma conversa natural e colete as seguintes informações:
        1. Qual vestibular o aluno quer passar (FUVEST, ENEM, UNICAMP, etc.)
        2. Data prevista da prova
        3. Matérias que precisa estudar
        4. Nível atual em cada matéria (fraco, médio, bom)
        5. Quantas horas por dia pode estudar
        6. Pontos fracos e fortes
        
        Faça uma pergunta por vez. Seja amigável e encorajador.
        
        Quando tiver todas as informações, responda em UMA ÚNICA mensagem assim:
        - Primeira linha: exatamente a palavra ONBOARDING_COMPLETO
        - Segunda linha: o JSON completo em uma única linha, sem markdown, sem ```
        
        Exemplo:
        ONBOARDING_COMPLETO
        {"vestibular":"ENEM","dataProva":"2026-11-01","horasPorDia":3,"pontosFracos":["Química"],"pontoFortes":["Matemática"],"semanas":[{"semana":1,"foco":"Química Básica","topicos":["Tabela periódica"],"metaQuestoes":15}],"dicaGeral":"Foco total!"}
        
        O JSON deve ter no máximo 4 semanas e seguir exatamente essa estrutura.
        """;

    @Transactional(readOnly = true)
    public DadosStatusOnboarding getStatus(Long usuarioId) {
        var usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));
        return new DadosStatusOnboarding(usuarioId, usuario.getOnboardingConcluido()
        );
    }


    @Transactional
    public DadosRespostaChat enviarMensagem(Long usuarioId, String mensagemUsuario) {
        var usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));

        if (usuario.getOnboardingConcluido()) {
            throw new RegrasDeNegocioException("Onboarding já foi concluído");
        }


        // salva mensagem do usuário
        var msgUsuario = new ChatMensagem();
        msgUsuario.setUsuario(usuario);
        msgUsuario.setRole(RoleChat.USER);
        msgUsuario.setConteudo(mensagemUsuario);
        chatMensagemRepository.save(msgUsuario);

        // busca histórico e monta contexto
        var historico = chatMensagemRepository.findAllByUsuarioIdOrderByCriadoEmAsc(usuarioId);
        var prompt = montarPromptComHistorico(historico);

        //chama a ia
        var respostaIA = aiClient.gerarResposta(prompt);


        // verifica se onboarding foi concluído
        boolean concluido = respostaIA.contains("ONBOARDING_COMPLETO");
        String respostaLimpa = respostaIA.replace("ONBOARDING_COMPLETO", "").trim();

        if (concluido) {
            salvarPlanoEFinalizar(usuario, respostaLimpa);
            respostaLimpa = respostaLimpa.substring(0, respostaLimpa.indexOf("{")).trim();
            if (respostaLimpa.isEmpty()) {
                respostaLimpa = "Perfeito! Seu plano de estudos personalizado foi criado. Vamos começar!";
            }
        }

        var msgIA = new ChatMensagem();
        msgIA.setUsuario(usuario);
        msgIA.setRole(RoleChat.ASSISTANT);
        msgIA.setConteudo(respostaLimpa);
        chatMensagemRepository.save(msgIA);

        return new DadosRespostaChat(respostaLimpa, concluido);
    }

    private String montarPromptComHistorico(List<ChatMensagem> historico) {
        var sb = new StringBuilder();
        sb.append(SYSTEM_PROMPT).append("\n\n");
        sb.append("Histórico da conversa:\n");

        for (var msg : historico) {
            var role = msg.getRole() == RoleChat.USER ? "Aluno" : "Assistente";
            sb.append(role).append(": ").append(msg.getConteudo()).append("\n");
        }

        sb.append("\nAssistente:");
        return sb.toString();
    }

    private void salvarPlanoEFinalizar(Usuario usuario, String respostaComJson) {
        try {
            var inicioJson = respostaComJson.indexOf("{");
            if (inicioJson == -1) {
                throw new RuntimeException("IA não retornou JSON válido no onboarding");
            }
            var fimJson = respostaComJson.lastIndexOf("}") + 1;
            var json = respostaComJson.substring(inicioJson, fimJson).trim();

            // valida se é JSON válido
            objectMapper.readTree(json);

            var plano = new PlanoEstudo();
            plano.setUsuario(usuario);
            plano.setConteudoJson(json);
            planoEstudoRepository.save(plano);

            usuario.setOnboardingConcluido(true);
            usuarioRepository.save(usuario);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar plano de estudos", e);
        }
    }

}