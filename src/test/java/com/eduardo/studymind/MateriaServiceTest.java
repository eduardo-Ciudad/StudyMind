package com.eduardo.studymind;
import static org.assertj.core.api.Assertions.assertThat;

import com.eduardo.studymind.domain.materia.Materia;
import com.eduardo.studymind.domain.materia.MateriaRepository;
import com.eduardo.studymind.domain.usuario.Usuario;
import com.eduardo.studymind.domain.usuario.UsuarioRepository;
import com.eduardo.studymind.dto.input.materia.DadosCadastroMateria;
import com.eduardo.studymind.exception.RecursoNaoEncontradoException;
import com.eduardo.studymind.exception.RegrasDeNegocioException;
import com.eduardo.studymind.service.MateriaService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MateriaServiceTest {

    @Mock
    private MateriaRepository materiaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private MateriaService materiaService;

    private Usuario usuario;

    @BeforeEach
    void setup() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Eduardo");
    }

    @Test
    @DisplayName("Deve cadastrar materia e retornar dados de detalhamento")
    void cadastrarMateria_sucesso() {
        var dados = new DadosCadastroMateria("Matemática", "Álgebra e geometria");

        var materiaSalva = new Materia();
        materiaSalva.setId(1L);
        materiaSalva.setNome("Matemática");
        materiaSalva.setDescricao("Álgebra e geometria");
        materiaSalva.setAtiva(true);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(materiaRepository.existsByNomeAndUsuarioId("Matemática", 1L)).thenReturn(false);
        when(materiaRepository.save(any(Materia.class))).thenReturn(materiaSalva);

        var resultado = materiaService.cadastrarMateria(dados, 1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nome()).isEqualTo("Matemática");
        assertThat(resultado.descricao()).isEqualTo("Álgebra e geometria");
        assertThat(resultado.ativa()).isTrue();
        verify(materiaRepository).save(any(Materia.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar materia com nome duplicado")
    void cadastrarMateria_nomeDuplicado() {
        var dados = new DadosCadastroMateria("Matemática", "Álgebra e geometria");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(materiaRepository.existsByNomeAndUsuarioId("Matemática", 1L)).thenReturn(true);

        assertThatThrownBy(() -> materiaService.cadastrarMateria(dados, 1L))
                .isInstanceOf(RegrasDeNegocioException.class)
                .hasMessage("Já existe uma matéria com esse nome");
    }

    @Test
    @DisplayName("Deve retornar lista de materias ativas do usuário")
    void listarMateria_sucesso() {
        var materia1 = new Materia();
        materia1.setId(1L);
        materia1.setNome("Matemática");
        materia1.setAtiva(true);

        var materia2 = new Materia();
        materia2.setId(2L);
        materia2.setNome("Português");
        materia2.setAtiva(true);

        when(materiaRepository.findAllByUsuarioIdAndAtivaTrue(1L)).thenReturn(List.of(materia1, materia2));

        var resultado = materiaService.listarMateria(1L);

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).nome()).isEqualTo("Matemática");
        assertThat(resultado.get(1).nome()).isEqualTo("Português");
    }

    @Test
    @DisplayName("Deve retornar materia ao buscar por ID existente")
    void buscarPorId_sucesso() {
        var materia = new Materia();
        materia.setId(1L);
        materia.setNome("Matemática");
        materia.setAtiva(true);


        when(materiaRepository.findById(1L)).thenReturn(Optional.of(materia));

        materiaService.desativarMateria(1L);

        assertThat(materia.getAtiva()).isFalse();
        verify(materiaRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao desativar materia inexistente")
    void desativarMateria_naoEncontrado() {
        when(materiaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> materiaService.desativarMateria(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Materia nao encontrada");
    }
}