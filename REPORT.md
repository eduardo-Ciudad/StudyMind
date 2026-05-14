# StudyMind — Relatório de Análise do Projeto

> Gerado em: 2026-05-14  
> Analisado por: Claude Code  
> Branch: main

---

## 1. Estrutura de Pacotes

**Pacote base:** `com.eduardo.studymind`

### Domain (Entidades e Repositórios)
| Pacote | Classes |
|--------|---------|
| `domain.usuario` | `Usuario`, `UsuarioRepository`, `Role` (enum) |
| `domain.materia` | `Materia`, `MateriaRepository` |
| `domain.topico` | `Topico`, `TopicoRepository`, `NivelDificuldade` (enum) |
| `domain.questao` | `Questao`, `QuestaoRepository`, `TipoQuestao` (enum) |
| `domain.resultado` | `Resultado`, `ResultadoRepository`, `RespostaStatus` (enum) |
| `domain.tarefa` | `Tarefa`, `TarefaRepository`, `TipoTarefa` (enum), `TarefaStatus` (enum) |
| `domain.plano` | `PlanoEstudo`, `PlanoEstudoRepository` |
| `domain.chat` | `ChatMensagem`, `ChatMensagemRepository`, `RoleChat` (enum) |

### DTOs
| Pacote | Classes |
|--------|---------|
| `dto.input.usuario` | `DadosCadastroUsuario`, `DadosAtualizacaoUsuario` |
| `dto.input.login` | `DadosLogin` |
| `dto.input.materia` | `DadosCadastroMateria`, `DadosAtualizacaoMateria` |
| `dto.input.topico` | `DadosCadastroTopico`, `DadosAtualizacaoTopico` |
| `dto.input.questao` | `DadosCadastroQuestao`, `DadosAtualizacaoQuestao` |
| `dto.input.resultado` | `DadosCadastroResultado` |
| `dto.input.tarefa` | `DadosCadastroTarefa`, `DadosAtualizacaoTarefa` |
| `dto.input.onboarding` | `DadosMensagemChat` |
| `dto.output.usuario` | `DadosListagemUsuario`, `DadosDetalhamentoUsuario` |
| `dto.output.jwt` | `DadosTokenJwt` |
| `dto.output.materia` | `DadosListagemMateria`, `DadosDetalhamentoMateria` |
| `dto.output.topico` | `DadosListagemTopico`, `DadosDetalhamentoTopico` |
| `dto.output.questao` | `DadosListagemQuestao`, `DadosDetalhamentoQuestao` |
| `dto.output.resultado` | `DadosListagemResultados`, `DadosDetalhamentoResultado` |
| `dto.output.tarefa` | `DadosListagemTarefa`, `DadosDetalhamentoTarefa` |
| `dto.output.performace` | `DadosDesempenhoTopico`, `DadosDesempenhoUsuario` |
| `dto.output.recomendacao` | `DadosRecomendacao` |
| `dto.output.onboarding` | `DadosStatusOnboarding`, `DadosRespostaChat` |
| `dto.output.plano` | `DadosPlanoEstudo` |
| `dto.output.erros` | `DadosErro` |

### Services
`UsuarioService`, `MateriaService`, `TopicoService`, `QuestaoService`, `ResultadoService`, `TarefaService`, `PerformanceAnalyzerService`, `OnboardingService`, `PlanoEstudoService`, `RecommendationService`

### Controllers
`UsuarioController`, `AuthController`, `MateriaController`, `TopicoController`, `QuestaoController`, `ResultadoController`, `TarefaController`, `DashboardController`, `DiagnosticoController`, `RecomendacaoController`, `OnboardingController`, `PlanoEstudoController`

### Infraestrutura
| Pacote | Classes |
|--------|---------|
| `infra.security.JwtService` | `JwtService` |
| `infra.security.SecurityConfig` | `SecurityConfig` |
| `infra.security.AuthFilter` | `AuthFilter` |
| `infra.security.UserDetailsServiceImpl` | `UserDetailsServiceImpl` |
| `infra.security.Utils` | `SecurityUtils` |
| `infra.ia` | `AIClient` (interface), `AnthropicClient` |

### Exceções
`GlobalExceptionHandler`, `RecursoNaoEncontradoException`, `RegrasDeNegocioException`

---

## 2. Entidades JPA

### `Usuario` → tabela `usuarios`
| Campo Java | Coluna SQL | Tipo SQL | Constraints |
|------------|------------|----------|-------------|
| `id` | `id` | BIGSERIAL | PK, NOT NULL |
| `nome` | `nome` | VARCHAR(100) | NOT NULL |
| `email` | `email` | VARCHAR(255) | NOT NULL, UNIQUE |
| `senha` | `senha` | VARCHAR(255) | NOT NULL |
| `role` | `role` | VARCHAR(20) | NOT NULL, enum: ADMIN/ALUNO |
| `ativo` | `ativo` | BOOLEAN | DEFAULT TRUE |
| `onboardingConcluido` | `onboarding_concluido` | BOOLEAN | DEFAULT FALSE |
| `criadoEm` | `criado_em` | TIMESTAMP | DEFAULT NOW(), updatable=false |

Implementa: `UserDetails` (Spring Security)  
Relacionamentos: nenhum (é referenciada pelas outras entidades)

---

