CREATE TABLE usuarios (
    id          BIGSERIAL PRIMARY KEY,
    nome        VARCHAR(100)        NOT NULL,
    email       VARCHAR(255)        NOT NULL UNIQUE,
    senha       VARCHAR(255)        NOT NULL,
    role        VARCHAR(20)         NOT NULL,
    ativo       BOOLEAN             NOT NULL DEFAULT TRUE,
    criado_em   TIMESTAMP           NOT NULL DEFAULT NOW()
);