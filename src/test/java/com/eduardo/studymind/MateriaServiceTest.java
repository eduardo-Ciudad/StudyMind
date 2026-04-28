package com.eduardo.studymind;
import static org.assertj.core.api.Assertions.assertThat;

import com.eduardo.studymind.domain.materia.Materia;
import com.eduardo.studymind.domain.materia.MateriaRepository;
import com.eduardo.studymind.dto.input.materia.DadosCadastroMateria;
import com.eduardo.studymind.service.MateriaService;
import jakarta.persistence.EntityNotFoundException;
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

    @InjectMocks
    private MateriaService materiaService;

    @Test
    @DisplayName("Deve cadastrar materia e retornar dados de detalhamento")
    void cadastrarMateria_sucesso() {
        // given
        var dados = new DadosCadastroMateria("Matemática", "Álgebra e geometria");

        var materiaSalva = new Materia();
        materiaSalva.setId(1L);
        materiaSalva.setNome("Matemática");
        materiaSalva.setDescricao("Álgebra e geometria");
        materiaSalva.setAtiva(true);

        when(materiaRepository.save(any(Materia.class))).thenReturn(materiaSalva);

        // when
        var resultado = materiaService.cadastrarMateria(dados);

        // then
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nome()).isEqualTo("Matemática");
        assertThat(resultado.descricao()).isEqualTo("Álgebra e geometria");
        assertThat(resultado.ativa()).isTrue();

        verify(materiaRepository).save(any(Materia.class));
    }

    @Test
    @DisplayName("Deve retornar lista de materias ativas")
    void listarMateria_sucesso() {
        // given
        var materia1 = new Materia();
        materia1.setId(1L);
        materia1.setNome("Matemática");
        materia1.setAtiva(true);

        var materia2 = new Materia();
        materia2.setId(2L);
        materia2.setNome("Português");
        materia2.setAtiva(true);

        when(materiaRepository.findAllByAtivaTrue()).thenReturn(List.of(materia1, materia2));

        // when
        var resultado = materiaService.listarMateria();

        // then
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).nome()).isEqualTo("Matemática");
        assertThat(resultado.get(1).nome()).isEqualTo("Português");
    }

    @Test
    @DisplayName("Deve retornar materia ao buscar por ID existente")
    void buscarPorId_sucesso() {
        // given
        var materia = new Materia();
        materia.setId(1L);
        materia.setNome("Matemática");
        materia.setAtiva(true);

        when(materiaRepository.findById(1L)).thenReturn(Optional.of(materia));

        // when
        var resultado = materiaService.buscarPorID(1L);

        // then
        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.nome()).isEqualTo("Matemática");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar materia com ID inexistente")
    void buscarPorId_naoEncontrado() {
        // given
        when(materiaRepository.findById(99L)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> materiaService.buscarPorID(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Materia nao encontrada");
    }

    @Test
    @DisplayName("Deve desativar materia existente")
    void desativarMateria_sucesso() {
        // given
        var materia = new Materia();
        materia.setId(1L);
        materia.setNome("Matemática");
        materia.setAtiva(true);

        when(materiaRepository.findById(1L)).thenReturn(Optional.of(materia));

        // when
        materiaService.desativarMateria(1L);

        // then
        assertThat(materia.getAtiva()).isFalse();
        verify(materiaRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao desativar materia inexistente")
    void desativarMateria_naoEncontrado() {
        // given
        when(materiaRepository.findById(99L)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> materiaService.desativarMateria(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Materia nao encontrada");
    }
}