### `Materia` → tabela `materias`
| Campo Java | Coluna SQL | Tipo SQL | Constraints |
|------------|------------|----------|-------------|
| `id` | `id` | BIGSERIAL | PK |
| `nome` | `nome` | VARCHAR(100) | NOT NULL |
| `descricao` | `descricao` | VARCHAR(255) | nullable |
| `ativa` | `ativa` | BOOLEAN | DEFAULT TRUE |

Relacionamentos: implícito via `Topico.materia` (OneToMany)

---

### `Topico` → tabela `topicos`
| Campo Java | Coluna SQL | Tipo SQL | Constraints |
|------------|------------|----------|-------------|
| `id` | `id` | BIGSERIAL | PK |
| `nome` | `nome` | VARCHAR(150) | NOT NULL |
| `descricao` | `descricao` | VARCHAR(500) | nullable |
| `materia` | `materia_id` | BIGINT | NOT NULL, FK → materias |
| `nivel` | `nivel` | VARCHAR(10) | NOT NULL, enum: FACIL/MEDIO/DIFICIL |
| `ativo` | `ativo` | BOOLEAN | DEFAULT TRUE |

Relacionamentos: `@ManyToOne(fetch=LAZY)` com `Materia`

---

### `Questao` → tabela `questoes`
| Campo Java | Coluna SQL | Tipo SQL | Constraints |
|------------|------------|----------|-------------|
| `id` | `id` | BIGSERIAL | PK |
| `enunciado` | `enunciado` | VARCHAR(1000) | NOT NULL |
| `tipo` | `tipo` | VARCHAR(20) | NOT NULL, enum: MULTIPLA_ESCOLHA/VERDADEIRO_FALSO/DISSERTATIVA |
| `topico` | `topico_id` | BIGINT | NOT NULL, FK → topicos |
| `ativa` | `ativa` | BOOLEAN | DEFAULT TRUE |

Relacionamentos: `@ManyToOne(fetch=LAZY)` com `Topico`

---

### `Resultado` → tabela `resultados`
| Campo Java | Coluna SQL | Tipo SQL | Constraints |
|------------|------------|----------|-------------|
| `id` | `id` | BIGSERIAL | PK |
| `usuario` | `usuario_id` | BIGINT | NOT NULL, FK → usuarios |
| `questao` | `questao_id` | BIGINT | NOT NULL, FK → questoes |
| `status` | `status` | VARCHAR(10) | NOT NULL, enum: CORRETO/INCORRETO/PULADO |
| `respostaUsuario` | `resposta_usuario` | VARCHAR(1000) | nullable |
| `respondidoEm` | `respondido_em` | TIMESTAMP | DEFAULT NOW(), updatable=false |

Relacionamentos: `@ManyToOne(fetch=LAZY)` com `Usuario` e `Questao`

---

### `Tarefa` → tabela `tarefas`
| Campo Java | Coluna SQL | Tipo SQL | Constraints |
|------------|------------|----------|-------------|
| `id` | `id` | BIGSERIAL | PK |
| `usuario` | `usuario_id` | BIGINT | NOT NULL, FK → usuarios |
| `topico` | `topico_id` | BIGINT | nullable, FK → topicos |
| `tipo` | `tipo` | VARCHAR(15) | NOT NULL, enum: QUESTOES/REVISAO/META_ACERTO |
| `descricao` | `descricao` | VARCHAR(255) | NOT NULL |
| `meta` | `meta` | INTEGER | NOT NULL |
| `prazo` | `prazo` | DATE | nullable |
| `status` | `status` | VARCHAR(15) | DEFAULT 'PENDENTE', enum: PENDENTE/EM_ANDAMENTO/CONCLUIDA/CANCELADA |
| `criadaEm` | `criada_em` | TIMESTAMP | DEFAULT NOW(), updatable=false |

Relacionamentos: `@ManyToOne(fetch=LAZY)` com `Usuario` (NOT NULL) e `Topico` (nullable)

---

### `ChatMensagem` → tabela `chat_mensagens`
| Campo Java | Coluna SQL | Tipo SQL | Constraints |
|------------|------------|----------|-------------|
| `id` | `id` | BIGSERIAL | PK |
| `usuario` | `usuario_id` | BIGINT | NOT NULL, FK → usuarios |
| `role` | `role` | VARCHAR(20) | NOT NULL, enum: USER/ASSISTANT |
| `conteudo` | `conteudo` | TEXT | NOT NULL |
| `criadoEm` | `criado_em` | TIMESTAMP | DEFAULT NOW(), updatable=false |

Relacionamentos: `@ManyToOne(fetch=LAZY)` com `Usuario`

---

### `PlanoEstudo` → tabela `plano_estudo`
| Campo Java | Coluna SQL | Tipo SQL | Constraints |
|------------|------------|----------|-------------|
| `id` | `id` | BIGSERIAL | PK |
| `usuario` | `usuario_id` | BIGINT | NOT NULL, UNIQUE, FK → usuarios |
| `conteudoJson` | `conteudo_json` | TEXT | NOT NULL |
| `criadoEm` | `criado_em` | TIMESTAMP | DEFAULT NOW(), updatable=false |

Relacionamentos: `@OneToOne(fetch=LAZY)` com `Usuario` (unique)

---

## 3. Migrações Flyway

**Caminho:** `src/main/resources/db/migration`

