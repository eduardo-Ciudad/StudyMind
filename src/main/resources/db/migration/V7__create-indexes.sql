CREATE INDEX idx_usuarios_email ON usuarios(email);

CREATE INDEX idx_topicos_materia_id ON topicos(materia_id);

CREATE INDEX idx_questoes_topico_id ON questoes(topico_id);

CREATE INDEX idx_resultados_usuario_id ON resultados(usuario_id);
CREATE INDEX idx_resultados_questao_id ON resultados(questao_id);

CREATE INDEX idx_tarefas_usuario_id ON tarefas(usuario_id);
CREATE INDEX idx_tarefas_topico_id ON tarefas(topico_id);
CREATE INDEX idx_tarefas_status ON tarefas(status);