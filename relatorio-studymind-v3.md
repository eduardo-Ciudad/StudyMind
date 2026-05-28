# Relatório Técnico — StudyMind v3

> Gerado em: 2026-05-28  
> Projeto: `com.eduardo.studymind`  
> Stack: Spring Boot 3.5.14 · Java 17 · PostgreSQL · Flyway · JWT · Anthropic Claude API

---

## 1. Estrutura de Pacotes

```
com.eduardo.studymind
├── StudymindApplication.java                  (entry point)
│
├── controller/
│   ├── AuthController.java
│   ├── AulaController.java
│   ├── DashboardController.java
│   ├── DiagnosticoController.java
│   ├── MateriaController.java
│   ├── OnboardingController.java
│   ├── PlanoEstudoController.java
│   ├── QuestaoController.java
│   ├── RecomendacaoController.java
│   ├── ResultadoController.java
│   ├── ResultadoSessaoController.java
│   ├── TarefaController.java
│   ├── TopicoController.java
│   └── UsuarioController.java
│
├── domain/
│   ├── chat/
│   │   ├── ChatMensagem.java          (entity)
│   │   ├── ChatMensagemRepository.java
│   │   └── RoleChat.java              (enum: USER, ASSISTANT)
│   ├── materia/
│   │   ├── Materia.java               (entity)
│   │   └── MateriaRepository.java
│   ├── plano/
│   │   ├── PlanoEstudo.java           (entity)
│   │   └── PlanoEstudoRepository.java
│   ├── questao/
│   │   ├── Questao.java               (entity)
│   │   ├── QuestaoRepository.java
│   │   └── TipoQuestao.java           (enum: MULTIPLA_ESCOLHA, VERDADEIRO_FALSO, DISSERTATIVA)
│   ├── resultado/
│   │   ├── RespostaStatus.java        (enum: CORRETO, INCORRETO, PULADO)
│   │   ├── Resultado.java             (entity)
│   │   ├── ResultadoRepository.java
│   │   ├── ResultadoSessao.java       (entity)
│   │   └── ResultadoSessaoRepository.java
│   ├── tarefa/
│   │   ├── Tarefa.java                (entity)
│   │   ├── TarefaRepository.java
│   │   ├── TarefaStatus.java          (enum: PENDENTE, EM_ANDAMENTO, CONCLUIDA, CANCELADA)
│   │   └── TipoTarefa.java            (enum: QUESTOES, REVISAO, META_ACERTO)
│   ├── token/
│   │   ├── TokenVerificacao.java      (entity)
│   │   └── TokenVerificacaoRepository.java
│   ├── topico/
│   │   ├── NivelDificuldade.java      (enum: FACIL, MEDIO, DIFICIL)
│   │   ├── Topico.java                (entity)
│   │   └── TopicoRepository.java
│   └── usuario/
│       ├── Role.java                  (enum: ADMIN, ALUNO)
│       ├── Usuario.java               (entity, implements UserDetails)
│       └── UsuarioRepository.java
│
├── dto/
│   ├── input/
│   │   ├── login/         DadosLogin
│   │   ├── materia/       DadosCadastroMateria, DadosAtualizacaoMateria
│   │   ├── onboarding/    DadosMensagemChat
│   │   ├── questao/       DadosCadastroQuestao, DadosAtualizacaoQuestao
│   │   ├── resultado/     DadosCadastroResultado, DadosCadastroResultadoSessao
│   │   ├── tarefa/        DadosCadastroTarefa, DadosAtualizacaoTarefa
│   │   ├── topico/        DadosCadastroTopico, DadosAtualizacaoTopico
│   │   └── usuario/       DadosCadastroUsuario, DadosAtualizacaoUsuario
│   └── output/
│       ├── aulaoutput/           DadosAulaOutput
│       ├── erros/                DadosErro
│       ├── jwt/                  DadosTokenJwt
│       ├── materia/              DadosDetalhamentoMateria, DadosListagemMateria
│       ├── onboarding/           DadosRespostaChat, DadosStatusOnboarding
│       ├── performance/          DadosDesempenhoTopico, DadosDesempenhoUsuario
│       ├── plano/                DadosPlanoEstudo
│       ├── questao/              DadosDetalhamentoQuestao, DadosListagemQuestao
│       ├── questaogerada/        DadosQuestaoGerada
│       ├── questoesoutput/       DadosQuestoesOutput
│       ├── recomendacao/         DadosRecomendacao
│       ├── resultado/            DadosDetalhamentoResultado, DadosListagemResultados, DadosResultadoSessaoOutput
│       ├── tarefa/               DadosDetalhamentoTarefa, DadosListagemTarefa
│       ├── tarefadescricaooutput/ DadosTarefaDescricao
│       ├── topico/               DadosDetalhamentoTopico, DadosListagemTopico
│       └── usuario/              DadosDetalhamentoUsuario, DadosListagemUsuario
│
├── exception/
│   ├── ErroIntegracaoIAException.java
│   ├── GlobalExceptionHandler.java
│   ├── RecursoNaoEncontradoException.java
│   └── RegrasDeNegocioException.java
│
├── infra/
│   ├── ia/
│   │   ├── AIClient.java              (interface)
│   │   └── AnthropicClient.java       (implementation)
│   └── security/
│       ├── AuthFilter.java
│       ├── JwtService.java
│       ├── SecurityConfig.java
│       ├── SecurityUtils.java
│       └── UserDetailsServiceImpl.java
│
└── service/
    ├── AulaService.java
    ├── EmailService.java
    ├── MateriaService.java
    ├── OnboardingService.java
    ├── PerformanceAnalyzerService.java
    ├── PlanoEstudoService.java
    ├── QuestaoService.java
    ├── RecommendationService.java
    ├── ResultadoService.java
    ├── ResultadoSessaoService.java
    ├── TarefaDescricaoService.java
    ├── TarefaService.java
    ├── TokenVerificacaoService.java
    ├── TopicoService.java
    ├── UsuarioService.java
    └── parser/
        └── PlanoEstudoParser.java
```

---

## 2. Entidades

### `Usuario` — tabela `usuarios`
| Campo | Tipo Java | Coluna SQL | Restrições |
|---|---|---|---|
| `id` | `Long` | `id` | PK, BIGSERIAL |
| `nome` | `String` | `nome` | NOT NULL, length 100 |
| `email` | `String` | `email` | NOT NULL, UNIQUE |
| `senha` | `String` | `senha` | NOT NULL (hash BCrypt) |
| `role` | `Role` (enum) | `role` | NOT NULL, EnumType.STRING |
| `ativo` | `Boolean` | `ativo` | NOT NULL, default `false` |
| `criadoEm` | `LocalDateTime` | `criado_em` | NOT NULL, updatable=false |
| `onboardingConcluido` | `Boolean` | `onboarding_concluido` | NOT NULL, default `false` |

