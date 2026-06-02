package com.eduardo.studymind.service;


import com.eduardo.studymind.domain.materia.Materia;
import com.eduardo.studymind.domain.materia.MateriaRepository;
import com.eduardo.studymind.domain.usuario.UsuarioRepository;
import com.eduardo.studymind.dto.input.materia.DadosAtualizacaoMateria;
import com.eduardo.studymind.dto.input.materia.DadosCadastroMateria;
import com.eduardo.studymind.dto.output.materia.DadosDetalhamentoMateria;
import com.eduardo.studymind.dto.output.materia.DadosListagemMateria;
import com.eduardo.studymind.exception.RecursoNaoEncontradoException;
import com.eduardo.studymind.exception.RegrasDeNegocioException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MateriaService {

    private final MateriaRepository materiaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public DadosDetalhamentoMateria cadastrarMateria(DadosCadastroMateria dados, Long usuarioId) {
        var usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));

        if (materiaRepository.existsByNomeAndUsuarioId(dados.nome(), usuarioId)) {
            throw new RegrasDeNegocioException("Já existe uma matéria com esse nome");
        }

        var materia = new Materia();
        materia.setUsuario(usuario);
        materia.setNome(dados.nome());
        materia.setDescricao(dados.descricao());
        materia.setAtiva(true);

        var materiaSalva = materiaRepository.save(materia);
        log.info("Matéria cadastrada com sucesso. Id: {}", materiaSalva.getId());
        return new DadosDetalhamentoMateria(materiaSalva);
    }

    public List<DadosListagemMateria> listarMateria(Long usuarioId) {
        return  materiaRepository.findAllByUsuarioIdAndAtivaTrue(usuarioId)
                .stream()
                .map(DadosListagemMateria::new)
                .toList();
    }

    public DadosDetalhamentoMateria buscarPorID(Long id) {
        var materia = materiaRepository.findById(id)
        .orElseThrow(() -> new RecursoNaoEncontradoException("Materia nao encontrada"));
        return new DadosDetalhamentoMateria(materia);
    }

    @Transactional
    public DadosDetalhamentoMateria atualizarMateria (Long id, DadosAtualizacaoMateria dados) {
        log.info("Atualizando matéria. Id: {}", id);
        var materia = materiaRepository.findById(id)
        .orElseThrow(() -> new RecursoNaoEncontradoException("Materia nao encontrada"));

        if(dados.nome() != null) materia.setNome(dados.nome());
        if (dados.descricao() != null) materia.setDescricao(dados.descricao());
        if (dados.ativa() != null) materia.setAtiva(dados.ativa());

        return new DadosDetalhamentoMateria(materia);
    }

    @Transactional
    public void desativarMateria(Long id) {
        log.info("Desativando matéria. Id: {}", id);
        var materia = materiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Materia nao encontrada"));
        materia.setAtiva(false);
    }

    @Transactional(readOnly = true)
    public Long buscarDono(Long id) {
        return materiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tarefa não encontrada"))
                .getUsuario().getId();
    }
}
