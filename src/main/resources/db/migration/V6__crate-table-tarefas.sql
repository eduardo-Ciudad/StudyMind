CREATE TABLE tarefas (
    id          BIGSERIAL     PRIMARY KEY,
    usuario_id  BIGINT        NOT NULL,
    topico_id   BIGINT,
    tipo        VARCHAR(15)   NOT NULL,
    descricao   VARCHAR(255)  NOT NULL,
    meta        INTEGER       NOT NULL,
    prazo       DATE,
    status      VARCHAR(15)   NOT NULL DEFAULT 'PENDENTE',
    criada_em   TIMESTAMP     NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_tarefa_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    CONSTRAINT fk_tarefa_topico  FOREIGN KEY (topico_id)  REFERENCES topicos(id)
);