**Implements:** `UserDetails` (Spring Security)  
**Relacionamentos:** nenhum FK declarado diretamente; é referenciado por todas as demais entidades.

---

### `Materia` — tabela `materias`
| Campo | Tipo Java | Restrições |
|---|---|---|
| `id` | `Long` | PK |
| `usuario` | `Usuario` | `@ManyToOne LAZY`, FK `usuario_id` NOT NULL |
| `nome` | `String` | NOT NULL, length 100 |
| `descricao` | `String` | length 255 |
| `ativa` | `Boolean` | NOT NULL, default `true` |

---

### `Topico` — tabela `topicos`
| Campo | Tipo Java | Restrições |
|---|---|---|
| `id` | `Long` | PK |
| `usuario` | `Usuario` | `@ManyToOne LAZY`, FK `usuario_id` NOT NULL |
| `nome` | `String` | NOT NULL, length 150 |
| `descricao` | `String` | length 500 |
| `materia` | `Materia` | `@ManyToOne LAZY`, FK `materia_id` NOT NULL |
| `nivel` | `NivelDificuldade` (enum) | NOT NULL, EnumType.STRING |
| `ativo` | `Boolean` | NOT NULL, default `true` |

---

### `Questao` — tabela `questoes`
| Campo | Tipo Java | Restrições |
|---|---|---|
| `id` | `Long` | PK |
| `enunciado` | `String` | NOT NULL, length 1000 |
| `tipo` | `TipoQuestao` (enum) | NOT NULL, EnumType.STRING |
| `topico` | `Topico` | `@ManyToOne LAZY`, FK `topico_id` NOT NULL |
| `ativa` | `Boolean` | NOT NULL, default `true` |

---

### `Resultado` — tabela `resultados`
| Campo | Tipo Java | Restrições |
|---|---|---|
| `id` | `Long` | PK |
| `usuario` | `Usuario` | `@ManyToOne LAZY`, FK `usuario_id` NOT NULL |
| `questao` | `Questao` | `@ManyToOne LAZY`, FK `questao_id` NOT NULL |
| `status` | `RespostaStatus` (enum) | NOT NULL, EnumType.STRING |
| `respostaUsuario` | `String` | length 1000 |
| `respondidoEm` | `LocalDateTime` | NOT NULL, updatable=false |

---

### `ResultadoSessao` — tabela `resultado_sessoes`
| Campo | Tipo Java | Restrições |
|---|---|---|
| `id` | `Long` | PK |
| `usuario` | `Usuario` | `@ManyToOne LAZY`, FK `usuario_id` NOT NULL |
| `topicoNome` | `String` | NOT NULL, length 150 |
| `materiaNome` | `String` | NOT NULL, length 100 |
| `totalQuestoes` | `Integer` | NOT NULL |
| `acertos` | `Integer` | NOT NULL |
| `taxaAcerto` | `Double` | NOT NULL (calculado: acertos/total × 100) |
| `respondidoEm` | `LocalDateTime` | NOT NULL, updatable=false |

> Armazena o resultado agregado de uma sessão de prática (sem vínculo FK com Topico/Materia, usa nome textual).

---

### `Tarefa` — tabela `tarefas`
| Campo | Tipo Java | Restrições |
|---|---|---|
| `id` | `Long` | PK |
| `usuario` | `Usuario` | `@ManyToOne LAZY`, FK `usuario_id` NOT NULL |
| `topico` | `Topico` | `@ManyToOne LAZY`, FK `topico_id` nullable |
| `tipo` | `TipoTarefa` (enum) | NOT NULL, EnumType.STRING |
| `descricao` | `String` | NOT NULL, length 255 |
| `meta` | `Integer` | NOT NULL |
| `prazo` | `LocalDate` | nullable |
| `status` | `TarefaStatus` (enum) | NOT NULL, default `PENDENTE` |
| `criadaEm` | `LocalDateTime` | NOT NULL, updatable=false |

---

### `PlanoEstudo` — tabela `plano_estudo`
| Campo | Tipo Java | Restrições |
|---|---|---|
| `id` | `Long` | PK |
| `usuario` | `Usuario` | `@ManyToOne LAZY`, FK `usuario_id` NOT NULL |
| `conteudoJson` | `String` | NOT NULL, TEXT (JSON completo do plano gerado pela IA) |
| `versao` | `Integer` | NOT NULL |
| `ativo` | `Boolean` | NOT NULL |
| `criadoEm` | `LocalDateTime` | NOT NULL, updatable=false |

---

### `ChatMensagem` — tabela `chat_mensagens`
| Campo | Tipo Java | Restrições |
|---|---|---|
| `id` | `Long` | PK |
| `usuario` | `Usuario` | `@ManyToOne LAZY`, FK `usuario_id` NOT NULL |
| `role` | `RoleChat` (enum) | NOT NULL, length 20, EnumType.STRING |
| `conteudo` | `String` | NOT NULL, TEXT |
| `criadoEm` | `LocalDateTime` | NOT NULL, updatable=false |

---

### `TokenVerificacao` — tabela `tokens_verificacao`
| Campo | Tipo Java | Restrições |
|---|---|---|
| `id` | `Long` | PK |
| `token` | `String` | NOT NULL, UNIQUE (UUID gerado no construtor) |
| `usuario` | `Usuario` | `@OneToOne LAZY`, FK `usuario_id` NOT NULL |
| `expiracao` | `LocalDateTime` | NOT NULL (agora + 24h) |
| `utilizado` | `Boolean` | NOT NULL, default `false` |

**Métodos:** `isExpirado()` — verifica se `LocalDateTime.now()` > `expiracao`  
**Métodos:** `marcarComoUtilizado()` — seta `utilizado = true`

---

## 3. Repositórios

### `UsuarioRepository` extends `JpaRepository<Usuario, Long>`
| Método | Descrição |
|---|---|
| `findByEmail(String email)` | Busca usuário por e-mail (usado no login e no `UserDetailsService`) |
| `existsByEmail(String email)` | Verifica se e-mail já está cadastrado |
| `findAllByAtivoTrue(Pageable pageable)` | Lista paginada apenas de usuários ativos |

---

### `MateriaRepository` extends `JpaRepository<Materia, Long>`
| Método | Descrição |
|---|---|
| `findAllByAtivaTrue()` | Lista todas as matérias ativas (global) |
| `findAllByUsuarioIdAndAtivaTrue(Long usuarioId)` | Lista matérias ativas do usuário |
| `findByNomeAndUsuarioId(String nome, Long usuarioId)` | Busca matéria pelo nome dentro do usuário |
| `existsByNomeAndUsuarioId(String nome, Long usuarioId)` | Verifica duplicidade de nome por usuário |

---

