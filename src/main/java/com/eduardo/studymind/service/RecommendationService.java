package com.eduardo.studymind.service;


import com.eduardo.studymind.dto.output.performace.DadosDesempenhoUsuario;
import com.eduardo.studymind.dto.output.recomendacao.DadosRecomendacao;
import com.eduardo.studymind.infra.ia.AIClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final PerformanceAnalyzerService performanceAnalyzerService;
    private final AIClient aiClient;
    private final ObjectMapper objectMapper;

    public DadosRecomendacao gerarRecomendacao(Long usuarioId) {
        var desempenho = performanceAnalyzerService.analisarDesempenho(usuarioId);

        var prompt = montarPrompt(desempenho);
        var respostaIA = aiClient.gerarResposta(prompt);

        return parseResposta(respostaIA, desempenho);
    }

    private String montarPrompt(DadosDesempenhoUsuario desempenho) {
        var topicosFragos = desempenho.topicosMaisFracos().stream()
                .map(t -> String.format("- %s (%s): %.1f%% de acerto",
                        t.topicoNome(), t.materiaNome(), t.taxaAcerto()))
                .collect(Collectors.joining("\n"));

        return """
                Você é um assistente educacional especializado em vestibular brasileiro.
                Analise o desempenho do aluno e forneça recomendações de estudo.
                
                DADOS DO ALUNO:
                - Total de questões respondidas: %d
                - Taxa de acerto geral: %.1f%%
                
                TÓPICOS COM MAIOR DIFICULDADE:
                %s
                
                Responda APENAS com um JSON válido, sem texto adicional, no seguinte formato:
                {
                  "diagnostico": "texto com análise geral do desempenho",
                  "topicosPrioritarios": ["topico1", "topico2", "topico3"],
                  "dicasPraticas": ["dica1", "dica2", "dica3"],
                  "mensagemMotivacional": "texto motivacional personalizado"
                }
                """.formatted(
                desempenho.totalRespostas(),
                desempenho.taxaAcertoGeral(),
                topicosFragos
        );
        }

    private DadosRecomendacao parseResposta(String respostaIA, DadosDesempenhoUsuario desempenho) {
        try {

            // limpa blocos de código markdown se a IA retornar com ```json
            var jsonLimpo = respostaIA
                    .replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

            var json = objectMapper.readTree(jsonLimpo);

            var topicosPrioritarios = new ArrayList<String>();
            json.get("topicosPrioritarios").forEach(t -> topicosPrioritarios.add(t.asText()));

            var dicasPraticas = new ArrayList<String>();
            json.get("dicasPraticas").forEach(d -> dicasPraticas.add(d.asText()));

            return new DadosRecomendacao(
                    desempenho.usuarioId(),
                    json.get("diagnostico").asText(),
                    topicosPrioritarios,
                    dicasPraticas,
                    json.get("mensagemMotivacional").asText(),
                    desempenho.taxaAcertoGeral()
            );
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar resposta da IA", e);
        }
    }
}
