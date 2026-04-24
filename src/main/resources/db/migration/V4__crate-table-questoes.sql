CREATE TABLE questoes (
    id         BIGSERIAL      PRIMARY KEY,
    enunciado  VARCHAR(1000)  NOT NULL,
    tipo       VARCHAR(20)    NOT NULL,
    topico_id  BIGINT         NOT NULL,
    ativa      BOOLEAN        NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_questao_topico FOREIGN KEY (topico_id) REFERENCES topicos(id)
);