### `TopicoRepository` extends `JpaRepository<Topico, Long>`
| Método | Descrição |
|---|---|
| `findAllByAtivoTrue()` | Lista todos os tópicos ativos |
| `findAllByUsuarioIdAndAtivoTrue(Long usuarioId)` | Tópicos ativos do usuário |
| `findAllByMateriaIdAndAtivoTrue(Long materiaId)` | Tópicos ativos de uma matéria |
| `findByNomeAndMateriaIdAndUsuarioId(String nome, Long materiaId, Long usuarioId)` | Busca tópico por nome+matéria+usuário |
| `existsByNomeAndMateriaId(String nome, Long materiaId)` | Verifica duplicidade nome+matéria |
| `findByNomeAndMateriaNome(String nome, String materiaNome)` | Busca por nome do tópico e nome da matéria (usado no `PerformanceAnalyzerService`) |
| `findAllByAtivoTrueWithMateriaAndUsuarioId(Long usuarioId)` | **@Query JPQL** — busca tópicos com JOIN FETCH de matéria para o usuário |

---

### `QuestaoRepository` extends `JpaRepository<Questao, Long>`
| Método | Descrição |
|---|---|
| `findAllByTopicoIdAndAtivaTrue(Long topicoId)` | Questões ativas de um tópico |
| `findByAtivaTrue(Pageable pageable)` | Listagem paginada de questões ativas |

---

### `ResultadoRepository` extends `JpaRepository<Resultado, Long>`
| Método | Descrição |
|---|---|
| `findAllByUsuarioId(Long usuarioId, Pageable pageable)` | Resultados paginados do usuário |
| `findAllByUsuarioIdAndQuestaoTopicoId(Long usuarioId, Long topicoId)` | Resultados do usuário num tópico |
| `countByUsuarioIdAndQuestaoTopicoIdAndStatus(Long usuarioId, Long topicoId, RespostaStatus status)` | Conta respostas por status (alimenta cálculo de taxa de acerto) |

---

### `ResultadoSessaoRepository` extends `JpaRepository<ResultadoSessao, Long>`
| Método | Descrição |
|---|---|
| `findByUsuarioIdOrderByRespondidoEmDesc(Long usuarioId)` | Histórico de sessões mais recentes primeiro |
| `findByUsuarioId(Long usuarioId)` | Todas as sessões do usuário (usado no `PerformanceAnalyzerService`) |

---

### `TarefaRepository` extends `JpaRepository<Tarefa, Long>`
| Método | Descrição |
|---|---|
| `findAllByUsuarioIdAndStatusNot(Long usuarioId, TarefaStatus status)` | Tarefas excluindo um status (ex: sem CANCELADA) |
| `findAllByUsuarioIdAndStatus(Long usuarioId, TarefaStatus status)` | Tarefas com status específico |
| `findAllByUsuarioIdAndTopicoId(Long usuarioId, Long topicoId)` | Tarefas de um tópico |
| `existsByUsuarioIdAndTopicoIdAndStatus(Long usuarioId, Long topicoId, TarefaStatus status)` | Verifica existência de tarefa pendente |
| `findByIdWithTopico(Long id)` | **@Query JPQL** — busca tarefa com LEFT JOIN FETCH de tópico e matéria |

---

### `PlanoEstudoRepository` extends `JpaRepository<PlanoEstudo, Long>`
| Método | Descrição |
|---|---|
| `findByUsuarioIdAndAtivoTrue(Long usuarioId)` | Plano ativo do usuário |
| `findAllByUsuarioIdOrderByVersaoAsc(Long usuarioId)` | Histórico de planos em ordem de versão |
| `existsByUsuarioId(Long usuarioId)` | Verifica se usuário possui algum plano |

---

### `ChatMensagemRepository` extends `JpaRepository<ChatMensagem, Long>`
| Método | Descrição |
|---|---|
| `findAllByUsuarioIdOrderByCriadoEmAsc(Long usuarioId)` | Histórico de chat em ordem cronológica |
| `deleteAllByUsuarioId(Long usuarioId)` | Limpa o histórico após conclusão do onboarding |

---

### `TokenVerificacaoRepository` extends `JpaRepository<TokenVerificacao, Long>`
| Método | Descrição |
|---|---|
| `findByToken(String token)` | Busca token de verificação por valor UUID |

---

## 4. Services

### `UsuarioService`
| Método | Parâmetros | Retorno | Descrição |
|---|---|---|---|
| `cadastrarUsuario` | `DadosCadastroUsuario` | `DadosDetalhamentoUsuario` | Cria usuário (ativo=false), gera e envia token de verificação |
| `listar` | `Pageable` | `Page<DadosListagemUsuario>` | Lista usuários ativos paginados |
| `buscarPorId` | `Long id` | `DadosDetalhamentoUsuario` | Busca usuário por ID |
| `atualizarUsuario` | `Long id, DadosAtualizacaoUsuario` | `DadosDetalhamentoUsuario` | Atualiza nome, e-mail ou senha |
| `desativar` | `Long id` | `void` | Seta `ativo = false` |

---

### `MateriaService`
| Método | Parâmetros | Retorno | Descrição |
|---|---|---|---|
| `cadastrarMateria` | `DadosCadastroMateria, Long usuarioId` | `DadosDetalhamentoMateria` | Cria matéria (valida nome duplicado por usuário) |
| `listarMateria` | `Long usuarioId` | `List<DadosListagemMateria>` | Lista matérias ativas do usuário |
| `buscarPorID` | `Long id` | `DadosDetalhamentoMateria` | Busca matéria por ID |
| `atualizarMateria` | `Long id, DadosAtualizacaoMateria` | `DadosDetalhamentoMateria` | Atualiza nome, descrição ou estado ativa |
| `desativarMateria` | `Long id` | `void` | Seta `ativa = false` |
| `buscarDono` | `Long id` | `Long` | Retorna `usuario.id` do dono (usado para verificar ownership) |

---

### `TopicoService`
| Método | Parâmetros | Retorno | Descrição |
|---|---|---|---|
| `cadastrarTopico` | `DadosCadastroTopico, Long usuarioId` | `DadosDetalhamentoTopico` | Cria tópico (valida duplicidade nome+matéria) |
| `listarTopicos` | `Long materiaId, Long usuarioId` | `List<DadosListagemTopico>` | Lista por matéria ou todos do usuário |
| `buscarPorId` | `Long id` | `DadosDetalhamentoTopico` | Busca tópico por ID |
| `atualizar` | `Long id, DadosAtualizacaoTopico` | `DadosDetalhamentoTopico` | Atualiza nome, descrição, nível ou ativo |
| `desativar` | `Long id` | `void` | Seta `ativo = false` |
| `buscarDono` | `Long id` | `Long` | Retorna `usuario.id` do dono |

---

