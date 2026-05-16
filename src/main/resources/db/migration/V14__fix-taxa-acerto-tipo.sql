ALTER TABLE resultado_sessoes
ALTER COLUMN taxa_acerto TYPE FLOAT8 USING taxa_acerto::FLOAT8;