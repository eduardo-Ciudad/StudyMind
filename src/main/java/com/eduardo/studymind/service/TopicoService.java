package com.eduardo.studymind.service;


import com.eduardo.studymind.domain.materia.MateriaRepository;
import com.eduardo.studymind.domain.topico.Topico;
import com.eduardo.studymind.domain.topico.TopicoRepository;
import com.eduardo.studymind.dto.input.topico.DadosCadastroTopico;
import com.eduardo.studymind.dto.output.topico.DadosDetalhamentoTopico;
import com.eduardo.studymind.dto.output.usuario.DadosDetalhamentoUsuario;
import com.eduardo.studymind.exception.RecursoNaoEncontradoException;
import com.eduardo.studymind.exception.RegrasDeNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class TopicoService {
    private final TopicoRepository topicoRepository;
    private final MateriaRepository materiaRepository;

    @Transactional
    public DadosDetalhamentoUsuario cadastrarTopico(DadosCadastroTopico dados) {
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
        return new DadosDetalhamentoTopico(topico)
    }
}