### `QuestaoService`
| Método | Parâmetros | Retorno | Descrição |
|---|---|---|---|
| `cadastrar` | `DadosCadastroQuestao` | `DadosDetalhamentoQuestao` | Cria questão vinculada ao tópico |
| `listar` | `Pageable` | `Page<DadosListagemQuestao>` | Lista questões ativas paginadas |
| `atualizar` | `Long id, DadosAtualizacaoQuestao` | `DadosDetalhamentoQuestao` | Atualiza enunciado, tipo ou ativa |
| `inativar` | `Long id` | `void` | Seta `ativa = false` |

---

### `ResultadoService`
| Método | Parâmetros | Retorno | Descrição |
|---|---|---|---|
| `cadastrar` | `DadosCadastroResultado` | `DadosDetalhamentoResultado` | Registra resultado de uma resposta |
| `listarPorUsuario` | `Long usuarioId, Pageable` | `Page<DadosListagemResultados>` | Resultados paginados do usuário |
| `detalharResultado` | `Long id` | `DadosDetalhamentoResultado` | Detalha um resultado |

---

### `ResultadoSessaoService`
| Método | Parâmetros | Retorno | Descrição |
|---|---|---|---|
| `salvar` | `DadosCadastroResultadoSessao, Long usuarioId` | `DadosResultadoSessaoOutput` | Salva sessão calculando taxa de acerto |
| `listarPorUsuario` | `Long usuarioId` | `List<DadosResultadoSessaoOutput>` | Histórico de sessões mais recentes primeiro |

---

### `TarefaService`
| Método | Parâmetros | Retorno | Descrição |
|---|---|---|---|
| `cadastrar` | `DadosCadastroTarefa` | `DadosDetalhamentoTarefa` | Cria tarefa (valida tarefa PENDENTE duplicada no tópico) |
| `listarPorUsuario` | `Long usuarioId, TarefaStatus status` | `List<DadosListagemTarefa>` | Lista tarefas com filtro opcional por status |
| `atualizarTarefa` | `Long id, DadosAtualizacaoTarefa` | `DadosDetalhamentoTarefa` | Atualiza (bloqueia CONCLUIDA/CANCELADA) |
| `cancelar` | `Long id` | `void` | Cancela tarefa (bloqueia CONCLUIDA) |
| `buscarDono` | `Long id` | `Long` | Retorna `usuario.id` da tarefa |

---

### `PlanoEstudoService`
| Método | Parâmetros | Retorno | Descrição |
|---|---|---|---|
| `buscarPorUsuario` | `Long usuarioId` | `DadosPlanoEstudo` | Retorna plano ativo do usuário |
| `buscarHistoricoPorUsuario` | `Long usuarioId` | `List<DadosPlanoEstudo>` | Histórico de planos por versão ascendente |
| `desativarPlanoEstudo` | `Long usuarioId` | `void` | Desativa plano atual (usado no ciclo de review) |

---

### `OnboardingService`
| Método | Parâmetros | Retorno | Descrição |
|---|---|---|---|
| `getStatus` | `Long usuarioId` | `DadosStatusOnboarding` | Retorna se onboarding está concluído |
| `enviarMensagem` | `Long usuarioId, String mensagem` | `DadosRespostaChat` | Fluxo inicial (bloqueia se já concluído) |
| `enviarMensagemReview` | `Long usuarioId, String mensagem` | `DadosRespostaChat` | Fluxo de revisão (exige onboarding já concluído) |
| `processarMensagem` *(private)* | `Usuario, String, String systemPrompt` | `DadosRespostaChat` | Salva mensagem, chama IA, detecta conclusão |
| `montarPromptComHistorico` *(private)* | `List<ChatMensagem>, String systemPrompt` | `String` | Monta prompt concatenando histórico |
| `salvarPlanoEFinalizar` *(private)* | `Usuario, String respostaComJson` | `void` | Extrai JSON, salva PlanoEstudo, popula tarefas, marca onboarding concluído |

**System Prompts internos:**
- `SYSTEM_PROMPT_ONBOARDING` — coleta dados do aluno (vestibular, data da prova, matérias, níveis, horas/dia) e gera plano de 6 semanas com 3 tarefas por semana.
- `SYSTEM_PROMPT_REVIEW` — revisão de progresso; gera novo plano (versão incrementada) com 3–5 tarefas por semana, sem repetir tópicos anteriores.

---

### `RecommendationService`
| Método | Parâmetros | Retorno | Descrição |
|---|---|---|---|
| `gerarRecomendacao` | `Long usuarioId` | `DadosRecomendacao` | Analisa desempenho e chama IA |
| `montarPrompt` *(private)* | `DadosDesempenhoUsuario` | `String` | Monta prompt com dados de performance |
| `parseResposta` *(private)* | `String respostaIA, DadosDesempenhoUsuario` | `DadosRecomendacao` | Parseia JSON da IA |

---

### `PerformanceAnalyzerService`
| Método | Parâmetros | Retorno | Descrição |
|---|---|---|---|
| `analisarDesempenho` | `Long usuarioId` | `DadosDesempenhoUsuario` | Agrega sessões por tópico, calcula taxa geral e lista os 5 tópicos mais fracos |

---

### `AulaService`
| Método | Parâmetros | Retorno | Descrição |
|---|---|---|---|
| `gerarConteudoAula` | `Long topicoId` | `DadosAulaOutput` | Gera aula via IA (busca tópico por ID) |
| `gerarQuestoes` | `Long topicoId, int quantidade` | `DadosQuestoesOutput` | Gera questões ENEM via IA por ID do tópico |
| `gerarConteudoPorNome` | `String topicoNome, String materiaNome, String nivel` | `DadosAulaOutput` | Gera aula via IA por nome (sem FK) |
| `gerarQuestoesPorNome` | `String topicoNome, String materiaNome, int quantidade` | `DadosQuestoesOutput` | Gera questões via IA por nome |

---

### `TarefaDescricaoService`
| Método | Parâmetros | Retorno | Descrição |
|---|---|---|---|
| `gerarDescricao` | `Long tarefaId` | `DadosTarefaDescricao` | Gera descrição motivadora da tarefa via IA |

---

### `EmailService`
| Método | Parâmetros | Retorno | Descrição |
|---|---|---|---|
| `enviarEmailVerificacao` | `Usuario, String token` | `void` | Envia e-mail HTML com link de ativação (`@Async`) |

---

### `TokenVerificacaoService`
| Método | Parâmetros | Retorno | Descrição |
|---|---|---|---|
| `gerarEEnviarToken` | `Usuario` | `void` | Cria `TokenVerificacao` (UUID, validade 24h) e dispara e-mail |
| `verificarToken` | `String tokenString` | `void` | Valida token (expirado? já usado?), ativa usuário |

---