| Migração | O que faz | Consistente com Entidade? |
|----------|-----------|--------------------------|
| `V1__create-table-usuarios.sql` | Cria tabela `usuarios` com id, nome, email (UNIQUE), senha, role, ativo, criado_em | ✅ Sim |
| `V2__create-table-materias.sql` | Cria tabela `materias` com id, nome, descricao, ativa | ✅ Sim |
| `V3__crate-table-topicos.sql` | Cria tabela `topicos` com FK para materias, campo nivel (CHECK FACIL/MEDIO/DIFICIL) | ✅ Sim |
| `V4__crate-table-questoes.sql` | Cria tabela `questoes` com FK para topicos, campo tipo (CHECK MULTIPLA_ESCOLHA/VERDADEIRO_FALSO/DISSERTATIVA) | ✅ Sim |
| `V5__create-table-resultados.sql` | Cria tabela `resultados` com FKs para usuarios e questoes, campo status (CHECK CORRETO/INCORRETO/PULADO) | ✅ Sim |
| `V6__crate-table-tarefas.sql` | Cria tabela `tarefas` com FKs para usuarios e topicos (nullable), tipo, meta, prazo, status | ✅ Sim |
| `V7__create-indexes.sql` | Cria índices: idx_usuarios_email, idx_topicos_materia_id, idx_questoes_topico_id, idx_resultados_usuario_id, idx_resultados_questao_id, idx_resultados_status, idx_tarefas_usuario_id, idx_tarefas_status | ✅ Adequado |
| `V8__add-onboarding-usuarios.sql` | Adiciona coluna `onboarding_concluido BOOLEAN DEFAULT FALSE` em usuarios | ✅ Sim |
| `V9__crate-table-chat-mensagens.sql` | Cria tabela `chat_mensagens` com FK para usuarios, role (USER/ASSISTANT), conteudo (TEXT) | ✅ Sim |
| `V10__create-table-plano-estudo.sql` | Cria tabela `plano_estudo` com FK UNIQUE para usuarios, conteudo_json (TEXT) | ✅ Sim |

**Conclusão:** Todas as 10 migrações estão alinhadas com as entidades JPA. Nenhuma inconsistência estrutural encontrada.

> **Nota:** Três arquivos têm erro de digitação no nome ("crate" em vez de "create"): V3, V4 e V9. O Flyway processa corretamente, mas o nome é incorreto.

---

## 4. DTOs

### DTOs de Input (Entrada)

| DTO | Campos | Validações | Observações |
|-----|--------|------------|-------------|
| `DadosCadastroUsuario` | nome, email, senha | @NotBlank, @Email, @Size(min=8) | — |
| `DadosAtualizacaoUsuario` | nome, email, senha | @Email (opcional) | ⚠️ Campo `senha` existe mas não é processado no service |
| `DadosLogin` | email, senha | @NotBlank | — |
| `DadosCadastroMateria` | nome, descricao | @NotBlank em nome | — |
| `DadosAtualizacaoMateria` | nome, descricao, ativa | @NotBlank em nome | — |
| `DadosCadastroTopico` | nome, descricao, materiaId, nivelDificuldade | @NotBlank, @NotNull | — |
| `DadosAtualizacaoTopico` | nome, descricao, nivelDificuldade, ativo | todos opcionais | — |
| `DadosCadastroQuestao` | enunciado, tipo, topicoId | @NotBlank, @NotNull | — |
| `DadosAtualizacaoQuestao` | enunciado, tipo, ativa | todos opcionais | — |
| `DadosCadastroResultado` | usuarioId, questaoId, status, respostaUsuario | @NotNull em 3 primeiros | — |
| `DadosCadastroTarefa` | usuarioId, topicoId, tipo, descricao, meta, prazo | @NotNull, @Positive em meta | ⚠️ topicoId é @NotNull mas DB/entidade permitem null |
| `DadosAtualizacaoTarefa` | descricao, meta, prazo, status | @Positive em meta | — |
| `DadosMensagemChat` | mensagem | @NotNull | — |

### DTOs de Output (Saída)

| DTO | Campos | Observações |
|-----|--------|-------------|
| `DadosListagemUsuario` | id, nome, email, role, ativo | — |
| `DadosDetalhamentoUsuario` | id, nome, email, role, ativo, criadoEm | — |
| `DadosTokenJwt` | token | — |
| `DadosListagemMateria` | id, nome, ativa | — |
| `DadosDetalhamentoMateria` | id, nome, descricao, ativa | — |
| `DadosListagemTopico` | id, nome, nivel, materia (nome), ativo | — |
| `DadosDetalhamentoTopico` | id, nome, descricao, nivel, materiaId, materiaNome, ativo | — |
| `DadosListagemQuestao` | id, enunciado, tipo, topicoNome, ativa | — |
| `DadosDetalhamentoQuestao` | id, enunciado, tipo, topicoId, topicoNome, ativa | — |
| `DadosListagemResultados` | id, questaoId, status, respondidoEm | — |
| `DadosDetalhamentoResultado` | id, usuarioId, questaoId, questaoEnunciado, status, respostaUsuario, respondidoEm | — |
| `DadosListagemTarefa` | id, usuarioId, usuarioNome, topicoId, topicoNome, tipo, descricao, meta, prazo, status | trata topico null com segurança |
| `DadosDetalhamentoTarefa` | + criadaEm | trata topico null com segurança |
| `DadosDesempenhoTopico` | topicoId, topicoNome, materiaNome, totalRespostas, totalAcertos, taxaAcerto | — |
| `DadosDesempenhoUsuario` | usuarioId, totalRespostas, totalAcertos, taxaAcertoGeral, desempenhoPorTopico, topicosMaisFracos | — |
| `DadosRecomendacao` | usuarioId, diagnostico, topicosPrioritarios, dicasPraticas, mensagemMotivacional, taxaAcertoGeral | — |
| `DadosStatusOnboarding` | usuarioId, onboardingConcluido | — |
| `DadosRespostaChat` | resposta, onboardingConcluido | — |
| `DadosPlanoEstudo` | id, usuarioId, conteudoJson, criadoEm | — |
| `DadosErro` | status, mensagem | — |

