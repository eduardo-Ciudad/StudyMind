package com.eduardo.studymind.service;


import com.eduardo.studymind.domain.resultado.RespostaStatus;
import com.eduardo.studymind.domain.resultado.ResultadoRepository;
import com.eduardo.studymind.domain.topico.Topico;
import com.eduardo.studymind.domain.topico.TopicoRepository;
import com.eduardo.studymind.dto.output.performace.DadosDesempenhoTopico;
import com.eduardo.studymind.dto.output.performace.DadosDesempenhoUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceAnalyzerService {

    private final ResultadoRepository resultadoRepository;
    private final TopicoRepository topicoRepository;

    public DadosDesempenhoUsuario analisarDesempenho(Long usuarioId) {
        var topicosAtivos = topicoRepository.findAllByAtivoTrueWithMateriaAndUsuarioId(usuarioId);
        var desempenhoPorTopico = topicosAtivos.stream()
                .map(topico -> calcularDesempenhoTopico(usuarioId, topico))
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

    private DadosDesempenhoTopico calcularDesempenhoTopico(Long usuarioId, Topico topico) {
        long total = resultadoRepository
                .findAllByUsuarioIdAndQuestaoTopicoId(usuarioId, topico.getId()).size();

        long acertos = resultadoRepository
                .countByUsuarioIdAndQuestaoTopicoIdAndStatus(usuarioId, topico.getId(), RespostaStatus.CORRETO);

        double taxa = total > 0 ? (double) acertos / total * 100 : 0.0;

        return new DadosDesempenhoTopico(
                topico.getId(),
                topico.getNome(),
                topico.getMateria().getNome(),
                (int) total,
                (int) acertos,
                taxa
        );
    }
}