### `PlanoEstudoParser` (service/parser)
| Método | Parâmetros | Retorno | Descrição |
|---|---|---|---|
| `parsearEPopular` | `Usuario, String conteudoJson` | `void` | Lê JSON do plano, cria/reutiliza matérias, tópicos e tarefas no banco |

---

## 5. Controllers

### `AuthController` — `/auth`
| Método HTTP | Caminho | Request Body | Response | Descrição |
|---|---|---|---|---|
| `POST` | `/auth/login` | `DadosLogin` | `DadosTokenJwt` (200) | Autentica e retorna JWT |
| `POST` | `/auth/registro` | `DadosCadastroUsuario` | `DadosDetalhamentoUsuario` (201) | Registra novo usuário |
| `GET` | `/auth/verificar?token=` | `@RequestParam token` | `String` (200) | Ativa conta via token de e-mail |

---

### `UsuarioController` — `/usuarios`
| Método HTTP | Caminho | Autorização | Request | Response | Descrição |
|---|---|---|---|---|---|
| `GET` | `/usuarios` | ADMIN | — | `Page<DadosListagemUsuario>` (200) | Lista usuários ativos (paginado, default size=10 sort=nome) |
| `GET` | `/usuarios/{id}` | ADMIN | — | `DadosDetalhamentoUsuario` (200) | Detalha usuário por ID |
| `PUT` | `/usuarios/{id}` | ADMIN | `DadosAtualizacaoUsuario` | `DadosDetalhamentoUsuario` (200) | Atualiza usuário |
| `DELETE` | `/usuarios/{id}` | ADMIN | — | `204 No Content` | Desativa usuário |

---

### `MateriaController` — `/materias`
| Método HTTP | Caminho | Request | Response | Descrição |
|---|---|---|---|---|
| `POST` | `/materias` | `DadosCadastroMateria` | `DadosDetalhamentoMateria` (201) | Cria matéria para usuário autenticado |
| `GET` | `/materias` | — | `List<DadosListagemMateria>` (200) | Lista matérias do usuário |
| `GET` | `/materias/{id}` | — | `DadosDetalhamentoMateria` (200) | Detalha matéria |
| `PUT` | `/materias/{id}` | `DadosAtualizacaoMateria` | `DadosDetalhamentoMateria` (200) | Atualiza (verifica ownership) |
| `DELETE` | `/materias/{id}` | — | `204 No Content` | Desativa (verifica ownership) |

---

### `TopicoController` — `/topicos`
| Método HTTP | Caminho | Request | Response | Descrição |
|---|---|---|---|---|
| `POST` | `/topicos` | `DadosCadastroTopico` | `DadosDetalhamentoTopico` (201) | Cria tópico |
| `GET` | `/topicos?materiaID=` | — | `List<DadosListagemTopico>` (200) | Lista tópicos (opcional: filtrar por matéria) |
| `GET` | `/topicos/{id}` | — | `DadosDetalhamentoTopico` (200) | Detalha tópico |
| `PUT` | `/topicos/{id}` | `DadosAtualizacaoTopico` | `DadosDetalhamentoTopico` (200) | Atualiza (verifica ownership) |
| `DELETE` | `/topicos/{id}` | — | `204 No Content` | Desativa (verifica ownership) |

---

### `QuestaoController` — `/questoes`
| Método HTTP | Caminho | Autorização | Request | Response | Descrição |
|---|---|---|---|---|---|
| `POST` | `/questoes` | ADMIN | `DadosCadastroQuestao` | `DadosDetalhamentoQuestao` (201) | Cria questão |
| `GET` | `/questoes` | Autenticado | — | `Page<DadosListagemQuestao>` (200) | Lista questões ativas paginadas (default size=10) |
| `PUT` | `/questoes/{id}` | ADMIN | `DadosAtualizacaoQuestao` | `DadosDetalhamentoQuestao` (200) | Atualiza questão |
| `DELETE` | `/questoes/{id}` | ADMIN | — | `204 No Content` | Inativa questão |

---

### `ResultadoController` — `/resultados`
| Método HTTP | Caminho | Request | Response | Descrição |
|---|---|---|---|---|
| `POST` | `/resultados` | `DadosCadastroResultado` | `DadosDetalhamentoResultado` (201) | Registra resultado de resposta |
| `GET` | `/resultados/usuario/{usuarioId}` | — | `Page<DadosListagemResultados>` (200) | Lista resultados paginados (ownership check) |
| `GET` | `/resultados/{id}` | — | `DadosDetalhamentoResultado` (200) | Detalha resultado (ownership check) |

---

### `ResultadoSessaoController` — `/resultado-sessao`
| Método HTTP | Caminho | Request | Response | Descrição |
|---|---|---|---|---|
| `POST` | `/resultado-sessao` | `DadosCadastroResultadoSessao` | `DadosResultadoSessaoOutput` (201) | Salva resultado de sessão |
| `GET` | `/resultado-sessao/usuario/{usuarioId}` | — | `List<DadosResultadoSessaoOutput>` (200) | Lista sessões (ownership check) |

---

### `TarefaController` — `/tarefas`
| Método HTTP | Caminho | Request | Response | Descrição |
|---|---|---|---|---|
| `POST` | `/tarefas` | `DadosCadastroTarefa` | `DadosDetalhamentoTarefa` (201) | Cria tarefa |
| `GET` | `/tarefas/usuario/{usuarioId}?status=` | — | `List<DadosListagemTarefa>` (200) | Lista tarefas (ownership check, status opcional) |
| `PUT` | `/tarefas/{id}` | `DadosAtualizacaoTarefa` | `DadosDetalhamentoTarefa` (200) | Atualiza (ownership check) |
| `DELETE` | `/tarefas/{id}` | — | `204 No Content` | Cancela tarefa (ownership check) |

---

### `OnboardingController` — `/onboarding`
| Método HTTP | Caminho | Request | Response | Descrição |
|---|---|---|---|---|
| `GET` | `/onboarding/status/{usuarioId}` | — | `DadosStatusOnboarding` (200) | Status do onboarding (ownership check) |
| `POST` | `/onboarding/mensagem/{usuarioId}` | `DadosMensagemChat` | `DadosRespostaChat` (200) | Chat onboarding inicial (ownership check) |
| `POST` | `/onboarding/review/{usuarioId}` | `DadosMensagemChat` | `DadosRespostaChat` (200) | Chat de revisão do plano (ownership check) |

---

### `PlanoEstudoController` — `/plano-estudo/usuario/{usuarioId}`
| Método HTTP | Caminho | Response | Descrição |
|---|---|---|---|
| `GET` | `/plano-estudo/usuario/{usuarioId}` | `DadosPlanoEstudo` (200) | Plano ativo (ownership check) |
| `GET` | `/plano-estudo/usuario/{usuarioId}/historico` | `List<DadosPlanoEstudo>` (200) | Histórico de versões (ownership check) |