### Desalinhamentos Identificados

- `DadosAtualizacaoUsuario.senha` existe no DTO mas o `UsuarioService` nunca o processa — alteração de senha silenciosamente ignorada.
- `DadosCadastroTarefa.topicoId` tem `@NotNull`, porém a coluna `topico_id` na tabela `tarefas` é nullable e a entidade `Tarefa` também permite null. Os DTOs de saída (`DadosListagemTarefa`, `DadosDetalhamentoTarefa`) já tratam topico nulo, criando inconsistência na regra de criação versus leitura.

---

## 5. Services

### `UsuarioService`
**Dependências:** `UsuarioRepository`, `PasswordEncoder`

| Método | Descrição |
|--------|-----------|
| `cadastrarUsuario(DadosCadastroUsuario)` | Valida unicidade de email, codifica senha (BCrypt), define Role=ALUNO, retorna DTO de detalhe |
| `listar(Pageable)` | Retorna usuários ativos paginados |
| `buscarPorId(Long)` | Lança `RecursoNaoEncontradoException` se não encontrado |
| `atualizarUsuario(Long, DadosAtualizacaoUsuario)` | Atualiza nome/email (valida unicidade); **ignora campo senha** |
| `desativar(Long)` | Define `ativo=false` (soft delete) |

---

### `MateriaService`
**Dependências:** `MateriaRepository`

| Método | Descrição |
|--------|-----------|
| `cadastrarMateria(DadosCadastroMateria)` | Cria matéria |
| `listarMateria()` | Retorna todas as matérias |
| `buscarPorID(Long)` | Lança exceção se não encontrado |
| `atualizarMateria(Long, DadosAtualizacaoMateria)` | Atualiza campos fornecidos |
| `desativarMateria(Long)` | Define `ativa=false` |

---

### `TopicoService`
**Dependências:** `TopicoRepository`, `MateriaRepository`

| Método | Descrição |
|--------|-----------|
| `cadastrarTopico(DadosCadastroTopico)` | Valida existência da matéria e unicidade do nome por matéria |
| `listarTopicos(Long materiaId)` | Filtra por matéria se fornecida, senão retorna todos ativos |
| `buscarPorId(Long)` | Lança exceção se não encontrado |
| `atualizar(Long, DadosAtualizacaoTopico)` | Atualiza campos fornecidos |
| `desativar(Long)` | Define `ativo=false` |

---

### `QuestaoService`
**Dependências:** `QuestaoRepository`, `TopicoRepository`

| Método | Descrição |
|--------|-----------|
| `cadastrar(DadosCadastroQuestao)` | Valida existência do tópico |
| `listar(Pageable)` | Retorna questões ativas paginadas |
| `atualizar(Long, DadosAtualizacaoQuestao)` | Atualiza campos fornecidos |
| `inativar(Long)` | Define `ativa=false` |

---

### `ResultadoService`
**Dependências:** `ResultadoRepository`, `QuestaoRepository`, `UsuarioRepository`

| Método | Descrição |
|--------|-----------|
| `cadastrar(DadosCadastroResultado)` | Valida existência de usuário e questão antes de salvar |
| `listarPorUsuario(Long, Pageable)` | `@Transactional(readOnly=true)`, paginado |
| `detalharResultado(Long)` | `@Transactional(readOnly=true)` |

---

### `TarefaService`
**Dependências:** `TarefaRepository`, `UsuarioRepository`, `TopicoRepository`

| Método | Descrição |
|--------|-----------|
| `cadastrar(DadosCadastroTarefa)` | Valida usuário e tópico; impede tarefas PENDENTE duplicadas para (usuario, topico) |
| `listarPorUsuario(Long, TarefaStatus)` | Filtra por status se fornecido; senão exclui CANCELADA |
| `atualizarTarefa(Long, DadosAtualizacaoTarefa)` | Impede atualização de tarefas CONCLUIDA ou CANCELADA |
| `cancelar(Long)` | Define `status=CANCELADA`; impede cancelar CONCLUIDA |

---

### `PerformanceAnalyzerService`
**Dependências:** `ResultadoRepository`, `TopicoRepository`

| Método | Descrição |
|--------|-----------|
| `analisarDesempenho(Long usuarioId)` | Busca todos os tópicos ativos, calcula desempenho por tópico (total/acertos/taxa), filtra tópicos sem resposta, identifica 5 mais fracos |
| `calcularDesempenhoTopico(Long, Topico)` | Método interno; executa contagens individuais por tópico via repository |

