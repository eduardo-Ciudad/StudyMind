package com.eduardo.studymind.service;

import com.eduardo.studymind.domain.questao.Questao;
import com.eduardo.studymind.domain.questao.QuestaoRepository;
import com.eduardo.studymind.domain.topico.Topico;
import com.eduardo.studymind.domain.topico.TopicoRepository;
import com.eduardo.studymind.dto.input.questao.DadosCadastroQuestao;
import com.eduardo.studymind.dto.output.questao.DadosDetalhamentoQuestao;
import com.eduardo.studymind.exception.RecursoNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestaoService {

    private final QuestaoRepository questaoRepository;
    private final TopicoRepository topicoRepository;

    public DadosDetalhamentoQuestao cadastrar(DadosCadastroQuestao dados) {
        var topico = topicoRepository.findById(dados.topicoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tópico nao encontrado"));
        Questao questao = new Questao();
        questao.setTopico(topico);
        questao.setTipo(dados.tipo());
        questao.setTopico(topico);
        questao.setAtiva(true);

        questaoRepository.save(questao);
        return new DadosDetalhamentoQuestao(questao);
    }


}