---

### `AulaController` — `/aula`
| Método HTTP | Caminho | Params | Response | Descrição |
|---|---|---|---|---|
| `GET` | `/aula/topico/{topicoId}/conteudo` | — | `DadosAulaOutput` (200) | Conteúdo de aula por ID do tópico |
| `GET` | `/aula/topico/{topicoId}/questoes?quantidade=` | `quantidade` (default 5) | `DadosQuestoesOutput` (200) | Questões por ID do tópico |
| `GET` | `/aula/topico/por-nome/conteudo?topicoNome=&materiaNome=&nivel=` | Query params | `DadosAulaOutput` (200) | Conteúdo de aula por nome |
| `GET` | `/aula/topico/por-nome/questoes?topicoNome=&materiaNome=&quantidade=` | Query params | `DadosQuestoesOutput` (200) | Questões por nome |
| `GET` | `/aula/tarefa/{tarefaId}/descricao` | — | `DadosTarefaDescricao` (200) | Descrição motivadora de tarefa |

---

### `DiagnosticoController` — `/diagnostico`
| Método HTTP | Caminho | Response | Descrição |
|---|---|---|---|
| `GET` | `/diagnostico/usuario/{usuarioId}` | `List<DadosDesempenhoTopico>` (200) | Desempenho por tópico (ownership check) |

---

### `RecomendacaoController` — `/recomendacao`
| Método HTTP | Caminho | Response | Descrição |
|---|---|---|---|
| `GET` | `/recomendacao/usuario/{usuarioId}` | `DadosRecomendacao` (200) | Recomendações personalizadas via IA (ownership check) |

---

### `DashboardController` — `/dashboard`
| Método HTTP | Caminho | Response | Descrição |
|---|---|---|---|
| `GET` | `/dashboard/usuario/{usuarioId}` | `DadosDesempenhoUsuario` (200) | Dados consolidados de desempenho (ownership check) |

---

## 6. DTOs

### DTOs de Entrada (Input)

| Classe | Campos | Validações |
|---|---|---|
| `DadosLogin` | `email: String`, `senha: String` | `@NotBlank` em ambos |
| `DadosCadastroUsuario` | `nome: String`, `email: String`, `senha: String` | `@NotBlank`, `@Email`, `@Size(min=8)` |
| `DadosAtualizacaoUsuario` | `nome: String`, `email: String`, `senha: String` | `@Email`, `@Size(min=8)` |
| `DadosCadastroMateria` | `nome: String`, `descricao: String` | `@NotBlank(nome)` |
| `DadosAtualizacaoMateria` | `nome: String`, `descricao: String`, `ativa: Boolean` | `@NotBlank(nome)` |
| `DadosCadastroTopico` | `nome: String`, `descricao: String`, `materiaId: Long`, `nivelDificuldade: NivelDificuldade` | `@NotBlank(nome)`, `@NotNull(materiaId, nivelDificuldade)` |
| `DadosAtualizacaoTopico` | `nome: String`, `descricao: String`, `nivelDificuldade: NivelDificuldade`, `ativo: Boolean` | nenhuma obrigatória |
| `DadosCadastroQuestao` | `enunciado: String`, `tipo: TipoQuestao`, `topicoId: Long` | `@NotBlank(enunciado)`, `@NotNull(tipo, topicoId)` |
| `DadosAtualizacaoQuestao` | `enunciado: String`, `tipo: TipoQuestao`, `ativa: Boolean` | nenhuma obrigatória |
| `DadosCadastroResultado` | `usuarioId: Long`, `questaoId: Long`, `status: RespostaStatus`, `respostaUsuario: String` | `@NotNull(usuarioId, questaoId, status)` |
| `DadosCadastroResultadoSessao` | `topicoNome: String`, `materiaNome: String`, `totalQuestoes: Integer`, `acertos: Integer` | `@NotBlank(nomes)`, `@NotNull(números)` |
| `DadosCadastroTarefa` | `usuarioId: Long`, `topicoId: Long`, `tipo: TipoTarefa`, `descricao: String`, `meta: Integer`, `prazo: LocalDate` | `@NotNull(usuarioId, tipo, meta)`, `@NotBlank(descricao)`, `@Positive(meta)` |
| `DadosAtualizacaoTarefa` | `descricao: String`, `meta: Integer`, `prazo: LocalDate`, `status: TarefaStatus` | `@Positive(meta)` |
| `DadosMensagemChat` | `mensagem: String` | `@NotNull` |

---

### DTOs de Saída (Output)

| Classe | Campos |
|---|---|
| `DadosTokenJwt` | `token: String` |
| `DadosErro` | `status: int`, `mensagem: String` |
| `DadosDetalhamentoUsuario` | `id, nome, email, role, ativo, criadoEm` |
| `DadosListagemUsuario` | `id, nome, email, role, ativo` |
| `DadosDetalhamentoMateria` | `id, nome, descricao, ativa` |
| `DadosListagemMateria` | `id, nome, ativa` |
| `DadosDetalhamentoTopico` | `id, nome, descricao, nivel, materiaId, materiaNome, ativo` |
| `DadosListagemTopico` | `id, nome, nivel, materia (nome), ativo` |
| `DadosDetalhamentoQuestao` | `id, enunciado, tipo, topicoId, topicoNome, ativa` |
| `DadosListagemQuestao` | `id, enunciado, tipo, topicoNome, ativa` |
| `DadosDetalhamentoResultado` | `id, usuarioId, questaoId, questaoEnunciado, status, respostaUsuario, respondidoEm` |
| `DadosListagemResultados` | `id, questaoId, status, respondidoEm` |
| `DadosResultadoSessaoOutput` | `id, topicoNome, materiaNome, totalQuestoes, acertos, taxaAcerto, respondidoEm` |
| `DadosDetalhamentoTarefa` | `id, usuarioId, usuarioNome, topicoId, topicoNome, tipo, descricao, meta, prazo, status, criadaEm` |
| `DadosListagemTarefa` | `id, usuarioId, usuarioNome, topicoId, topicoNome, tipo, descricao, meta, prazo, status` |
| `DadosPlanoEstudo` | `id, usuarioId, conteudoJson, versao, criadoEm` |
| `DadosRespostaChat` | `resposta: String`, `onboardingConcluido: boolean` |
| `DadosStatusOnboarding` | `usuarioId: Long`, `onboardingConcluido: boolean` |
| `DadosDesempenhoTopico` | `topicoId, topicoNome, materiaNome, totalRespostas, totalAcertos, taxaAcerto` |
| `DadosDesempenhoUsuario` | `usuarioId, totalRespostas, totalAcertos, taxaAcertoGeral, desempenhoPorTopico (List), topicosMaisFracos (List)` |
| `DadosRecomendacao` | `usuarioId, diagnostico, topicosPrioritarios (List), dicasPraticas (List), mensagemMotivacional, taxaAcertoGeral` |
| `DadosAulaOutput` | `titulo, materia, nivelDificuldade, conteudo, recomendacoes (List<String>)` |
| `DadosQuestoesOutput` | `topico, total, questoes (List<DadosQuestaoGerada>)` |
| `DadosQuestaoGerada` | `numero, enunciado, alternativas (List<String>), alternativaCorreta (int índice 0-based), explicacao` |
| `DadosTarefaDescricao` | `titulo, descricaoDetalhada, passos (List<String>)` |

