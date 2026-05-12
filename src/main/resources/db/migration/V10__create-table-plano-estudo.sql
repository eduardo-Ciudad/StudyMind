CREATE TABLE plano_estudo (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE,
    conteudo_json TEXT NOT NULL,
    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_plano_estudo_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE INDEX idx_plano_estudo_usuario_id ON plano_estudo(usuario_id);