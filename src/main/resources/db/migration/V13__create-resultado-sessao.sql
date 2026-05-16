CREATE TABLE resultado_sessoes (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
    topico_nome VARCHAR(150) NOT NULL,
    materia_nome VARCHAR(100) NOT NULL,
    total_questoes INT NOT NULL,
    acertos INT NOT NULL,
    taxa_acerto DECIMAL(5,2) NOT NULL,
    respondido_em TIMESTAMP NOT NULL DEFAULT NOW()
);