package com.eduardo.studymind;
import com.eduardo.studymind.dto.output.performance.DadosDesempenhoTopico;
import com.eduardo.studymind.dto.output.performance.DadosDesempenhoUsuario;
import com.eduardo.studymind.exception.ErroIntegracaoIAException;
import com.eduardo.studymind.infra.ia.AIClient;
import com.eduardo.studymind.service.PerformanceAnalyzerService;
import com.eduardo.studymind.service.RecommendationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;



@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private PerformanceAnalyzerService performanceAnalyzerService;

    @Mock
    private AIClient aiClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RecommendationService recommendationService;

    private DadosDesempenhoUsuario desempenhoMock;

    private static final String RESPOSTA_IA_VALIDA = """
            {
              "diagnostico": "Desempenho abaixo da média em exatas.",
              "topicosPrioritarios": ["Funções", "Geometria"],
              "dicasPraticas": ["Revisar teoria", "Fazer exercícios"],
              "mensagemMotivacional": "Você consegue!"
            }
            """;

    @BeforeEach
    void setup() {
        var topicoFraco = new DadosDesempenhoTopico(null, "Funções", "Matemática", 10, 3, 30.0);
        desempenhoMock = new DadosDesempenhoUsuario(
                1L, 10, 3, 30.0,
                List.of(topicoFraco),
                List.of(topicoFraco)
        );
    }

    private void configurarObjectMapper() {
        var realMapper = new ObjectMapper();
        try {
            when(objectMapper.readTree(anyString()))
                    .thenAnswer(i -> realMapper.readTree((String) i.getArgument(0)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Deve gerar recomendação com resposta válida da IA")
    void gerarRecomendacao_sucesso() {
        configurarObjectMapper();
        when(performanceAnalyzerService.analisarDesempenho(1L)).thenReturn(desempenhoMock);
        when(aiClient.gerarResposta(anyString())).thenReturn(RESPOSTA_IA_VALIDA);

        var resultado = recommendationService.gerarRecomendacao(1L);

        assertThat(resultado.usuarioId()).isEqualTo(1L);
        assertThat(resultado.diagnostico()).isEqualTo("Desempenho abaixo da média em exatas.");
        assertThat(resultado.topicosPrioritarios()).containsExactly("Funções", "Geometria");
        assertThat(resultado.dicasPraticas()).containsExactly("Revisar teoria", "Fazer exercícios");
        assertThat(resultado.mensagemMotivacional()).isEqualTo("Você consegue!");
        assertThat(resultado.taxaAcertoGeral()).isEqualTo(30.0);
    }

    @Test
    @DisplayName("Deve limpar blocos markdown antes de parsear JSON")
    void gerarRecomendacao_limpaBlocoMarkdown() {
        configurarObjectMapper();
        var respostaComMarkdown = "```json\n" + RESPOSTA_IA_VALIDA + "\n```";
        when(performanceAnalyzerService.analisarDesempenho(1L)).thenReturn(desempenhoMock);
        when(aiClient.gerarResposta(anyString())).thenReturn(respostaComMarkdown);

        var resultado = recommendationService.gerarRecomendacao(1L);

        assertThat(resultado.diagnostico()).isEqualTo("Desempenho abaixo da média em exatas.");
    }

    @Test
    @DisplayName("Deve lançar ErroIntegracaoIAException quando JSON da IA é inválido")
    void gerarRecomendacao_jsonInvalido() {
        configurarObjectMapper();
        when(performanceAnalyzerService.analisarDesempenho(1L)).thenReturn(desempenhoMock);
        when(aiClient.gerarResposta(anyString())).thenReturn("resposta inválida");

        assertThatThrownBy(() -> recommendationService.gerarRecomendacao(1L))
                .isInstanceOf(ErroIntegracaoIAException.class)
                .hasMessage("Erro ao processar resposta da IA");
    }

    @Test
    @DisplayName("Deve lançar ErroIntegracaoIAException quando IA está indisponível")
    void gerarRecomendacao_iaIndisponivel() {
        when(performanceAnalyzerService.analisarDesempenho(1L)).thenReturn(desempenhoMock);
        when(aiClient.gerarResposta(anyString())).thenThrow(new RuntimeException("timeout"));

        assertThatThrownBy(() -> recommendationService.gerarRecomendacao(1L))
                .isInstanceOf(ErroIntegracaoIAException.class);
    }

    @Test
    @DisplayName("Deve incluir tópicos fracos no prompt enviado para a IA")
    void gerarRecomendacao_incluiTopicosNoPrompt() {
        configurarObjectMapper();
        when(performanceAnalyzerService.analisarDesempenho(1L)).thenReturn(desempenhoMock);
        when(aiClient.gerarResposta(anyString())).thenReturn(RESPOSTA_IA_VALIDA);

        recommendationService.gerarRecomendacao(1L);

        verify(aiClient).gerarResposta(argThat(prompt ->
                prompt.contains("Funções") &&
                        prompt.contains("Matemática") &&
                        prompt.contains("30.0%")
        ));
    }
}