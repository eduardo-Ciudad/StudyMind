package com.eduardo.studymind.service;


import com.eduardo.studymind.domain.materia.MateriaRepository;
import com.eduardo.studymind.domain.topico.Topico;
import com.eduardo.studymind.domain.topico.TopicoRepository;
import com.eduardo.studymind.dto.input.topico.DadosAtualizacaoTopico;
import com.eduardo.studymind.dto.input.topico.DadosCadastroTopico;
import com.eduardo.studymind.dto.output.topico.DadosDetalhamentoTopico;
import com.eduardo.studymind.dto.output.topico.DadosListagemTopico;
import com.eduardo.studymind.exception.RecursoNaoEncontradoException;
import com.eduardo.studymind.exception.RegrasDeNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicoService {
    private final TopicoRepository topicoRepository;
    private final MateriaRepository materiaRepository;

    @Transactional
    public DadosDetalhamentoTopico cadastrarTopico(DadosCadastroTopico dados) {
        var materia = materiaRepository.findById(dados.materiaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("MAtéria nao encontrada"));

        if (topicoRepository.existsByNomeAndMateriaId(dados.nome(), dados.materiaId())) {
            throw new RegrasDeNegocioException("Já existe um topico com esse nome");
        }
        var topico = new Topico();
        topico.setNome(dados.nome());
        topico.setDescricao(dados.descricao());
        topico.setMateria(materia);
        topico.setNivel(dados.nivelDificuldade());
        topico.setAtivo(true);

        topicoRepository.save(topico);
        return new DadosDetalhamentoTopico(topico);
    }

    public List<DadosListagemTopico> listarTopicos(Long materiaID) {

        return topicoRepository.findAllByAtivoTrue()
                .stream()
                .map(DadosListagemTopico::new)
                .toList();
    }

    public DadosDetalhamentoTopico buscarPorId(Long id) {
        var topico = topicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tópico nao encontrado"));
        return new DadosDetalhamentoTopico(topico);
    }

    @Transactional
    public DadosDetalhamentoTopico atualizar(Long id, DadosAtualizacaoTopico dados) {
        var topico = topicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Topico nao encontrado"));

        if (dados.nome() != null) topico.setNome(dados.nome());
        if (dados.descricao() != null) topico.setDescricao(dados.descricao());
        if (dados.nivelDificuldade() != null) topico.setNivel(dados.nivelDificuldade());
        if (dados.ativo() != null) topico.setAtivo(dados.ativo());

        return new DadosDetalhamentoTopico(topico);
    }

    @Transactional
    public void desativar(Long id) {
        var topico = topicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Topico nao encontrado"));
        topico.setAtivo(false);
    }
}