> **Atenção:** Problema N+1 — para cada tópico ativo, executa 2 queries (`countByUsuarioIdAndQuestaoTopicoId` e `countByUsuarioIdAndQuestaoTopicoIdAndStatus`). Com muitos tópicos, pode degradar o desempenho.

---

### `OnboardingService`
**Dependências:** `ChatMensagemRepository`, `PlanoEstudoRepository`, `UsuarioRepository`, `AIClient`, `ObjectMapper`

| Método | Descrição |
|--------|-----------|
| `getStatus(Long)` | Retorna flag `onboardingConcluido` do usuário |
| `enviarMensagem(Long, String)` | Salva mensagem do usuário, busca histórico, chama IA, detecta marcador "ONBOARDING_COMPLETO", salva plano e finaliza onboarding se necessário |
| `montarPromptComHistorico(List<ChatMensagem>)` | Interno; constrói system prompt + histórico da conversa |
| `salvarPlanoEFinalizar(Usuario, String)` | Interno; extrai JSON da resposta, valida estrutura, salva `PlanoEstudo`, atualiza `onboardingConcluido=true` |

**System Prompt pergunta sobre:** vestibular desejado, data prevista, matérias, nível atual, horas/dia disponíveis, pontos fortes e fracos.

---

### `PlanoEstudoService`
**Dependências:** `PlanoEstudoRepository`

| Método | Descrição |
|--------|-----------|
| `buscarPorUsuario(Long)` | Busca plano de estudos; lança `RecursoNaoEncontradoException` se não existir |

---

### `RecommendationService`
**Dependências:** `PerformanceAnalyzerService`, `AIClient`, `ObjectMapper`

| Método | Descrição |
|--------|-----------|
| `gerarRecomendacao(Long)` | Obtém desempenho via `PerformanceAnalyzerService`, monta prompt com dados, chama IA, parseia JSON (trata blocos markdown), retorna `DadosRecomendacao` |

---

## 6. Controllers

### `AuthController` — `/auth`
| Método | Endpoint | Body | Resposta | Segurança |
|--------|----------|------|----------|-----------|
| POST | `/login` | `DadosLogin` | `DadosTokenJwt` (200) | Pública |
| POST | `/registro` | `DadosCadastroUsuario` | `DadosDetalhamentoUsuario` (201) | Pública |

---

### `UsuarioController` — `/usuarios`
| Método | Endpoint | Body/Params | Resposta | Segurança |
|--------|----------|-------------|----------|-----------|
| POST | `/` | `DadosCadastroUsuario` | `DadosDetalhamentoUsuario` (201) | Pública* |
| GET | `/` | Pageable (default size=10) | `Page<DadosListagemUsuario>` (200) | JWT |
| GET | `/{id}` | — | `DadosDetalhamentoUsuario` (200) | JWT |
| PUT | `/{id}` | `DadosAtualizacaoUsuario` | `DadosDetalhamentoUsuario` (200) | JWT |
| DELETE | `/{id}` | — | 204 No Content | JWT |

*Duplica funcionalidade de `/auth/registro`

---

### `MateriaController` — `/materias`
| Método | Endpoint | Body | Resposta | Segurança |
|--------|----------|------|----------|-----------|
| POST | `/` | `DadosCadastroMateria` | `DadosDetalhamentoMateria` (201) | JWT |
| GET | `/` | — | `List<DadosListagemMateria>` (200) | JWT |
| GET | `/{id}` | — | `DadosDetalhamentoMateria` (200) | JWT |
| PUT | `/{id}` | `DadosAtualizacaoMateria` | `DadosDetalhamentoMateria` (200) | JWT |
| DELETE | `/{id}` | — | 204 No Content | JWT |

---

### `TopicoController` — `/topicos`
| Método | Endpoint | Body/Params | Resposta | Segurança |
|--------|----------|-------------|----------|-----------|
| POST | `/` | `DadosCadastroTopico` | `DadosDetalhamentoTopico` (201) | JWT |
| GET | `/` | QueryParam `materiaID` (opcional) | `List<DadosListagemTopico>` (200) | JWT |
| GET | `/{id}` | — | `DadosDetalhamentoTopico` (200) | JWT |
| PUT | `/{id}` | `DadosAtualizacaoTopico` | `DadosDetalhamentoTopico` (200) | JWT |
| DELETE | `/{id}` | — | 204 No Content | JWT |

---

### `QuestaoController` — `/questoes`
| Método | Endpoint | Body/Params | Resposta | Segurança |
|--------|----------|-------------|----------|-----------|
| POST | `/` | `DadosCadastroQuestao` | `DadosDetalhamentoQuestao` (201) | JWT |
| GET | `/` | Pageable (default size=10) | `Page<DadosListagemQuestao>` (200) | JWT |
| PUT | `/{id}` | `DadosAtualizacaoQuestao` | `DadosDetalhamentoQuestao` (200) | JWT |
| DELETE | `/{id}` | — | 204 No Content | JWT |

---

### `ResultadoController` — `/resultados`
| Método | Endpoint | Body/Params | Resposta | Segurança |
|--------|----------|-------------|----------|-----------|
| POST | `/` | `DadosCadastroResultado` | `DadosDetalhamentoResultado` (201) | JWT |
| GET | `/usuario/{usuarioId}` | Pageable | `Page<DadosListagemResultados>` (200) | JWT + Ownership |
| GET | `/{id}` | — | `DadosDetalhamentoResultado` (200) | JWT + Ownership |

