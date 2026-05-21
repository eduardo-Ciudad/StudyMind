package com.eduardo.studymind.service;


import com.eduardo.studymind.domain.materia.MateriaRepository;
import com.eduardo.studymind.domain.tarefa.TarefaRepository;
import com.eduardo.studymind.domain.topico.Topico;
import com.eduardo.studymind.domain.topico.TopicoRepository;
import com.eduardo.studymind.domain.usuario.UsuarioRepository;
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
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public DadosDetalhamentoTopico cadastrarTopico(DadosCadastroTopico dados, Long usuarioId) {
        var usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));

        var materia = materiaRepository.findById(dados.materiaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Matéria nao encontrada"));

        if (topicoRepository.existsByNomeAndMateriaId(dados.nome(), dados.materiaId())) {
            throw new RegrasDeNegocioException("Já existe um tópico com esse nome nessa matéria");
        }

        var topico = new Topico();
        topico.setUsuario(usuario);
        topico.setNome(dados.nome());
        topico.setDescricao(dados.descricao());
        topico.setMateria(materia);
        topico.setNivel(dados.nivelDificuldade());
        topico.setAtivo(true);

        var topicoSalvo = topicoRepository.save(topico);
        return new DadosDetalhamentoTopico(topicoSalvo);
    }

    public List<DadosListagemTopico> listarTopicos(Long materiaId, Long usuarioId) {


        // Se materiaId foi informado, busca apenas os tópicos
        // daquela matéria e que estejam ativos.
        //
        // Se materiaId for null, busca todos os tópicos ativos.
        List<Topico> topicos = (materiaId != null)
                ? topicoRepository.findAllByMateriaIdAndAtivoTrue(materiaId)   // esse ? e : é umamaneira simples de escrever if e else
                : topicoRepository.findAllByUsuarioIdAndAtivoTrue(usuarioId);                       // (condição) ? seSim : seNao


        // Converte cada entidade Topico em um DTO de saída
        // (DadosListagemTopico) e retorna a lista final.
        return topicos.stream()
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

    @Transactional(readOnly = true)
    public Long buscarDono(Long id) {
        return topicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tarefa não encontrada"))
                .getUsuario().getId();
    }
}


