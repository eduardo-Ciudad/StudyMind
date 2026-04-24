CREATE TABLE materias (
    id        BIGSERIAL     PRIMARY KEY,
    nome      VARCHAR(100)  NOT NULL,
    descricao VARCHAR(255),
    ativa     BOOLEAN       NOT NULL DEFAULT TRUE
);