---

### `TarefaController` — `/tarefas`
| Método | Endpoint | Body/Params | Resposta | Segurança |
|--------|----------|-------------|----------|-----------|
| POST | `/` | `DadosCadastroTarefa` | `DadosDetalhamentoTarefa` (201) | JWT |
| GET | `/usuario/{usuarioId}` | QueryParam `status` (opcional) | `List<DadosListagemTarefa>` (200) | JWT + Ownership |
| PUT | `/{id}` | `DadosAtualizacaoTarefa` | `DadosDetalhamentoTarefa` (200) | JWT |
| DELETE | `/{id}` | — | 204 No Content | JWT |

---

### `DashboardController` — `/dashboard`
| Método | Endpoint | Resposta | Segurança |
|--------|----------|----------|-----------|
| GET | `/usuario/{usuarioId}` | `DadosDesempenhoUsuario` (200) | JWT + Ownership |

---

### `DiagnosticoController` — `/diagnostico`
| Método | Endpoint | Resposta | Segurança |
|--------|----------|----------|-----------|
| GET | `/usuario/{usuarioId}` | `List<DadosDesempenhoTopico>` (200) | JWT + Ownership |

---

### `RecomendacaoController` — `/recomendacao`
| Método | Endpoint | Resposta | Segurança |
|--------|----------|----------|-----------|
| GET | `/usuario/{usuarioId}` | `DadosRecomendacao` (200) | JWT + Ownership |

---

### `OnboardingController` — `/onboarding`
| Método | Endpoint | Body | Resposta | Segurança |
|--------|----------|------|----------|-----------|
| GET | `/status/{usuarioId}` | — | `DadosStatusOnboarding` (200) | JWT + Ownership |
| POST | `/mensagem/{usuarioId}` | `DadosMensagemChat` | `DadosRespostaChat` (200) | JWT + Ownership |

---

### `PlanoEstudoController` — `/plano-estudo/usuario/{usuarioId}`
| Método | Endpoint | Resposta | Segurança |
|--------|----------|----------|-----------|
| GET | `/` | `DadosPlanoEstudo` (200) | JWT + Ownership |

---

## 7. Segurança

### JWT — `JwtService`
- **Biblioteca:** `com.auth0:java-jwt:4.4.0`
- **Algoritmo:** HMAC256
- **Segredo:** `${api.security.token.secret}` (variável de ambiente)
- **Emissor (issuer):** `studymind-api`
- **Expiração:** 2 horas a partir da emissão
- **Claims do token:**
  - `sub` (subject): `usuario.email`
  - `role`: `usuario.role.name()`
  - `id`: `usuario.id`
  - `exp`: `Instant.now().plus(2, HOURS)`

### `AuthFilter` — Fluxo de Autenticação
1. Extrai header `Authorization: Bearer <token>`
2. Valida token via `JwtService.validarToken()` → retorna o email
3. Se válido: carrega `UserDetails` via `UserDetailsServiceImpl`
4. Cria `UsernamePasswordAuthenticationToken` com authorities
5. Injeta no `SecurityContextHolder`

### `SecurityConfig` — Rotas
| Tipo | Rotas |
|------|-------|
| **Públicas** | POST `/auth/login`, POST `/auth/registro`, `/v3/api-docs/**`, `/swagger-ui/**` |
| **Protegidas** | Todas as demais (requerem JWT válido) |

> **Nota:** A seção `.requestMatchers("/admin/**").hasRole("ADMIN")` está comentada no código — controle de acesso por role não está efetivo no roteamento.

### `SecurityUtils` — Verificação de Ownership
- Admins (`Role.ADMIN`) podem acessar dados de qualquer usuário
- Alunos (`Role.ALUNO`) só podem acessar seus próprios dados
- Lança `403 Forbidden` se `usuarioId` não corresponde ao autenticado

### CORS
```
Origens permitidas: http://127.0.0.1:5500, http://localhost:5500, http://localhost:3000
Métodos: GET, POST, PUT, DELETE, OPTIONS
Headers: * (todos)
Credentials: true
```

---

## 8. Integração Anthropic

### `AIClient` (interface)
```java
String gerarResposta(String prompt);
```

### `AnthropicClient` (implementação)
| Configuração | Valor |
|-------------|-------|
| Endpoint | `https://api.anthropic.com/v1/messages` |
| Modelo | `claude-haiku-4-5-20251001` |
| Max Tokens | `4096` |
| API Key | `${anthropic.api.key}` |
| anthropic-version | `2023-06-01` |

**Formato de request:**
```json
{
  "model": "claude-haiku-4-5-20251001",
  "max_tokens": 4096,
  "messages": [{ "role": "user", "content": "<prompt>" }]
}
```
A resposta é extraída de `response.content[0].text`.

---

### Fluxo do Onboarding (OnboardingService → AIClient)

```
POST /onboarding/mensagem/{usuarioId}
    │
    ├─ Salva mensagem do usuário em chat_mensagens (role=USER)
    ├─ Busca todo o histórico do usuário (findAllByUsuarioIdOrderByCriadoEmAsc)
    ├─ Monta prompt: SYSTEM_PROMPT + histórico da conversa
    ├─ Chama AIClient.gerarResposta(prompt)
    │
    ├─ [SE resposta contém "ONBOARDING_COMPLETO"]
    │       ├─ Extrai JSON após o marcador
    │       ├─ Valida estrutura do JSON via ObjectMapper
    │       ├─ Salva em plano_estudo (PlanoEstudoRepository.save)
    │       └─ Atualiza usuario.onboardingConcluido = true
    │
    ├─ Salva resposta da IA em chat_mensagens (role=ASSISTANT)
    └─ Retorna DadosRespostaChat { resposta, onboardingConcluido }
```

