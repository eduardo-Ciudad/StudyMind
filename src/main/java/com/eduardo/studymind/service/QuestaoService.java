package com.eduardo.studymind.service;

import com.eduardo.studymind.domain.questao.Questao;
import com.eduardo.studymind.domain.questao.QuestaoRepository;
import com.eduardo.studymind.domain.topico.Topico;
import com.eduardo.studymind.domain.topico.TopicoRepository;
import com.eduardo.studymind.dto.input.questao.DadosAtualizacaoQuestao;
import com.eduardo.studymind.dto.input.questao.DadosCadastroQuestao;
import com.eduardo.studymind.dto.output.questao.DadosDetalhamentoQuestao;
import com.eduardo.studymind.dto.output.questao.DadosListagemQuestao;
import com.eduardo.studymind.exception.RecursoNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        var questaoSalva = questaoRepository.save(questao);
        return new DadosDetalhamentoQuestao(questaoSalva);
    }

    public Page<DadosListagemQuestao> listar(Pageable pageable) {
        return questaoRepository.findByAtivaTrue(pageable)
                .map(DadosListagemQuestao::new);
    }

    @Transactional
    public DadosDetalhamentoQuestao atualizar(Long id, DadosAtualizacaoQuestao dados) {
        var questao = questaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Questao nao encontrado"));
        if(dados.enunciado() != null) questao.setEnunciado(dados.enunciado());
        if(dados.tipo() != null) questao.setTipo(dados.tipo());
        if(dados.ativa() != null) questao.setAtiva(dados.ativa());

        return new DadosDetalhamentoQuestao(questao);
    }

    @Transactional
    public void inativar(Long id) {
       var questao =  questaoRepository.findById(id)
               .orElseThrow(() -> new RecursoNaoEncontradoException("Questao nao encontrado"));
       questao.setAtiva(false);
    }
}
