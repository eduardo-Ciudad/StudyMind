package com.eduardo.studymind.service;

import com.eduardo.studymind.domain.resultado.ResultadoSessao;
import com.eduardo.studymind.domain.resultado.ResultadoSessaoRepository;
import com.eduardo.studymind.domain.usuario.Usuario;
import com.eduardo.studymind.domain.usuario.UsuarioRepository;
import com.eduardo.studymind.dto.input.resultado.DadosCadastroResultadoSessao;
import com.eduardo.studymind.dto.output.resultado.DadosResultadoSessaoOutput;
import com.eduardo.studymind.exception.RecursoNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResultadoSessaoService {

    private final ResultadoSessaoRepository repository;
    private final UsuarioRepository usuarioRepository;

    public DadosResultadoSessaoOutput salvar(DadosCadastroResultadoSessao dados, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado"));

        double taxaAcerto = dados.totalQuestoes() > 0
                ? Math.round((dados.acertos() * 100.0 / dados.totalQuestoes()) * 100.0) / 100.0
                : 0.0;

        ResultadoSessao sessao = new ResultadoSessao();
        sessao.setUsuario(usuario);
        sessao.setTopicoNome(dados.topicoNome());
        sessao.setMateriaNome(dados.materiaNome());
        sessao.setTotalQuestoes(dados.totalQuestoes());
        sessao.setAcertos(dados.acertos());
        sessao.setTaxaAcerto(taxaAcerto);

        repository.save(sessao);
        log.info("Resultado de sessão salvo com sucesso. Id: {}", sessao.getId());

        return new DadosResultadoSessaoOutput(
                sessao.getId(),
                sessao.getTopicoNome(),
                sessao.getMateriaNome(),
                sessao.getTotalQuestoes(),
                sessao.getAcertos(),
                sessao.getTaxaAcerto(),
                sessao.getRespondidoEm()
        );
    }

    public List<DadosResultadoSessaoOutput> listarPorUsuario(Long usuarioId) {
        return repository.findByUsuarioIdOrderByRespondidoEmDesc(usuarioId)
                .stream()
                .map(s -> new DadosResultadoSessaoOutput(
                        s.getId(),
                        s.getTopicoNome(),
                        s.getMateriaNome(),
                        s.getTotalQuestoes(),
                        s.getAcertos(),
                        s.getTaxaAcerto(),
                        s.getRespondidoEm()
                ))
                .toList();
    }
}