---

## 7. Segurança

### Configuração (`SecurityConfig`)

- **Sessão:** `STATELESS` — sem criação de sessão HTTP.
- **CSRF:** desabilitado.
- **CORS:** configurável via `cors.allowed-origins` (env var); padrão `localhost:5500`, `127.0.0.1:5500`, `localhost:3000`. Métodos permitidos: GET, POST, PUT, DELETE, OPTIONS. Credentials: true.
- **Criptografia de senha:** `BCryptPasswordEncoder`.

### Rotas Públicas (sem autenticação)
| Rota | Motivo |
|---|---|
| `POST /auth/login` | Login |
| `POST /auth/registro` | Registro |
| `GET /auth/verificar` | Verificação de e-mail |
| `/v3/api-docs/**` | Swagger docs |
| `/swagger-ui/**` | Swagger UI |

### Rotas Exclusivas de ADMIN
| Rota |
|---|
| `GET /usuarios` |
| `GET /usuarios/{id}` |
| `PUT /usuarios/{id}` |
| `DELETE /usuarios/{id}` |
| `POST /questoes` |
| `PUT /questoes/{id}` |
| `DELETE /questoes/{id}` |

### Demais rotas
Qualquer usuário autenticado (`.anyRequest().authenticated()`), com controle de **ownership** aplicado no nível da lógica de negócio (veja `SecurityUtils`).

---

### Fluxo JWT

```
1. Cliente POST /auth/login  →  AuthController
2. AuthController autentica via AuthenticationManager (Spring Security)
3. JwtService.gerarToken(usuario)
   - Algoritmo: HMAC256 com secret lido de ${api.security.token.secret}
   - Issuer: "studymind-api"
   - Subject: email do usuário
   - Claims: role (ex: "ALUNO"), id (Long)
   - Expiração: 2 horas
4. Token JWT retornado ao cliente como {"token": "..."}

5. Requisições subsequentes:
   Header: Authorization: Bearer <token>
6. AuthFilter (OncePerRequestFilter)
   - Extrai o token do header
   - JwtService.validarToken(token) → retorna email ou null
   - UserDetailsServiceImpl.loadUserByUsername(email) → carrega Usuario do banco
   - Popula SecurityContextHolder com UsernamePasswordAuthenticationToken

7. SecurityUtils.getUsuarioAuthenticado(authentication) → retorna Usuario autenticado
8. SecurityUtils.verificarOwnership(ownerId, authentication)
   - Admin: acesso irrestrito
   - Usuário comum: só acessa próprios dados; lança HTTP 403 se IDs divergem
```

### Verificação de E-mail
```
1. POST /auth/registro → cria usuário com ativo=false
2. TokenVerificacaoService.gerarEEnviarToken()
   - Cria TokenVerificacao (UUID, expira em 24h, utilizado=false)
   - EmailService.enviarEmailVerificacao(@Async) → e-mail HTML via JavaMail/Gmail SMTP
3. Usuário clica no link: GET /auth/verificar?token=<uuid>
4. TokenVerificacaoService.verificarToken()
   - Valida: existe, não expirado, não utilizado
   - Seta usuario.ativo=true e token.utilizado=true
```

---

## 8. Camada de IA

### `AIClient` (interface)
```java
public interface AIClient {
    String gerarResposta(String prompt);
}
```
Interface simples para desacoplar a implementação da IA do restante da aplicação.

---

### `AnthropicClient` (implementação)

- **Modelo:** `claude-haiku-4-5-20251001`
- **Endpoint:** `POST https://api.anthropic.com/v1/messages`
- **Headers:** `x-api-key`, `anthropic-version: 2023-06-01`, `content-type: application/json`
- **max_tokens:** 4096
- **HTTP Client:** `RestClient` com `JdkClientHttpRequestFactory`, timeout de conexão de 10s
- **API Key:** lida de `${anthropic.api.key}` (variável de ambiente)
- **Inicialização:** `@PostConstruct init()` — cria o `RestClient`

**Estrutura da requisição:**
```json
{
  "model": "claude-haiku-4-5-20251001",
  "max_tokens": 4096,
  "messages": [{ "role": "user", "content": "<prompt montado pela aplicação>" }]
}
```

**Retorno:** extrai `content[0].text` da resposta da API.

---

### `RecommendationService` — Prompt de Recomendação

Contexto enviado à IA:
```
- Total de questões respondidas pelo aluno
- Taxa de acerto geral (%)
- Top 5 tópicos com menor taxa de acerto (nome, matéria, %)
```

Resposta esperada (JSON):
```json
{
  "diagnostico": "...",
  "topicosPrioritarios": ["topico1", "topico2", "topico3"],
  "dicasPraticas": ["dica1", "dica2"],
  "mensagemMotivacional": "..."
}
```

---

### `AulaService` — Prompts de Aula e Questões

**Prompt de Aula:**  
Recebe: nome do tópico, matéria e nível.  
Resposta esperada (JSON):
```json
{
  "titulo": "...",
  "materia": "...",
  "nivelDificuldade": "...",
  "conteudo": "...mínimo 300 palavras...",
  "recomendacoes": ["rec1", "rec2", "rec3"]
}
```

**Prompt de Questões:**  
Recebe: nome do tópico, matéria, nível e quantidade.  
Resposta esperada (JSON):
```json
{
  "topico": "...",
  "total": 5,
  "questoes": [{
    "numero": 1,
    "enunciado": "...",
    "alternativas": ["A) ...", "B) ...", "C) ...", "D) ...", "E) ..."],
    "alternativaCorreta": 0,
    "explicacao": "..."
  }]
}
```
Regra obrigatória: questões devem ser autocontidas (proibido referenciar gráficos, figuras ou tabelas externas).

---

### `TarefaDescricaoService` — Prompt de Descrição de Tarefa

Recebe: descrição, tipo, meta, nome do tópico e matéria.  
Resposta esperada (JSON):
```json
{
  "titulo": "...",
  "descricaoDetalhada": "...2-3 frases...",
  "passos": ["passo1", "passo2", "passo3"]
}
```
Máximo 4 passos.

---

### `OnboardingService` — Prompts de Onboarding

