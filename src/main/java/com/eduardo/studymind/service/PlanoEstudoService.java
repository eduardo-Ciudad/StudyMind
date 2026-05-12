package com.eduardo.studymind.service;

import com.eduardo.studymind.domain.plano.PlanoEstudoRepository;
import com.eduardo.studymind.dto.output.plano.DadosPlanoEstudo;
import com.eduardo.studymind.exception.RecursoNaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlanoEstudoService {

    private final PlanoEstudoRepository planoEstudoRepository;

    @Transactional(readOnly = true)
    public DadosPlanoEstudo buscarPorUsuario(Long usuarioId) {
        var plano = planoEstudoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Plano de estudo não encontrado"));
        return new DadosPlanoEstudo(plano.getId(), plano.getUsuario().getId(), plano.getConteudoJson(), plano.getCriadoEm());
    }
}
