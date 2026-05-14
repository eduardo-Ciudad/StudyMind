package com.eduardo.studymind;

import com.eduardo.studymind.domain.chat.ChatMensagem;
import com.eduardo.studymind.domain.chat.ChatMensagemRepository;
import com.eduardo.studymind.domain.plano.PlanoEstudo;
import com.eduardo.studymind.domain.plano.PlanoEstudoRepository;
import com.eduardo.studymind.domain.usuario.Usuario;
import com.eduardo.studymind.domain.usuario.UsuarioRepository;
import com.eduardo.studymind.exception.RecursoNaoEncontradoException;
import com.eduardo.studymind.exception.RegrasDeNegocioException;
import com.eduardo.studymind.infra.ia.AIClient;
import com.eduardo.studymind.service.OnboardingService;
import com.eduardo.studymind.service.parser.PlanoEstudoParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OnboardingServiceTest {

    @Mock
    private ChatMensagemRepository chatMensagemRepository;

    @Mock
    private PlanoEstudoRepository planoEstudoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AIClient aiClient;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private PlanoEstudoParser planoEstudoParser;

    @InjectMocks
    private OnboardingService onboardingService;

    private Usuario usuario;

    @BeforeEach
    void setup() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Eduardo");
        usuario.setEmail("edu@email.com");
        usuario.setOnboardingConcluido(false);
    }

    @Test
    @DisplayName("Deve retornar status de onboarding não concluído")
    void getStatus_naoConcluido() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        var resultado = onboardingService.getStatus(1L);

        assertThat(resultado.onboardingConcluido()).isFalse();
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar status de usuário inexistente")
    void getStatus_usuarioNaoEncontrado() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> onboardingService.getStatus(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessage("Usuario nao encontrado");
    }

    @Test
    @DisplayName("Deve salvar mensagem do usuário e retornar resposta da IA")
    void enviarMensagem_respostaSimples() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(chatMensagemRepository.findAllByUsuarioIdOrderByCriadoEmAsc(1L)).thenReturn(List.of());
        when(aiClient.gerarResposta(anyString())).thenReturn("Qual vestibular você quer passar?");

        var resultado = onboardingService.enviarMensagem(1L, "Olá");

        assertThat(resultado.resposta()).isEqualTo("Qual vestibular você quer passar?");
        assertThat(resultado.onboardingConcluido()).isFalse();
        verify(chatMensagemRepository, times(2)).save(any(ChatMensagem.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao enviar mensagem com onboarding já concluído")
    void enviarMensagem_onboardingJaConcluido() {
        usuario.setOnboardingConcluido(true);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> onboardingService.enviarMensagem(1L, "Olá"))
                .isInstanceOf(RegrasDeNegocioException.class)
                .hasMessage("Onboarding já foi concluído. Use o endpoint de review.");
    }

    @Test
    @DisplayName("Deve concluir onboarding quando IA retorna ONBOARDING_COMPLETO")
    void enviarMensagem_concluidoComSucesso() throws Exception {
        var json = "{\"vestibular\":\"ENEM\",\"materias\":[],\"semanas\":[]}";
        var respostaIA = "ONBOARDING_COMPLETO\n" + json;

        var realMapper = new com.fasterxml.jackson.databind.ObjectMapper();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(chatMensagemRepository.findAllByUsuarioIdOrderByCriadoEmAsc(1L)).thenReturn(List.of());
        when(aiClient.gerarResposta(anyString())).thenReturn(respostaIA);
        when(planoEstudoRepository.findByUsuarioIdAndAtivoTrue(1L)).thenReturn(Optional.empty());
        when(planoEstudoRepository.findAllByUsuarioIdOrderByVersaoAsc(1L)).thenReturn(List.of());
        when(planoEstudoRepository.save(any(PlanoEstudo.class))).thenReturn(new PlanoEstudo());
        when(objectMapper.readTree(anyString())).thenAnswer(i -> realMapper.readTree((String) i.getArgument(0)));

        var resultado = onboardingService.enviarMensagem(1L, "Pronto");

        assertThat(resultado.onboardingConcluido()).isTrue();
        assertThat(usuario.getOnboardingConcluido()).isTrue();
        verify(planoEstudoRepository).save(any(PlanoEstudo.class));
        verify(planoEstudoParser).parsearEPopular(eq(usuario), anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção ao enviar review com onboarding não concluído")
    void enviarMensagemReview_onboardingNaoConcluido() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> onboardingService.enviarMensagemReview(1L, "Quero revisar"))
                .isInstanceOf(RegrasDeNegocioException.class)
                .hasMessage("Onboarding ainda não foi concluído.");
    }

    @Test
    @DisplayName("Deve processar review e desativar plano anterior")
    void enviarMensagemReview_desativaPlanoAnterior() throws Exception {
        usuario.setOnboardingConcluido(true);

        var planoAtivo = new PlanoEstudo();
        planoAtivo.setId(1L);
        planoAtivo.setAtivo(true);
        planoAtivo.setVersao(1);

        var json = "{\"vestibular\":\"ENEM\",\"materias\":[],\"semanas\":[]}";
        var respostaIA = "ONBOARDING_COMPLETO\n" + json;

        var realMapper = new com.fasterxml.jackson.databind.ObjectMapper();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(chatMensagemRepository.findAllByUsuarioIdOrderByCriadoEmAsc(1L)).thenReturn(List.of());
        when(aiClient.gerarResposta(anyString())).thenReturn(respostaIA);
        when(planoEstudoRepository.findByUsuarioIdAndAtivoTrue(1L)).thenReturn(Optional.of(planoAtivo));
        when(planoEstudoRepository.findAllByUsuarioIdOrderByVersaoAsc(1L)).thenReturn(List.of(planoAtivo));
        when(planoEstudoRepository.save(any(PlanoEstudo.class))).thenReturn(new PlanoEstudo());
        when(objectMapper.readTree(anyString())).thenAnswer(i -> realMapper.readTree((String) i.getArgument(0)));

        onboardingService.enviarMensagemReview(1L, "Quero revisar");

        assertThat(planoAtivo.getAtivo()).isFalse();
        verify(planoEstudoRepository).save(any(PlanoEstudo.class));
        verify(planoEstudoParser).parsearEPopular(eq(usuario), anyString());
    }
}