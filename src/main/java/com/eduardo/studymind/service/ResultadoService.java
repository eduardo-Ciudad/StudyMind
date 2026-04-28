package com.eduardo.studymind.service;


import com.eduardo.studymind.domain.questao.QuestaoRepository;
import com.eduardo.studymind.domain.resultado.Resultado;
import com.eduardo.studymind.domain.resultado.ResultadoRepository;
import com.eduardo.studymind.domain.usuario.UsuarioRepository;
import com.eduardo.studymind.dto.input.resultado.DadosCadastroResultado;
import com.eduardo.studymind.dto.output.resultado.DadosDetalhamentoResultado;
import com.eduardo.studymind.dto.output.resultado.DadosListagemResultados;
import com.eduardo.studymind.exception.RecursoNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ResultadoService {

    private final ResultadoRepository resultadoRepository;
    private final QuestaoRepository questaoRepository;
    private final UsuarioRepository usuarioRepository;

    public DadosDetalhamentoResultado cadastrar(DadosCadastroResultado dados){
        var usuario = usuarioRepository.findById(dados.usuarioId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));
        var questao = questaoRepository.findById(dados.questaoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Questao nao encontrado"));
        Resultado resultado = new Resultado();
        resultado.setUsuario(usuario);
        resultado.setQuestao(questao);
        resultado.setStatus(dados.status());
        resultado.setRespostaUsuario(dados.respostaUsuario());

        var resultadoSalvo = resultadoRepository.save(resultado);
        return new DadosDetalhamentoResultado(resultadoSalvo);
    }

    @Transactional(readOnly = true)
    public Page<DadosListagemResultados> listarPorUsuario(Long usuarioId, Pageable pageable){
        return resultadoRepository.findAllByUsuarioId(usuarioId, pageable)
                .map(DadosListagemResultados::new);
    }

    @Transactional(readOnly = true)
    public DadosDetalhamentoResultado detalharResultado(Long id){
        var resultado = resultadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Resultado nao encontrado"));
        return new DadosDetalhamentoResultado(resultado);
    }

}
