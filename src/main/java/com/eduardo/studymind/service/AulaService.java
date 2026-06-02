package com.eduardo.studymind.service;

import com.eduardo.studymind.domain.topico.Topico;
import com.eduardo.studymind.domain.topico.TopicoRepository;
import com.eduardo.studymind.dto.output.aulaoutput.DadosAulaOutput;
import com.eduardo.studymind.dto.output.questaogerada.DadosQuestaoGerada;
import com.eduardo.studymind.dto.output.questoesoutput.DadosQuestoesOutput;
import com.eduardo.studymind.exception.ErroIntegracaoIAException;
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
public class AulaService {

    private final AIClient aiClient;
    private final TopicoRepository topicoRepository;

    public DadosAulaOutput gerarConteudoAula(Long topicoId) {
        Topico topico = topicoRepository.findById(topicoId)
                .orElseThrow(() -> new EntityNotFoundException("Tópico não encontrado"));

        log.info("Iniciando geração de aula para topicoId: {}", topicoId);

        String prompt = """
                Você é um professor especialista preparando material de estudo personalizado.
                
                Tópico: %s
                Matéria: %s
                Nível de dificuldade: %s
                
                Gere um conteúdo de aula estruturado em JSON com exatamente este formato:
                {
                  "titulo": "título da aula",
                  "materia": "nome da matéria",
                  "nivelDificuldade": "nível",
                  "conteudo": "explicação detalhada e didática do tópico, com conceitos-chave, exemplos práticos e o que o aluno deve focar. Mínimo 300 palavras.",
                  "recomendacoes": ["recomendação 1", "recomendação 2", "recomendação 3"]
                }
                
                Responda APENAS com o JSON, sem texto adicional, sem markdown.
                """.formatted(
                topico.getNome(),
                topico.getMateria().getNome(),
                topico.getNivel().name()
        );

        String resposta = aiClient.gerarResposta(prompt);
        return parseAulaJson(resposta);
    }

    public DadosQuestoesOutput gerarQuestoes(Long topicoId, int quantidade) {
        Topico topico = topicoRepository.findById(topicoId)
                .orElseThrow(() -> new EntityNotFoundException("Tópico não encontrado"));

        log.info("Iniciando geração de {} questões para topicoId: {}", quantidade, topicoId);

        String prompt = """


                Você é um professor criando questões de revisão para vestibular/ENEM.
                
                Tópico: %s
                Matéria: %s
                Nível: %s
                Quantidade de questões: %d
                
                Gere exatamente %d questões de múltipla escolha em JSON com este formato:
                {
                  "topico": "nome do tópico",
                  "total": %d,
                  "questoes": [
                    {
                      "numero": 1,
                      "enunciado": "texto da questão",
                      "alternativas": ["A) texto", "B) texto", "C) texto", "D) texto", "E) texto"],
                      "alternativaCorreta": 0,
                      "explicacao": "por que esta alternativa está correta"
                    }
                  ]
                }
                
                As questões devem ser específicas ao tópico, progressivas em dificuldade e no estilo ENEM.
                
                REGRAS OBRIGATÓRIAS PARA AS QUESTÕES:
                - NUNCA gere questões que dependam de imagens, gráficos visuais, tabelas externas ou figuras.
                - Todas as questões devem ser completamente autocontidas no enunciado.
                - Se a questão envolver dados numéricos, comparações ou situações contextuais, inclua TODOS os dados diretamente no texto do enunciado.
                - Exemplo correto: "Uma loja vendeu 120 unidades em janeiro, 95 em fevereiro e 180 em março. Em qual mês as vendas foram maiores?"
                - Exemplo proibido: "Analise o gráfico abaixo e responda..."
                
                Responda APENAS com o JSON, sem texto adicional, sem markdown.
                """.formatted(
                topico.getNome(),
                topico.getMateria().getNome(),
                topico.getNivel().name(),
                quantidade, quantidade, quantidade
        );

        String resposta = aiClient.gerarResposta(prompt);
        return parseQuestoesJson(resposta);
    }

