CREATE TABLE resultados (
    id               BIGSERIAL     PRIMARY KEY,
    usuario_id       BIGINT        NOT NULL,
    questao_id       BIGINT        NOT NULL,
    status           VARCHAR(10)   NOT NULL,
    resposta_usuario VARCHAR(1000),
    respondido_em    TIMESTAMP     NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_resultado_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    CONSTRAINT fk_resultado_questao FOREIGN KEY (questao_id) REFERENCES questoes(id)
);