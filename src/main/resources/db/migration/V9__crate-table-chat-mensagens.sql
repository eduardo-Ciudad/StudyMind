CREATE TABLE chat_mensagens (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    conteudo TEXT NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_chat_mensagem_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE INDEX idx_chat_mensagens_usuario_id ON chat_mensagens(usuario_id);