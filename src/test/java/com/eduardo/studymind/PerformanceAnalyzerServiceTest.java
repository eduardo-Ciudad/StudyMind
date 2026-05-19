package com.eduardo.studymind;
import com.eduardo.studymind.domain.resultado.ResultadoSessao;
import com.eduardo.studymind.domain.resultado.ResultadoSessaoRepository;
import com.eduardo.studymind.domain.topico.TopicoRepository;
import com.eduardo.studymind.service.PerformanceAnalyzerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PerformanceAnalyzerServiceTest {

    @Mock
    private ResultadoSessaoRepository resultadoSessaoRepository;

    @Mock
    private TopicoRepository topicoRepository;

    @InjectMocks
    private PerformanceAnalyzerService performanceAnalyzerService;

    private ResultadoSessao criarSessao(String topicoNome, String materiaNome, int total, int acertos) {
        var sessao = new ResultadoSessao();
        sessao.setTopicoNome(topicoNome);
        sessao.setMateriaNome(materiaNome);
        sessao.setTotalQuestoes(total);
        sessao.setAcertos(acertos);
        sessao.setTaxaAcerto(total > 0 ? (double) acertos / total * 100 : 0.0);
        return sessao;
    }

    @Test
    @DisplayName("Deve retornar desempenho zerado quando usuário não tem sessões")
    void analisarDesempenho_semSessoes() {
        when(resultadoSessaoRepository.findByUsuarioId(1L)).thenReturn(List.of());

        var resultado = performanceAnalyzerService.analisarDesempenho(1L);

        assertThat(resultado.totalRespostas()).isZero();
        assertThat(resultado.totalAcertos()).isZero();
        assertThat(resultado.taxaAcertoGeral()).isZero();
        assertThat(resultado.desempenhoPorTopico()).isEmpty();
        assertThat(resultado.topicosMaisFracos()).isEmpty();
    }

    @Test
    @DisplayName("Deve calcular taxa de acerto corretamente")
    void analisarDesempenho_calculaTaxaCorretamente() {
        var sessao = criarSessao("Funções", "Matemática", 10, 7);
        when(resultadoSessaoRepository.findByUsuarioId(1L)).thenReturn(List.of(sessao));

        var resultado = performanceAnalyzerService.analisarDesempenho(1L);

        assertThat(resultado.totalRespostas()).isEqualTo(10);
        assertThat(resultado.totalAcertos()).isEqualTo(7);
        assertThat(resultado.taxaAcertoGeral()).isEqualTo(70.0);
    }

    @Test
    @DisplayName("Deve agrupar múltiplas sessões do mesmo tópico")
    void analisarDesempenho_agrupaSessoesDoMesmoTopico() {
        var sessao1 = criarSessao("Funções", "Matemática", 10, 6);
        var sessao2 = criarSessao("Funções", "Matemática", 10, 8);
        when(resultadoSessaoRepository.findByUsuarioId(1L)).thenReturn(List.of(sessao1, sessao2));

        var resultado = performanceAnalyzerService.analisarDesempenho(1L);

        assertThat(resultado.desempenhoPorTopico()).hasSize(1);
        assertThat(resultado.totalRespostas()).isEqualTo(20);
        assertThat(resultado.totalAcertos()).isEqualTo(14);
        assertThat(resultado.taxaAcertoGeral()).isEqualTo(70.0);
    }

    @Test
    @DisplayName("Deve identificar os 5 tópicos mais fracos ordenados por taxa")
    void analisarDesempenho_identificaTopicosMaisFracos() {
        var sessoes = List.of(
                criarSessao("Funções", "Matemática", 10, 9),
                criarSessao("Geometria", "Matemática", 10, 3),
                criarSessao("Gramática", "Português", 10, 5),
                criarSessao("Redação", "Português", 10, 7),
                criarSessao("Cinética", "Química", 10, 2),
                criarSessao("Termologia", "Física", 10, 4)
        );
        when(resultadoSessaoRepository.findByUsuarioId(1L)).thenReturn(sessoes);

        var resultado = performanceAnalyzerService.analisarDesempenho(1L);

        assertThat(resultado.topicosMaisFracos()).hasSize(5);
        assertThat(resultado.topicosMaisFracos().get(0).topicoNome()).isEqualTo("Cinética");
        assertThat(resultado.topicosMaisFracos().get(1).topicoNome()).isEqualTo("Geometria");
    }

    @Test
    @DisplayName("Deve separar tópicos de matérias diferentes com mesmo nome")
    void analisarDesempenho_separaTopicosPorMateria() {
        var sessao1 = criarSessao("Introdução", "Matemática", 10, 8);
        var sessao2 = criarSessao("Introdução", "Física", 10, 5);
        when(resultadoSessaoRepository.findByUsuarioId(1L)).thenReturn(List.of(sessao1, sessao2));

        var resultado = performanceAnalyzerService.analisarDesempenho(1L);

        assertThat(resultado.desempenhoPorTopico()).hasSize(2);
    }
}