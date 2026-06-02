package com.eduardo.studymind.service.parser;

import com.eduardo.studymind.domain.materia.Materia;
import com.eduardo.studymind.domain.materia.MateriaRepository;
import com.eduardo.studymind.domain.tarefa.Tarefa;
import com.eduardo.studymind.domain.tarefa.TarefaRepository;
import com.eduardo.studymind.domain.tarefa.TarefaStatus;
import com.eduardo.studymind.domain.tarefa.TipoTarefa;
import com.eduardo.studymind.domain.topico.NivelDificuldade;
import com.eduardo.studymind.domain.topico.Topico;
import com.eduardo.studymind.domain.topico.TopicoRepository;
import com.eduardo.studymind.domain.usuario.Usuario;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanoEstudoParser {

    private final MateriaRepository materiaRepository;
    private final TopicoRepository topicoRepository;
    private final TarefaRepository tarefaRepository;
    private final ObjectMapper objectMapper;


    @Transactional
    public void parsearEPopular(Usuario usuario, String conteudoJson) {
        try {
            log.info("Iniciando parse do plano de estudos para usuarioId: {}", usuario.getId());
            var planoNode = objectMapper.readTree(conteudoJson);
            var materiasNode = planoNode.get("materias");
            var semanasNode = planoNode.get("semanas");

            if (materiasNode == null || semanasNode == null) {
                throw new RuntimeException("JSON do plano inválido: campos 'materias' ou 'semanas' ausentes");
            }

            // PASSO 1 — processa matérias e tópicos
            // map de nome da matéria -> entidade Materia
            // usado depois para resolver as tarefas
            var materiasPorNome = processarMaterias(usuario, materiasNode);

            // PASSO 2 — processa semanas e tarefas
            processarSemanas(usuario, semanasNode, materiasPorNome);

            int totalTarefas = 0;
            for (var semana : semanasNode) {
                var t = semana.get("tarefas");
                if (t != null) totalTarefas += t.size();
            }
            log.info("Plano parseado com sucesso. {} tarefas criadas para usuarioId: {}", totalTarefas, usuario.getId());

        } catch (Exception e) {
            log.error("Erro ao parsear plano de estudos para usuarioId: {}", usuario.getId(), e);
            throw new RuntimeException("Erro ao parsear plano de estudos", e);
        }
    }


    private Map<String, Materia> processarMaterias(Usuario usuario, JsonNode materiasNode) {
        var materiasPorNome = new HashMap<String, Materia>();

        for (var materiaNode : materiasNode) {
            var nomeMateria = materiaNode.get("nome").asText();
            var descricaoMateria = materiaNode.has("descricao")
                    ? materiaNode.get("descricao").asText()
                    : null;

            // reutiliza ou cria a matéria
            var materia = materiaRepository
                    .findByNomeAndUsuarioId(nomeMateria, usuario.getId())
                    .orElseGet(() -> {
                        var novaMateria = new Materia();
                        novaMateria.setUsuario(usuario);
                        novaMateria.setNome(nomeMateria);
                        novaMateria.setDescricao(descricaoMateria);
                        novaMateria.setAtiva(true);
                        return materiaRepository.save(novaMateria);
                    });

            materiasPorNome.put(nomeMateria, materia);

            // processa tópicos dessa matéria
            var topicosNode = materiaNode.get("topicos");
            if (topicosNode != null) {
                processarTopicos(usuario, topicosNode, materia);
            }
        }

        return materiasPorNome;
    }


    private void processarTopicos(Usuario usuario, JsonNode topicosNode, Materia materia) {
        for (var topicoNode : topicosNode) {
            var nomeTopico = topicoNode.get("nome").asText();
            var descricaoTopico = topicoNode.has("descricao")
                    ? topicoNode.get("descricao").asText()
                    : null;
            var nivelTexto = topicoNode.has("nivel")
                    ? topicoNode.get("nivel").asText()
                    : "MEDIO";
            var nivel = NivelDificuldade.valueOf(nivelTexto);

            // reutiliza ou cria o tópico
            topicoRepository
                    .findByNomeAndMateriaIdAndUsuarioId(nomeTopico, materia.getId(), usuario.getId())
                    .orElseGet(() -> {
                        var novoTopico = new Topico();
                        novoTopico.setUsuario(usuario);
                        novoTopico.setNome(nomeTopico);
                        novoTopico.setDescricao(descricaoTopico);
                        novoTopico.setMateria(materia);
                        novoTopico.setNivel(nivel);
                        novoTopico.setAtivo(true);
                        return topicoRepository.save(novoTopico);
                    });
        }
    }

    private void processarSemanas(Usuario usuario, JsonNode semanasNode, Map<String, Materia> materiasPorNome) {
        for (var semanaNode : semanasNode) {
            var tarefasNode = semanaNode.get("tarefas");
            if (tarefasNode == null) continue;

            for (var tarefaNode : tarefasNode) {
                var topicoNome = tarefaNode.get("topicoNome").asText();
                var materiaNome = tarefaNode.get("materiaNome").asText();
                var tipo = TipoTarefa.valueOf(tarefaNode.get("tipo").asText());
                var descricao = tarefaNode.get("descricao").asText();
                var meta = tarefaNode.get("meta").asInt();

                // resolve a matéria pelo nome
                var materia = materiasPorNome.get(materiaNome);
                if (materia == null) {
                    throw new RuntimeException("Matéria não encontrada no plano: " + materiaNome);
                }

                // resolve o tópico pelo nome + matéria + usuário
                var topico = topicoRepository
                        .findByNomeAndMateriaIdAndUsuarioId(topicoNome, materia.getId(), usuario.getId())
                        .orElseThrow(() -> new RuntimeException("Tópico não encontrado: " + topicoNome));

                // sempre cria nova tarefa
                var tarefa = new Tarefa();
                tarefa.setUsuario(usuario);
                tarefa.setTopico(topico);
                tarefa.setTipo(tipo);
                tarefa.setDescricao(descricao);
                tarefa.setMeta(meta);
                tarefa.setStatus(TarefaStatus.PENDENTE);
                tarefaRepository.save(tarefa);
            }
        }
    }

}
