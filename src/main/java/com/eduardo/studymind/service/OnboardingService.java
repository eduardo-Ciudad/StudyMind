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
import com.eduardo.studymind.service.parser.PlanoEstudoParser;
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
    private final PlanoEstudoParser  planoEstudoParser;

    private static final String SYSTEM_PROMPT_ONBOARDING = """
            Você é um assistente educacional do StudyMind, especializado em vestibular brasileiro.
            Seu objetivo é conhecer o aluno e criar um plano de estudos personalizado de 12 semanas.

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

            O JSON deve seguir EXATAMENTE essa estrutura:
            ONBOARDING_COMPLETO
            {"vestibular":"FUVEST","dataExame":"2026-11-15","horasPorDia":3,"versao":1,"materias":[{"nome":"Matemática","descricao":"Matemática para vestibular","topicos":[{"nome":"Funções","nivel":"MEDIO","descricao":"Funções quadráticas e exponenciais"}]}],"semanas":[{"numero":1,"tarefas":[{"topicoNome":"Funções","materiaNome":"Matemática","tipo":"REVISAO","descricao":"Revisar conceitos de funções quadráticas","meta":10}]}]}

            Regras do JSON:
            - Gere exatamente 12 semanas
            - nivel deve ser: FACIL, MEDIO ou DIFICIL
            - tipo deve ser: QUESTOES, REVISAO ou META_ACERTO
            - meta é um número inteiro positivo
            - dataExame no formato yyyy-MM-dd
            """;

    private static final String SYSTEM_PROMPT_REVIEW = """
            Você é um assistente educacional do StudyMind, especializado em vestibular brasileiro.
            O aluno já possui um plano de estudos ativo e quer fazer uma revisão do progresso.

            Conduza uma conversa natural e colete as seguintes informações:
            1. O que está funcionando bem no plano atual
            2. Quais matérias ou tópicos estão sendo difíceis
            3. Se a carga de horas por dia ainda está adequada
            4. Se há alguma matéria nova para adicionar ou remover

            Faça uma pergunta por vez. Seja amigável e encorajador.

            Quando tiver todas as informações, gere um novo plano ajustado respondendo assim:
            - Primeira linha: exatamente a palavra ONBOARDING_COMPLETO
            - Segunda linha: o JSON completo em uma única linha, sem markdown, sem ```

            O JSON deve seguir EXATAMENTE a mesma estrutura do plano anterior, com as devidas correções.
            Regras do JSON:
            - Gere exatamente 12 semanas
            - nivel deve ser: FACIL, MEDIO ou DIFICIL
            - tipo deve ser: QUESTOES, REVISAO ou META_ACERTO
            - meta é um número inteiro positivo
            - dataExame no formato yyyy-MM-dd
            - O campo versao deve ser incrementado em relação ao plano anterior
            - Não repita os mesmos tópicos e tarefas do plano anterior
            """;

    @Transactional(readOnly = true)
    public DadosStatusOnboarding getStatus(Long usuarioId) {
        var usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));
        return new DadosStatusOnboarding(usuarioId, usuario.getOnboardingConcluido());
    }

    @Transactional
    public DadosRespostaChat enviarMensagem(Long usuarioId, String mensagemUsuario) {
        var usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));

        if (usuario.getOnboardingConcluido()) {
            throw new RegrasDeNegocioException("Onboarding já foi concluído. Use o endpoint de review.");
        }

        return processarMensagem(usuario, mensagemUsuario, SYSTEM_PROMPT_ONBOARDING);
    }

    @Transactional
    public DadosRespostaChat enviarMensagemReview(Long usuarioId, String mensagemUsuario) {
        var usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));

        if (!usuario.getOnboardingConcluido()) {
            throw new RegrasDeNegocioException("Onboarding ainda não foi concluído.");
        }

        return processarMensagem(usuario, mensagemUsuario, SYSTEM_PROMPT_REVIEW);
    }

    private DadosRespostaChat processarMensagem(Usuario usuario, String mensagemUsuario, String systemPrompt) {
        var msgUsuario = new ChatMensagem();
        msgUsuario.setUsuario(usuario);
        msgUsuario.setRole(RoleChat.USER);
        msgUsuario.setConteudo(mensagemUsuario);
        chatMensagemRepository.save(msgUsuario);

        var historico = chatMensagemRepository.findAllByUsuarioIdOrderByCriadoEmAsc(usuario.getId());
        var prompt = montarPromptComHistorico(historico, systemPrompt);
        var respostaIA = aiClient.gerarResposta(prompt);

        boolean concluido = respostaIA.contains("ONBOARDING_COMPLETO");
        String respostaLimpa = respostaIA.replace("ONBOARDING_COMPLETO", "").trim();

        if (concluido) {
            salvarPlanoEFinalizar(usuario, respostaLimpa);
            var fimMensagem = respostaLimpa.indexOf("{");
            respostaLimpa = fimMensagem > 0
                    ? respostaLimpa.substring(0, fimMensagem).trim()
                    : "Perfeito! Seu plano de estudos personalizado foi criado. Vamos começar!";

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

    private String montarPromptComHistorico(List<ChatMensagem> historico, String systemPrompt) {
        var sb = new StringBuilder();
        sb.append(systemPrompt).append("\n\n");
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
                throw new RuntimeException("IA não retornou JSON válido");
            }
            var fimJson = respostaComJson.lastIndexOf("}") + 1;
            var json = respostaComJson.substring(inicioJson, fimJson).trim();

            objectMapper.readTree(json);

            // desativa plano anterior se existir (caso review)
            planoEstudoRepository.findByUsuarioIdAndAtivoTrue(usuario.getId())
                    .ifPresent(planoAtivo -> planoAtivo.setAtivo(false));

            // calcula próxima versão
            var versaoAtual = planoEstudoRepository
                    .findAllByUsuarioIdOrderByVersaoAsc(usuario.getId())
                    .stream()
                    .mapToInt(p -> p.getVersao())
                    .max()
                    .orElse(0);

            var plano = new PlanoEstudo();
            plano.setUsuario(usuario);
            plano.setConteudoJson(json);
            plano.setVersao(versaoAtual + 1);
            plano.setAtivo(true);
            planoEstudoRepository.save(plano);
            planoEstudoParser.parsearEPopular(usuario, json);

            usuario.setOnboardingConcluido(true);
            usuarioRepository.save(usuario);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar plano de estudos", e);
        }
    }
}