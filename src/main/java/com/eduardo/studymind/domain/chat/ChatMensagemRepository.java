package com.eduardo.studymind.domain.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMensagemRepository  extends JpaRepository<ChatMensagem, Long> {
    List<ChatMensagem> findAllByUsuarioIdOrderByCriadoEmAsc(Long usuarioId);
    void deleteAllByUsuarioId(Long usuarioId);
}
