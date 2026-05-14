ALTER TABLE plano_estudo
    DROP CONSTRAINT IF EXISTS plano_estudo_usuario_id_key;

ALTER TABLE plano_estudo
    ADD COLUMN versao INTEGER NOT NULL DEFAULT 1,
    ADD COLUMN ativo BOOLEAN NOT NULL DEFAULT TRUE;

CREATE INDEX idx_plano_estudo_ativo ON plano_estudo(usuario_id, ativo);