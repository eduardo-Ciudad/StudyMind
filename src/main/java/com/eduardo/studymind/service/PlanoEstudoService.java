package com.eduardo.studymind.service;

import com.eduardo.studymind.domain.plano.PlanoEstudoRepository;
import com.eduardo.studymind.dto.output.plano.DadosPlanoEstudo;
import com.eduardo.studymind.exception.RecursoNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanoEstudoService {

    private final PlanoEstudoRepository planoEstudoRepository;

    @Transactional(readOnly = true)
    public DadosPlanoEstudo buscarPorUsuario(Long usuarioId) {
        var plano = planoEstudoRepository.findByUsuarioIdAndAtivoTrue(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Plano de estudo não encontrado"));
        return new DadosPlanoEstudo(plano.getId(), plano.getUsuario().getId(), plano.getConteudoJson(), plano.getVersao(), plano.getCriadoEm());
    }

    @Transactional
    public List<DadosPlanoEstudo> buscarHistoricoPorUsuario(Long usuarioId) {
        return planoEstudoRepository.findAllByUsuarioIdOrderByVersaoAsc(usuarioId)
                .stream()
                .map(p -> new DadosPlanoEstudo(p.getId(), p.getUsuario().getId(), p.getConteudoJson(), p.getVersao(), p.getCriadoEm()))
                .toList();
    }

    @Transactional
    public void desativarPlanoEstudo(Long usuarioId) {
        log.info("Desativando plano de estudo para usuarioId: {}", usuarioId);
        planoEstudoRepository.findByUsuarioIdAndAtivoTrue(usuarioId)
                .ifPresent(plano -> plano.setAtivo(false));
    //O método ifPresent executa a ação apenas se o Optional contiver um valor.
        }
}
