package com.eduardo.studymind.service;

import com.eduardo.studymind.domain.tarefa.Tarefa;
import com.eduardo.studymind.domain.tarefa.TarefaRepository;
import com.eduardo.studymind.dto.output.tarefadescricaooutput.DadosTarefaDescricao;
import com.eduardo.studymind.exception.ErroIntegracaoIAException;
import com.eduardo.studymind.exception.RecursoNaoEncontradoException;
import com.eduardo.studymind.infra.ia.AIClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TarefaDescricaoService {

    private final AIClient aiClient;
    private final TarefaRepository tarefaRepository;

    public DadosTarefaDescricao gerarDescricao(Long tarefaId) {
        log.info("Gerando descrição para tarefaId: {}", tarefaId);
        Tarefa tarefa = tarefaRepository.findByIdWithTopico(tarefaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tarefa não encontrada"));

        String prompt = """
                Você é um assistente de estudos explicando o que o aluno deve fazer em uma tarefa.
                
                Tarefa: %s
                Tipo: %s
                Meta: %d questões
                Tópico: %s
                Matéria: %s
                
                Gere uma descrição motivadora e clara em JSON com este formato:
                {
                  "titulo": "título amigável da tarefa",
                  "descricaoDetalhada": "explicação em 2-3 frases do que o aluno vai fazer e por que é importante",
                  "passos": ["passo 1", "passo 2", "passo 3"]
                }
                
                Os passos devem ser ações concretas e simples. Máximo 4 passos.
                Responda APENAS com o JSON, sem texto adicional, sem markdown.
                """.formatted(
                tarefa.getDescricao(),
                tarefa.getTipo().name(),
                tarefa.getMeta(),
                tarefa.getTopico() != null ? tarefa.getTopico().getNome() : "Geral",
                tarefa.getTopico() != null ? tarefa.getTopico().getMateria().getNome() : "Geral"
        );

        String resposta = aiClient.gerarResposta(prompt);
        return parseDescricaoJson(resposta);
    }

    private DadosTarefaDescricao parseDescricaoJson(String json) {
        try {
            String jsonLimpo = json.trim()
                    .replaceAll("(?s)```json\\s*", "")
                    .replaceAll("(?s)```\\s*", "")
                    .trim();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(jsonLimpo);
            List<String> passos = new ArrayList<>();
            node.get("passos").forEach(p -> passos.add(p.asText()));
            return new DadosTarefaDescricao(
                    node.get("titulo").asText(),
                    node.get("descricaoDetalhada").asText(),
                    passos
            );
        } catch (Exception e) {
            log.error("Erro ao fazer parse do JSON de descrição da IA", e);
            throw new ErroIntegracaoIAException("Erro ao processar resposta da IA para tarefa", e);
        }
    }
}