**System Prompt coleta:**
1. Vestibular desejado (FUVEST, ENEM, UNICAMP, etc.)
2. Data prevista do exame
3. Matérias para estudar
4. Nível atual em cada matéria
5. Horas por dia disponíveis
6. Pontos fortes e fracos

---

### Fluxo de Recomendação (RecommendationService → AIClient)

```
GET /recomendacao/usuario/{usuarioId}
    │
    ├─ PerformanceAnalyzerService.analisarDesempenho(usuarioId)
    │       └─ Calcula desempenho por tópico, identifica 5 mais fracos
    │
    ├─ Monta prompt com:
    │       ├─ Total de respostas e taxa de acerto geral
    │       └─ Lista dos 5 tópicos mais fracos com detalhes
    │
    ├─ Chama AIClient.gerarResposta(prompt)
    ├─ Parseia JSON (trata blocos markdown ```json ... ```)
    └─ Retorna DadosRecomendacao { diagnostico, topicosPrioritarios,
                                    dicasPraticas, mensagemMotivacional,
                                    taxaAcertoGeral }
```

---

## 9. Testes

### Classes de Teste Existentes

| Classe | Tipo | Quantidade de Testes |
|--------|------|---------------------|
| `StudymindApplicationTests` | Contexto (smoke test) | 1 |
| `UsuarioServiceTest` | Unitário (Mockito) | 6 |
| `MateriaServiceTest` | Unitário (Mockito) | 6 |
| `TopicoServiceTest` | Unitário (Mockito) | 5 |
| `QuestaoServiceTest` | Unitário (Mockito) | 5 |
| `ResultadoServiceTest` | Unitário (Mockito) | 4 |
| `TarefaServiceTest` | Unitário (Mockito) | 6 |
| `AuthControllerTest` | Integração (MockMvc) | 4 |

**Total:** ~37 testes

### Cenários Cobertos (por service)

**UsuarioService:** cadastro com sucesso, email duplicado, busca por id, id não encontrado, atualização com email duplicado, desativação  
**MateriaService:** cadastro, listagem, busca por id, id não encontrado, desativação com e sem existência  
**TopicoService:** cadastro com sucesso, nome duplicado por matéria, listagem, id não encontrado, desativação  
**QuestaoService:** cadastro com sucesso, tópico não encontrado, atualização com id não encontrado, inativação com e sem existência  
**ResultadoService:** cadastro com sucesso, usuário não encontrado, questão não encontrada, detalhe não encontrado  
**TarefaService:** cadastro com sucesso, tarefa duplicada, usuário não encontrado, atualização de tarefa concluída, cancelamento com e sem conflito  
**AuthController:** login com sucesso, login sem email, registro com sucesso, registro com senha fraca

### Cobertura Ausente

- `TopicoService.listarTopicos(materiaId)` — filtro por matéria não testado
- `TopicoService.atualizar()` — nenhum teste
- `QuestaoService.listar()` — nenhum teste
- `TarefaService.listarPorUsuario()` — nenhum teste
- `TarefaService.atualizarTarefa()` — nenhum teste
- `PerformanceAnalyzerService` — **zero cobertura**
- `OnboardingService` — **zero cobertura** (fluxo crítico de IA sem testes)
- `RecommendationService` — **zero cobertura**
- `PlanoEstudoService` — **zero cobertura**
- Controllers (exceto Auth) — **zero cobertura**
- `JwtService` — nenhum teste unitário
- `AuthFilter` — nenhum teste
- `SecurityUtils` — nenhum teste

---

## 10. Problemas Encontrados

### Críticos

**[C1] Atualização de senha não funciona**  
`DadosAtualizacaoUsuario` possui campo `senha`, mas `UsuarioService.atualizarUsuario()` nunca o processa nem o codifica.  
Usuários não conseguem alterar a senha. A mudança é silenciosamente ignorada.

**[C2] Inconsistência em `topicoId` de Tarefa**  
- `DadosCadastroTarefa.topicoId` tem `@NotNull` (obrigatório na criação)
- Coluna `tarefas.topico_id` é nullable no banco
- Entidade `Tarefa.topico` permite null (`@JoinColumn` sem `nullable=false`)
- DTOs de saída já tratam topico nulo
- Regra de criação é mais restritiva que o esquema — inconsistência de design

**[C3] Ausência de Cascade Delete**  
Nenhuma entidade define política de cascata (`cascade`, `orphanRemoval`).  
Deletar um `Usuario` deixará registros órfãos em `chat_mensagens`, `resultados`, `tarefas` e `plano_estudo`.

**[C4] Segredo JWT sem valor padrão**  
`application.properties` referencia `${JWT_SECRET}` sem fallback.  
A aplicação não inicializa se a variável de ambiente não estiver definida (afeta testes de integração também).

---

### Segurança

**[S1] Senha do banco de dados no código-fonte**  
`application.properties` contém `spring.datasource.password=Vanda1107.` em texto puro.  
Não deve estar no controle de versão.

