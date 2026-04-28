package com.eduardo.studymind.service;


import com.eduardo.studymind.domain.materia.Materia;
import com.eduardo.studymind.domain.materia.MateriaRepository;
import com.eduardo.studymind.dto.input.materia.DadosAtualizacaoMateria;
import com.eduardo.studymind.dto.input.materia.DadosCadastroMateria;
import com.eduardo.studymind.dto.output.materia.DadosDetalhamentoMateria;
import com.eduardo.studymind.dto.output.materia.DadosListagemMateria;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MateriaService {

    private final MateriaRepository materiaRepository;

    @Transactional
    public DadosDetalhamentoMateria cadastrarMateria (DadosCadastroMateria dados) {
        var materia = new Materia();
        materia.setNome(dados.nome());
        materia.setDescricao(dados.descricao());
        materia.setAtiva(true);

        var materiaSalva = materiaRepository.save(materia);
        return new DadosDetalhamentoMateria(materiaSalva);
    }

    public List<DadosListagemMateria> listarMateria() {
        return  materiaRepository.findAllByAtivaTrue()
                .stream()
                .map(DadosListagemMateria::new)
                .toList();
    }

    public DadosDetalhamentoMateria buscarPorID(Long id) {
        var materia = materiaRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Materia nao encontrada"));
        return new DadosDetalhamentoMateria(materia);
    }

    @Transactional
    public DadosDetalhamentoMateria atualizarMateria (Long id, DadosAtualizacaoMateria dados) {
        var materia = materiaRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Materia nao encontrada"));

        if(dados.nome() != null) materia.setNome(dados.nome());
        if (dados.descricao() != null) materia.setDescricao(dados.descricao());
        if (dados.ativa() != null) materia.setAtiva(dados.ativa());

        return new DadosDetalhamentoMateria(materia);
    }

    @Transactional
    public void desativarMateria(Long id) {
        var materia = materiaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Materia nao encontrada"));
        materia.setAtiva(false);
    }
}