    public DadosAulaOutput gerarConteudoPorNome(String topicoNome, String materiaNome, String nivel) {
        String prompt = """
            Você é um professor especialista preparando material de estudo personalizado.
            
            Tópico: %s
            Matéria: %s
            Nível de dificuldade: %s
            
            Gere um conteúdo de aula estruturado em JSON com exatamente este formato:
            {
              "titulo": "título da aula",
              "materia": "nome da matéria",
              "nivelDificuldade": "nível",
              "conteudo": "explicação detalhada e didática do tópico, com conceitos-chave, exemplos práticos e o que o aluno deve focar. Mínimo 300 palavras.",
              "recomendacoes": ["recomendação 1", "recomendação 2", "recomendação 3"]
            }
            
            Responda APENAS com o JSON, sem texto adicional, sem markdown.
            """.formatted(topicoNome, materiaNome, nivel);

        return parseAulaJson(aiClient.gerarResposta(prompt));
    }

    public DadosQuestoesOutput gerarQuestoesPorNome(String topicoNome, String materiaNome, int quantidade) {
        String prompt = """
            Você é um professor criando questões de revisão para vestibular/ENEM.
            
            Tópico: %s
            Matéria: %s
            Quantidade de questões: %d
            
            Gere exatamente %d questões de múltipla escolha em JSON com este formato:
            {
              "topico": "nome do tópico",
              "total": %d,
              "questoes": [
                {
                  "numero": 1,
                  "enunciado": "texto da questão",
                  "alternativas": ["A) texto", "B) texto", "C) texto", "D) texto", "E) texto"],
                  "alternativaCorreta": 0,
                  "explicacao": "por que esta alternativa está correta"
                }
              ]
            }
            
            As questões devem ser específicas ao tópico, progressivas em dificuldade e no estilo ENEM.
            
                REGRAS OBRIGATÓRIAS PARA AS QUESTÕES:
                - NUNCA gere questões que dependam de imagens, gráficos visuais, tabelas externas ou figuras.
                - Todas as questões devem ser completamente autocontidas no enunciado.
                - Se a questão envolver dados numéricos, comparações ou situações contextuais, inclua TODOS os dados diretamente no texto do enunciado.
                - Exemplo correto: "Uma loja vendeu 120 unidades em janeiro, 95 em fevereiro e 180 em março. Em qual mês as vendas foram maiores?"
                - Exemplo proibido: "Analise o gráfico abaixo e responda..."
                
            Responda APENAS com o JSON, sem texto adicional, sem markdown.
            """.formatted(topicoNome, materiaNome, quantidade, quantidade, quantidade);

        return parseQuestoesJson(aiClient.gerarResposta(prompt));
    }

    private DadosAulaOutput parseAulaJson(String json) {
        try {
            String jsonLimpo = json.trim()
                    .replaceAll("(?s)```json\\s*", "")
                    .replaceAll("(?s)```\\s*", "")
                    .trim();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(jsonLimpo);
            List<String> recomendacoes = new ArrayList<>();
            node.get("recomendacoes").forEach(r -> recomendacoes.add(r.asText()));
            return new DadosAulaOutput(
                    node.get("titulo").asText(),
                    node.get("materia").asText(),
                    node.get("nivelDificuldade").asText(),
                    node.get("conteudo").asText(),
                    recomendacoes
            );
        } catch (Exception e) {
            log.error("Erro ao fazer parse do JSON de aula da IA", e);
            throw new ErroIntegracaoIAException("Erro ao processar resposta da IA para aula", e);
        }
    }

    private DadosQuestoesOutput parseQuestoesJson(String json) {
        try {
            String jsonLimpo = json.trim()
                    .replaceAll("(?s)```json\\s*", "")
                    .replaceAll("(?s)```\\s*", "")
                    .trim();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(jsonLimpo);
            List<DadosQuestaoGerada> questoes = new ArrayList<>();
            node.get("questoes").forEach(q -> {
                List<String> alternativas = new ArrayList<>();
                q.get("alternativas").forEach(a -> alternativas.add(a.asText()));
                questoes.add(new DadosQuestaoGerada(
                        q.get("numero").asInt(),
                        q.get("enunciado").asText(),
                        alternativas,
                        q.get("alternativaCorreta").asInt(),
                        q.get("explicacao").asText()
                ));
            });
            return new DadosQuestoesOutput(
                    node.get("topico").asText(),
                    node.get("total").asInt(),
                    questoes
            );
        } catch (Exception e) {
            log.error("Erro ao fazer parse do JSON de questões da IA", e);
            throw new ErroIntegracaoIAException("Erro ao processar resposta da IA para questões", e);
        }
    }
}