**[S2] CORS hardcoded para desenvolvimento**  
Origins permitidas (`localhost:5500`, `localhost:3000`) estão fixas no código.  
Qualquer deploy de produção exige modificação do código ou do `application.properties`.

**[S3] Dados sensíveis não criptografados**  
`chat_mensagens.conteudo` e `plano_estudo.conteudo_json` guardam informações pessoais do aluno em texto puro.

**[S4] Endpoint duplicado de criação de usuário**  
`POST /usuarios` e `POST /auth/registro` fazem a mesma operação.  
O `POST /usuarios` está efetivamente público (não aparece na whitelist mas não há restrição explícita).

**[S5] Controle de acesso por role incompleto**  
`.requestMatchers("/admin/**").hasRole("ADMIN")` está comentado em `SecurityConfig`.  
Nenhum endpoint admin foi criado; o enum `ADMIN` existe mas não tem efeito sobre roteamento.

---

### Desempenho

**[P1] Problema N+1 em `PerformanceAnalyzerService`**  
Para cada tópico ativo, são disparadas 2 queries de contagem separadas (`countByUsuarioIdAndQuestaoTopicoId` e `countByUsuarioIdAndQuestaoTopicoIdAndStatus`).  
Com 50 tópicos = 100 queries por requisição ao dashboard.

**[P2] Histórico de chat não paginado**  
`ChatMensagemRepository.findAllByUsuarioIdOrderByCriadoEmAsc()` retorna todas as mensagens do usuário.  
Usuários com muitas mensagens consumirão memória crescente e enviarão contextos enormes para a IA.

---

### Fragilidade da Integração com IA

**[A1] Parser frágil do marcador "ONBOARDING_COMPLETO"**  
`OnboardingService` depende de formato exato na resposta da IA: `"ONBOARDING_COMPLETO\n{json}"`.  
Se o modelo inserir texto adicional ou formatar diferente, o `indexOf` falha e lança `RuntimeException`.  
Não há retry, fallback ou validação de formato.

**[A2] JSON do plano de estudos sem schema definido**  
`plano_estudo.conteudo_json` aceita qualquer JSON válido.  
`RecommendationService` e outros consumidores assumem estrutura específica sem validação formal.

**[A3] Sem fallback para IA indisponível**  
Se a API Anthropic retornar erro HTTP, `AnthropicClient` propagará exceção não tratada.  
Os endpoints `/onboarding/mensagem` e `/recomendacao` não têm tratamento de erro específico para falha da IA.

---

### Qualidade de Código

**[Q1] Erros de digitação em nomes de migrations**  
V3, V4 e V9 usam `"crate"` em vez de `"create"` no nome do arquivo. Não impede funcionamento mas prejudica legibilidade.

**[Q2] Imports não utilizados**  
`UsuarioController` e `TopicoController` possuem imports desnecessários (`org.apache.tomcat.util.digester.Rule`, `org.w3c.dom.stylesheets.LinkStyle`).

**[Q3] Mensagens de exceção inconsistentes**  
Sem padronização: algumas capitalizam ("Usuario nao encontrado"), outras não, algumas com acentos ("MAtéria nao encontrada").

**[Q4] Pacotes com sub-pacotes redundantes**  
`infra.security.JwtService.JwtService`, `infra.security.SecurityConfig.SecurityConfig` etc. — o pacote tem o mesmo nome da classe, criando hierarquia desnecessária.

---

### Design

**[D1] Sem auditoria de alterações**  
Nenhuma entidade registra `updated_at`, `updated_by` ou histórico de mudanças.

**[D2] Soft delete inconsistente**  
`Materia`, `Topico`, `Questao` têm flag `ativa/ativo` (soft delete).  
`Usuario` usa `ativo=false` mas nenhum endpoint exclui de fato.  
`Resultado` e `ChatMensagem` não têm soft delete — deletar é permanente.

**[D3] Sem documentação dos endpoints**  
`springdoc-openapi-starter-webmvc-ui` está no `pom.xml` e a UI está habilitada, mas nenhum controller usa `@Operation`, `@ApiResponse` ou `@Tag`. A documentação gerada é incompleta.

**[D4] Sem README ou documentação de setup**  
Projeto não tem instruções de configuração, variáveis de ambiente necessárias ou diagrama de banco de dados.

---

## Resumo Executivo

| Categoria | Total |
|-----------|-------|
| Problemas críticos | 4 |
| Problemas de segurança | 5 |
| Problemas de desempenho | 2 |
| Problemas na integração IA | 3 |
| Problemas de qualidade | 4 |
| Problemas de design | 4 |
| **Total** | **22** |

**Cobertura de testes:** ~37 testes existentes; 5 services críticos sem nenhuma cobertura.

**Prioridade de ação sugerida:**
1. Remover segredos do `application.properties` (senha do banco, definir JWT_SECRET)
2. Implementar atualização de senha corretamente
3. Adicionar cascade delete ou definir comportamento esperado ao remover usuário
4. Adicionar testes para `OnboardingService`, `PerformanceAnalyzerService` e `RecommendationService`
5. Resolver inconsistência do `topicoId` obrigatório vs nullable no banco
6. Tornar CORS configurável via variável de ambiente
7. Corrigir problema N+1 no `PerformanceAnalyzerService`
8. Adicionar tratamento de erro para falha da API Anthropic
