package com.eduardo.studymind;

import com.eduardo.studymind.domain.materia.Materia;
import com.eduardo.studymind.domain.materia.MateriaRepository;
import com.eduardo.studymind.domain.topico.NivelDificuldade;
import com.eduardo.studymind.domain.topico.Topico;
import com.eduardo.studymind.domain.topico.TopicoRepository;
import com.eduardo.studymind.dto.input.topico.DadosCadastroTopico;
import com.eduardo.studymind.exception.RecursoNaoEncontradoException;
import com.eduardo.studymind.exception.RegrasDeNegocioException;
import com.eduardo.studymind.service.TopicoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.util.List;
import java.util.Optional;




import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TopicoServiceTest {

    @Mock
    private TopicoRepository topicoRepository;

    @Mock
    private MateriaRepository materiaRepository;

    @InjectMocks
    private TopicoService topicoService;

    @Test
    @DisplayName("Deve cadastrar topico e retornar detalhamento")
    void cadastrarTopico_sucesso() {
        var dados = new DadosCadastroTopico("Geometria", "Formas planas", 1L, NivelDificuldade.MEDIO);

        var materia = new Materia();
        materia.setId(1L);
        materia.setNome("Matemática");

        var topicoSalvo = new Topico();
        topicoSalvo.setId(1L);
        topicoSalvo.setNome("Geometria");
        topicoSalvo.setDescricao("Formas planas");
        topicoSalvo.setMateria(materia);
        topicoSalvo.setAtivo(true);

        when(materiaRepository.findById(1L)).thenReturn(Optional.of(materia));
        when(topicoRepository.existsByNomeAndMateriaId("Geometria", 1L)).thenReturn(false);
        when(topicoRepository.save(any(Topico.class))).thenReturn(topicoSalvo);

        var resultado = topicoService.cadastrarTopico(dados);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nome()).isEqualTo("Geometria");
        verify(topicoRepository).save(any(Topico.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar topico com nome duplicado")
    void cadastrarTopico_nomeDuplicado() {
        var dados = new DadosCadastroTopico("Geometria", "Formas planas", 1L, NivelDificuldade.MEDIO);

        var materia = new Materia();
        materia.setId(1L);

        when(materiaRepository.findById(1L)).thenReturn(Optional.of(materia));
        when(topicoRepository.existsByNomeAndMateriaId("Geometria", 1L)).thenReturn(true);

        assertThatThrownBy(() -> topicoService.cadastrarTopico(dados))
                .isInstanceOf(RegrasDeNegocioException.class)
                .hasMessage("Já existe um topico com esse nome");
    }

    @Test
    @DisplayName("Deve retornar lista de topicos ativos")
    void listarTopicos_sucesso() {

        var materia = new Materia();
        materia.setId(1L);
        materia.setNome("Matemática");

        var topico1 = new Topico();
        topico1.setId(1L);
        topico1.setNome("Geometria");
        topico1.setAtivo(true);
        topico1.setMateria(materia);

        var topico2 = new Topico();
        topico2.setId(2L);
        topico2.setNome("Álgebra");
        topico2.setAtivo(true);
        topico2.setMateria(materia);

        when(topicoRepository.findAllByAtivoTrue()).thenReturn(List.of(topico1, topico2));

        var resultado = topicoService.listarTopicos(null);

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).nome()).isEqualTo("Geometria");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar topico inexistente")
    void buscarPorId_naoEncontrado() {
        when(topicoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> topicoService.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Tópico nao encontrado");
    }

    @Test
    @DisplayName("Deve desativar topico existente")
    void desativar_sucesso() {
        var topico = new Topico();
        topico.setId(1L);
        topico.setAtivo(true);

        when(topicoRepository.findById(1L)).thenReturn(Optional.of(topico));

        topicoService.desativar(1L);

        assertThat(topico.getAtivo()).isFalse();
    }
}