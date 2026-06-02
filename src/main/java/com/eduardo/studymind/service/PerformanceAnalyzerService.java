package com.eduardo.studymind.service;


import com.eduardo.studymind.domain.resultado.ResultadoSessao;
import com.eduardo.studymind.domain.resultado.ResultadoSessaoRepository;
import com.eduardo.studymind.domain.topico.Topico;
import com.eduardo.studymind.domain.topico.TopicoRepository;
import com.eduardo.studymind.dto.output.performance.DadosDesempenhoTopico;
import com.eduardo.studymind.dto.output.performance.DadosDesempenhoUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceAnalyzerService {

    private final ResultadoSessaoRepository  resultadoSessaoRepository;
    private final TopicoRepository topicoRepository;


    public DadosDesempenhoUsuario analisarDesempenho(Long usuarioId) {
        var sessoes = resultadoSessaoRepository.findByUsuarioId(usuarioId);

        // agrupa as sessões por tópico
        var desempenhoPorTopico = sessoes.stream()
                .collect(Collectors.groupingBy(s -> s.getTopicoNome() + "|" + s.getMateriaNome()))
                .entrySet().stream()
                .map(entry -> {
                    var sessoesDoTopico = entry.getValue();
                    var primeira = sessoesDoTopico.get(0);

                    int totalRespostas = sessoesDoTopico.stream()
                            .mapToInt(ResultadoSessao::getTotalQuestoes)
                            .sum();

                    int totalAcertos = sessoesDoTopico.stream()
                            .mapToInt(ResultadoSessao::getAcertos)
                            .sum();

                    double taxa = totalRespostas > 0
                            ? (double) totalAcertos / totalRespostas * 100
                            : 0.0;

                    Long topicoId = topicoRepository
                            .findByNomeAndMateriaNomeAndUsuarioId(
                                    primeira.getTopicoNome(),
                                    primeira.getMateriaNome(),
                                    usuarioId
                            )
                            .map(Topico::getId)
                            .orElse(null);

                    return new DadosDesempenhoTopico(
                            topicoId, // ← usa o valor resolvido
                            primeira.getTopicoNome(),
                            primeira.getMateriaNome(),
                            totalRespostas,
                            totalAcertos,
                            taxa
                    );
                })
                .filter(d -> d.totalRespostas() > 0)
                .toList();

        int totalRespostas = desempenhoPorTopico.stream()
                .mapToInt(DadosDesempenhoTopico::totalRespostas)
                .sum();

        int totalAcertos = desempenhoPorTopico.stream()
                .mapToInt(DadosDesempenhoTopico::totalAcertos)
                .sum();

        double taxaGeral = totalRespostas > 0
                ? (double) totalAcertos / totalRespostas * 100
                : 0.0;

        var topicosMaisFracos = desempenhoPorTopico.stream()
                .sorted(Comparator.comparingDouble(DadosDesempenhoTopico::taxaAcerto))
                .limit(5)
                .toList();

        return new DadosDesempenhoUsuario(
                usuarioId,
                totalRespostas,
                totalAcertos,
                taxaGeral,
                desempenhoPorTopico,
                topicosMaisFracos
        );
    }


}
