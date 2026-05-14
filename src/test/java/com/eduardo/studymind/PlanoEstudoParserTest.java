package com.eduardo.studymind;

import com.eduardo.studymind.domain.materia.Materia;
import com.eduardo.studymind.domain.materia.MateriaRepository;
import com.eduardo.studymind.domain.tarefa.Tarefa;
import com.eduardo.studymind.domain.tarefa.TarefaRepository;
import com.eduardo.studymind.domain.topico.Topico;
import com.eduardo.studymind.domain.topico.TopicoRepository;
import com.eduardo.studymind.domain.usuario.Usuario;
import com.eduardo.studymind.service.parser.PlanoEstudoParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlanoEstudoParserTest {

    @Mock
    private MateriaRepository materiaRepository;

    @Mock
    private TopicoRepository topicoRepository;

    @Mock
    private TarefaRepository tarefaRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PlanoEstudoParser planoEstudoParser;

    private Usuario usuario;

    private static final String JSON_VALIDO = """
            {
              "vestibular": "ENEM",
              "dataExame": "2026-11-01",
              "horasPorDia": 3,
              "versao": 1,
              "materias": [
                {
                  "nome": "Matemática",
                  "descricao": "Matemática para ENEM",
                  "topicos": [
                    {
                      "nome": "Funções",
                      "nivel": "MEDIO",
                      "descricao": "Funções quadráticas"
                    }
                  ]
                }
              ],
              "semanas": [
                {
                  "numero": 1,
                  "tarefas": [
                    {
                      "topicoNome": "Funções",
                      "materiaNome": "Matemática",
                      "tipo": "REVISAO",
                      "descricao": "Revisar funções quadráticas",
                      "meta": 10
                    }
                  ]
                }
              ]
            }
            """;

    @BeforeEach
    void setup() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Eduardo");
    }

    @Test
    @DisplayName("Deve criar matéria nova quando não existe no banco")
    void parsear_criaMateriaNova() throws Exception {
        var realMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        when(objectMapper.readTree(anyString())).thenAnswer(i -> realMapper.readTree((String) i.getArgument(0)));

        var materia = new Materia();
        materia.setId(1L);
        materia.setNome("Matemática");
        materia.setUsuario(usuario);

        var topico = new Topico();
        topico.setId(1L);
        topico.setNome("Funções");
        topico.setMateria(materia);
        topico.setUsuario(usuario);

        when(materiaRepository.findByNomeAndUsuarioId("Matemática", 1L)).thenReturn(Optional.empty());
        when(materiaRepository.save(any(Materia.class))).thenReturn(materia);
        when(topicoRepository.findByNomeAndMateriaIdAndUsuarioId("Funções", 1L, 1L)).thenReturn(Optional.empty());
        when(topicoRepository.save(any(Topico.class))).thenReturn(topico);
        when(topicoRepository.findByNomeAndMateriaIdAndUsuarioId("Funções", 1L, 1L))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(topico));

        planoEstudoParser.parsearEPopular(usuario, JSON_VALIDO);

        verify(materiaRepository).save(any(Materia.class));
        verify(topicoRepository, atLeastOnce()).save(any(Topico.class));
        verify(tarefaRepository).save(any(Tarefa.class));
    }

    @Test
    @DisplayName("Deve reutilizar matéria existente sem criar nova")
    void parsear_reutilizaMateriaExistente() throws Exception {
        var realMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        when(objectMapper.readTree(anyString())).thenAnswer(i -> realMapper.readTree((String) i.getArgument(0)));

        var materia = new Materia();
        materia.setId(1L);
        materia.setNome("Matemática");
        materia.setUsuario(usuario);

        var topico = new Topico();
        topico.setId(1L);
        topico.setNome("Funções");
        topico.setMateria(materia);
        topico.setUsuario(usuario);

        when(materiaRepository.findByNomeAndUsuarioId("Matemática", 1L)).thenReturn(Optional.of(materia));
        when(topicoRepository.findByNomeAndMateriaIdAndUsuarioId("Funções", 1L, 1L))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(topico));
        when(topicoRepository.save(any(Topico.class))).thenReturn(topico);

        planoEstudoParser.parsearEPopular(usuario, JSON_VALIDO);

        verify(materiaRepository, never()).save(any(Materia.class));
        verify(tarefaRepository).save(any(Tarefa.class));
    }

    @Test
    @DisplayName("Deve reutilizar tópico existente sem criar novo")
    void parsear_reutilizaTopicoExistente() throws Exception {
        var realMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        when(objectMapper.readTree(anyString())).thenAnswer(i -> realMapper.readTree((String) i.getArgument(0)));

        var materia = new Materia();
        materia.setId(1L);
        materia.setNome("Matemática");
        materia.setUsuario(usuario);

        var topico = new Topico();
        topico.setId(1L);
        topico.setNome("Funções");
        topico.setMateria(materia);
        topico.setUsuario(usuario);

        when(materiaRepository.findByNomeAndUsuarioId("Matemática", 1L)).thenReturn(Optional.of(materia));
        when(topicoRepository.findByNomeAndMateriaIdAndUsuarioId("Funções", 1L, 1L)).thenReturn(Optional.of(topico));

        planoEstudoParser.parsearEPopular(usuario, JSON_VALIDO);

        verify(topicoRepository, never()).save(any(Topico.class));
        verify(tarefaRepository).save(any(Tarefa.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando JSON não contém campo materias")
    void parsear_jsonSemMaterias() throws Exception {
        var jsonInvalido = """
            {
              "vestibular": "ENEM",
              "semanas": []
            }
            """;
        var realMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        when(objectMapper.readTree(anyString())).thenAnswer(i -> realMapper.readTree((String) i.getArgument(0)));

        assertThatThrownBy(() -> planoEstudoParser.parsearEPopular(usuario, jsonInvalido))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Erro ao parsear plano de estudos")
                .cause()
                .hasMessageContaining("JSON do plano inválido");
    }

    @Test
    @DisplayName("Deve sempre criar nova tarefa mesmo com tópico reutilizado")
    void parsear_sempreCriaNovasTarefas() throws Exception {
        var realMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        when(objectMapper.readTree(anyString())).thenAnswer(i -> realMapper.readTree((String) i.getArgument(0)));

        var materia = new Materia();
        materia.setId(1L);
        materia.setNome("Matemática");
        materia.setUsuario(usuario);

        var topico = new Topico();
        topico.setId(1L);
        topico.setNome("Funções");
        topico.setMateria(materia);
        topico.setUsuario(usuario);

        when(materiaRepository.findByNomeAndUsuarioId("Matemática", 1L)).thenReturn(Optional.of(materia));
        when(topicoRepository.findByNomeAndMateriaIdAndUsuarioId("Funções", 1L, 1L)).thenReturn(Optional.of(topico));

        planoEstudoParser.parsearEPopular(usuario, JSON_VALIDO);

        verify(tarefaRepository, times(1)).save(any(Tarefa.class));
    }
}