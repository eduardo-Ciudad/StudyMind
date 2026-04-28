package com.eduardo.studymind.service;

import com.eduardo.studymind.domain.usuario.Usuario;
import com.eduardo.studymind.domain.usuario.UsuarioRepository;
import com.eduardo.studymind.dto.input.usuario.DadosAtualizacaoUsuario;
import com.eduardo.studymind.dto.input.usuario.DadosCadastroUsuario;
import com.eduardo.studymind.dto.output.usuario.DadosDetalhamentoUsuario;
import com.eduardo.studymind.dto.output.usuario.DadosListagemUsuario;
import com.eduardo.studymind.exception.RecursoNaoEncontradoException;
import com.eduardo.studymind.exception.RegrasDeNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public DadosDetalhamentoUsuario cadastrarUsuario(DadosCadastroUsuario dados){
        if(usuarioRepository.existsByEmail(dados.email())) {
            throw new RegrasDeNegocioException("E-mail já cadastrado");
        }

        var usuario = new Usuario();
        usuario.setNome(dados.nome());
        usuario.setEmail(dados.email());
        // criptografar a senha com springSecurity
        usuario.setSenha(dados.senha());
        usuario.setRole(dados.role());
        usuario.setAtivo(true);

        var usuarioSalvo = usuarioRepository.save(usuario);
        return  new DadosDetalhamentoUsuario(usuarioSalvo);

    }

    public Page<DadosListagemUsuario> listar(Pageable pageable) {
        return usuarioRepository.findAllByAtivoTrue(pageable)
                .map(DadosListagemUsuario::new);
    }

    public DadosDetalhamentoUsuario buscarPorId(Long id){
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));
        return new DadosDetalhamentoUsuario(usuario);
    }

    @Transactional
    public DadosDetalhamentoUsuario atualizarUsuario(Long id, DadosAtualizacaoUsuario dados){
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario não Encontrado"));

        if (dados.email() != null && !dados.email().equals(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(dados.email())) {
                throw  new RegrasDeNegocioException("E-mail já cadastrado");
            }
            usuario.setEmail(dados.email());
        }

        if (dados.nome() != null) usuario.setNome(dados.nome());

        return  new DadosDetalhamentoUsuario(usuario);
    }


    @Transactional
    public void desativar(Long id){
        var usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario nao encontrado"));
        usuario.setAtivo(false);
    }
}
