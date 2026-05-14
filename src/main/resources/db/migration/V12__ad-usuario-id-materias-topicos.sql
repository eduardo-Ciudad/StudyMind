ALTER TABLE materias
    ADD COLUMN usuario_id BIGINT NOT NULL DEFAULT 1;

ALTER TABLE materias
    ADD CONSTRAINT fk_materias_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id);

ALTER TABLE materias
    DROP CONSTRAINT IF EXISTS materias_nome_key;

ALTER TABLE topicos
    ADD COLUMN usuario_id BIGINT NOT NULL DEFAULT 1;

ALTER TABLE topicos
    ADD CONSTRAINT fk_topicos_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id);

CREATE INDEX idx_materias_usuario_id ON materias(usuario_id);
CREATE INDEX idx_topicos_usuario_id ON topicos(usuario_id);