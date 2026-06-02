ALTER TABLE resultado_sessoes
    ADD COLUMN topico_id BIGINT,
    ADD COLUMN materia_id BIGINT;

ALTER TABLE resultado_sessoes
    ADD CONSTRAINT fk_resultado_sessao_topico
        FOREIGN KEY (topico_id) REFERENCES topicos(id);

ALTER TABLE resultado_sessoes
    ADD CONSTRAINT fk_resultado_sessao_materia
        FOREIGN KEY (materia_id) REFERENCES materias(id);