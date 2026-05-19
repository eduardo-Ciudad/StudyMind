package com.eduardo.studymind;

import com.eduardo.studymind.domain.plano.PlanoEstudo;
import com.eduardo.studymind.domain.plano.PlanoEstudoRepository;
import com.eduardo.studymind.domain.usuario.Usuario;
import com.eduardo.studymind.exception.RecursoNaoEncontradoException;
import com.eduardo.studymind.service.PlanoEstudoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanoEstudoServiceTest {

    @Mock
    private PlanoEstudoRepository planoEstudoRepository;

    @InjectMocks
    private PlanoEstudoService planoEstudoService;

    private Usuario usuario;
    private PlanoEstudo plano;

    @BeforeEach
    void setup() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Eduardo");

        plano = new PlanoEstudo();
        plano.setId(1L);
        plano.setUsuario(usuario);
        plano.setConteudoJson("{\"vestibular\":\"ENEM\"}");
        plano.setVersao(1);
        plano.setAtivo(true);
        plano.setCriadoEm(LocalDateTime.now());
    }

    // buscarPorUsuario

    @Test
    @DisplayName("Deve retornar plano ativo do usuário")
    void buscarPorUsuario_retornaPlano() {
        when(planoEstudoRepository.findByUsuarioIdAndAtivoTrue(1L))
                .thenReturn(Optional.of(plano));

        var resultado = planoEstudoService.buscarPorUsuario(1L);

        assertThat(resultado.id()).isEqualTo(1L);
        assertThat(resultado.usuarioId()).isEqualTo(1L);
        assertThat(resultado.versao()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve lançar exceção quando não existe plano ativo")
    void buscarPorUsuario_planoNaoEncontrado() {
        when(planoEstudoRepository.findByUsuarioIdAndAtivoTrue(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> planoEstudoService.buscarPorUsuario(1L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Plano de estudo não encontrado");
    }

    // buscarHistoricoPorUsuario

    @Test
    @DisplayName("Deve retornar histórico ordenado por versão")
    void buscarHistorico_retornaLista() {
        var planoV2 = new PlanoEstudo();
        planoV2.setId(2L);
        planoV2.setUsuario(usuario);
        planoV2.setConteudoJson("{\"vestibular\":\"FUVEST\"}");
        planoV2.setVersao(2);
        planoV2.setAtivo(false);
        planoV2.setCriadoEm(LocalDateTime.now());

        when(planoEstudoRepository.findAllByUsuarioIdOrderByVersaoAsc(1L))
                .thenReturn(List.of(plano, planoV2));

        var resultado = planoEstudoService.buscarHistoricoPorUsuario(1L);

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).versao()).isEqualTo(1);
        assertThat(resultado.get(1).versao()).isEqualTo(2);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando usuário não tem planos")
    void buscarHistorico_semPlanos() {
        when(planoEstudoRepository.findAllByUsuarioIdOrderByVersaoAsc(1L))
                .thenReturn(List.of());

        var resultado = planoEstudoService.buscarHistoricoPorUsuario(1L);

        assertThat(resultado).isEmpty();
    }

    // desativarPlanoEstudo

    @Test
    @DisplayName("Deve desativar plano ativo existente")
    void desativar_planoExistente() {
        when(planoEstudoRepository.findByUsuarioIdAndAtivoTrue(1L))
                .thenReturn(Optional.of(plano));

        planoEstudoService.desativarPlanoEstudo(1L);

        assertThat(plano.getAtivo()).isFalse();
    }

    @Test
    @DisplayName("Não deve lançar exceção quando não existe plano ativo")
    void desativar_semPlanoAtivo() {
        when(planoEstudoRepository.findByUsuarioIdAndAtivoTrue(1L))
                .thenReturn(Optional.empty());

        assertThatNoException()
                .isThrownBy(() -> planoEstudoService.desativarPlanoEstudo(1L));
    }
}