O sistema instrui o modelo a conduzir uma conversa natural fazendo UMA pergunta por vez e, ao final, emitir o token `ONBOARDING_COMPLETO` seguido de um JSON estruturado com o plano de estudos.

**JSON do plano:**
```json
{
  "vestibular": "FUVEST",
  "dataExame": "2026-11-15",
  "horasPorDia": 3,
  "versao": 1,
  "materias": [{
    "nome": "Matemática",
    "descricao": "...",
    "topicos": [{"nome": "Funções", "nivel": "MEDIO", "descricao": "..."}]
  }],
  "semanas": [{
    "numero": 1,
    "tarefas": [{"topicoNome": "...", "materiaNome": "...", "tipo": "REVISAO", "descricao": "...", "meta": 10}]
  }]
}
```

- Onboarding inicial: 6 semanas, exatamente 3 tarefas por semana, máximo 5 matérias.
- Onboarding review: 6 semanas, 3–5 tarefas por semana, versão incrementada, sem repetir tópicos.

**Tratamento da resposta:**
1. Detecta `ONBOARDING_COMPLETO` na resposta.
2. Extrai o JSON entre `{` e `}`.
3. Valida com `ObjectMapper.readTree()`.
4. Desativa plano anterior (se existir).
5. Limpa histórico de chat (`deleteAllByUsuarioId`).
6. Salva novo `PlanoEstudo`.
7. Chama `PlanoEstudoParser.parsearEPopular()` — cria matérias, tópicos e tarefas no banco.
8. Marca `usuario.onboardingConcluido = true`.

---

### Tratamento de Erros de IA
A exception `ErroIntegracaoIAException` é lançada em qualquer falha de parsing JSON ou chamada HTTP. O `GlobalExceptionHandler` mapeia para HTTP 502 Bad Gateway.

---

## 9. Migrações Flyway

| Versão | Arquivo | Descrição |
|---|---|---|
| V1 | `V1__create-table-usuarios.sql` | Cria tabela `usuarios` com id, nome, email (UNIQUE), senha, role, ativo, criado_em |
| V2 | `V2__create-table-materias.sql` | Cria tabela `materias` com id, nome, descricao, ativa |
| V3 | `V3__crate-table-topicos.sql` | Cria tabela `topicos` com id, nome, descricao, materia_id (FK), nivel, ativo |
| V4 | `V4__crate-table-questoes.sql` | Cria tabela `questoes` com id, enunciado, tipo, topico_id (FK), ativa |
| V5 | `V5__create-table-resultados.sql` | Cria tabela `resultados` com id, usuario_id (FK), questao_id (FK), status, resposta_usuario, respondido_em |
| V6 | `V6__crate-table-tarefas.sql` | Cria tabela `tarefas` com id, usuario_id (FK), topico_id (FK, nullable), tipo, descricao, meta, prazo, status (default PENDENTE), criada_em |
| V7 | `V7__create-indexes.sql` | Cria índices em: `usuarios.email`, `topicos.materia_id`, `questoes.topico_id`, `resultados.usuario_id`, `resultados.questao_id`, `tarefas.usuario_id`, `tarefas.topico_id`, `tarefas.status` |
| V8 | `V8__add-onboarding-usuarios.sql` | Adiciona coluna `onboarding_concluido BOOLEAN NOT NULL DEFAULT FALSE` em `usuarios` |
| V9 | `V9__crate-table-chat-mensagens.sql` | Cria tabela `chat_mensagens` com id, usuario_id (FK), role, conteudo (TEXT), criado_em; cria índice em usuario_id |
| V10 | `V10__create-table-plano-estudo.sql` | Cria tabela `plano_estudo` com id, usuario_id (FK, UNIQUE), conteudo_json (TEXT), criado_em; cria índice em usuario_id |
| V11 | `V11__alter-table-plano-estudo.sql` | Remove constraint UNIQUE de usuario_id; adiciona colunas `versao INTEGER NOT NULL DEFAULT 1` e `ativo BOOLEAN NOT NULL DEFAULT TRUE`; cria índice composto `(usuario_id, ativo)` |
| V12 | `V12__ad-usuario-id-materias-topicos.sql` | Adiciona `usuario_id` (FK para usuarios) em `materias` e `topicos`; remove unique constraint de `materias.nome`; cria índices em ambas as tabelas |
| V13 | `V13__create-resultado-sessao.sql` | Cria tabela `resultado_sessoes` com id, usuario_id (FK), topico_nome, materia_nome, total_questoes, acertos, taxa_acerto (DECIMAL 5,2), respondido_em |
| V14 | `V14__fix-taxa-acerto-tipo.sql` | Altera tipo de `taxa_acerto` de DECIMAL para `FLOAT8` |
| V15 | `V15__create-table-token-verificacao.sql` | Cria tabela `tokens_verificacao` com id, token (VARCHAR UNIQUE), usuario_id (FK), expiracao, utilizado (default FALSE) |

---

## 10. Dependências (`pom.xml`)

| Dependência | Grupo / Artefato | Versão | Escopo | Descrição |
|---|---|---|---|---|
| Spring Boot Parent | `org.springframework.boot:spring-boot-starter-parent` | 3.5.14 | — | BOM principal |
| spring-boot-starter-web | Spring Boot | gerenciada | compile | REST API, Tomcat embutido |
| spring-boot-starter-data-jpa | Spring Boot | gerenciada | compile | JPA/Hibernate + Spring Data |
| spring-boot-starter-mail | Spring Boot | gerenciada | compile | JavaMail para envio de e-mails |
| spring-boot-starter-validation | Spring Boot | gerenciada | compile | Bean Validation (Jakarta) |
| spring-boot-starter-security | Spring Boot | gerenciada | compile | Spring Security |
| postgresql | `org.postgresql:postgresql` | gerenciada | runtime | Driver JDBC para PostgreSQL |
| flyway-core | `org.flywaydb:flyway-core` | gerenciada | compile | Migrações de banco de dados |
| flyway-database-postgresql | `org.flywaydb:flyway-database-postgresql` | gerenciada | compile | Suporte Flyway a PostgreSQL |
| lombok | `org.projectlombok:lombok` | gerenciada | optional | Geração de código boilerplate |
| spring-boot-starter-test | Spring Boot | gerenciada | test | JUnit 5, Mockito, AssertJ |
| spring-security-test | `org.springframework.security:spring-security-test` | gerenciada | test | Suporte a testes de segurança |
| java-jwt | `com.auth0:java-jwt` | **4.4.0** | compile | Geração e validação de tokens JWT (HMAC256) |
| springdoc-openapi-starter-webmvc-ui | `org.springdoc:springdoc-openapi-starter-webmvc-ui` | **2.8.8** | compile | Swagger UI e OpenAPI 3 |

**Java:** 17  
**Build Plugin:** `spring-boot-maven-plugin` (exclui Lombok do JAR final)