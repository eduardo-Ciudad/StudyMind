CREATE TABLE topicos (
    id         BIGSERIAL     PRIMARY KEY,
    nome       VARCHAR(150)  NOT NULL,
    descricao  VARCHAR(500),
    materia_id BIGINT        NOT NULL,
    nivel      VARCHAR(10)   NOT NULL,
    ativo      BOOLEAN       NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_topico_materia FOREIGN KEY (materia_id) REFERENCES materias(id)
);