package com.eduardo.studymind.service;


import com.eduardo.studymind.domain.tarefa.Tarefa;
import com.eduardo.studymind.domain.tarefa.TarefaRepository;
import com.eduardo.studymind.domain.tarefa.TarefaStatus;
import com.eduardo.studymind.domain.topico.TopicoRepository;
import com.eduardo.studymind.domain.usuario.UsuarioRepository;
import com.eduardo.studymind.dto.input.tarefa.DadosAtualizacaoTarefa;
import com.eduardo.studymind.dto.input.tarefa.DadosCadastroTarefa;
import com.eduardo.studymind.dto.output.tarefa.DadosDetalhamentoTarefa;
import com.eduardo.studymind.dto.output.tarefa.DadosListagemTarefa;
import com.eduardo.studymind.exception.RecursoNaoEncontradoException;
import com.eduardo.studymind.exception.RegrasDeNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class TarefaService {

    private final TarefaRepository tarefaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TopicoRepository topicoRepository;

    @Transactional
    public DadosDetalhamentoTarefa cadastrar(DadosCadastroTarefa dados){
        var usuario = usuarioRepository.findById(dados.usuarioId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));
        var topico = topicoRepository.findById(dados.topicoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Topico nao encontrado"));
        if (tarefaRepository.existsByUsuarioIdAndTopicoIdAndStatus(dados.usuarioId(), dados.topicoId(), TarefaStatus.PENDENTE)) {
            throw new RegrasDeNegocioException("Já existe essa tarefa pendente para esse tópico");
        }

        var tarefa  = new Tarefa();
        tarefa.setUsuario(usuario);
        tarefa.setTopico(topico);
        tarefa.setTipo(dados.tipo());
        tarefa.setDescricao(dados.descricao());
        tarefa.setMeta(dados.meta());
        tarefa.setPrazo(dados.time());
        tarefa.setStatus(TarefaStatus.PENDENTE);

        var tarefaSalva = tarefaRepository.save(tarefa);
        return new DadosDetalhamentoTarefa(tarefaSalva);
    }

    public List<DadosListagemTarefa> listarPorUsuario(Long usuarioId, TarefaStatus status) {
        if (status != null) {
            return tarefaRepository.findAllByUsuarioIdAndStatus(usuarioId, status)
                    .stream()
                    .map(DadosListagemTarefa::new)
                    .toList();
        }
        return tarefaRepository.findAllByUsuarioIdAndStatusNot(usuarioId, TarefaStatus.CANCELADA)
                .stream()
                .map(DadosListagemTarefa::new)
                .toList();
    }

    @Transactional
    public DadosDetalhamentoTarefa atualizarTarefa(Long id, DadosAtualizacaoTarefa dados){
        var tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tarefa não encontrada"));
        if (tarefa.getStatus() == TarefaStatus.CONCLUIDA || tarefa.getStatus() == TarefaStatus.CANCELADA) {
            throw new RegrasDeNegocioException("Não é possivel atualizar a tarefa" + tarefa.getStatus().name().toLowerCase(Locale.ROOT));
        }


        if (dados.descricao() != null) tarefa.setDescricao(dados.descricao());
        if (dados.meta() != null)      tarefa.setMeta(dados.meta());
        if (dados.prazo() != null)     tarefa.setPrazo(dados.prazo());
        if (dados.status() != null)    tarefa.setStatus(dados.status());

        return new DadosDetalhamentoTarefa(tarefa);
    }

    @Transactional
    public void cancelar(Long id){
        var tarefa = tarefaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tarefa nao encontrada"));

        if (tarefa.getStatus() == TarefaStatus.CONCLUIDA) {
            throw new RegrasDeNegocioException("Não é possivel cancelar uma tarefa já cadastrada");
        }

        tarefa.setStatus(TarefaStatus.CANCELADA);
    }
}
