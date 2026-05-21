
  ---
Relatório de Análise — StudyMind (Spring Boot)

  ---
1. Mapa de Pacotes e Classes

com.eduardo.studymind (raiz)

┌──────────────────────┬──────────────────────────────────────────────┐
│        Classe        │               Responsabilidade               │
├──────────────────────┼──────────────────────────────────────────────┤
│ StudymindApplication │ Entry point, anotação @SpringBootApplication │
└──────────────────────┴──────────────────────────────────────────────┘

domain.usuario

┌───────────────────┬───────────────────────────────────────────────────────────────────────────┐
│      Classe       │                             Responsabilidade                              │
├───────────────────┼───────────────────────────────────────────────────────────────────────────┤
│ Usuario           │ Entidade JPA da tabela usuarios, implementa UserDetails (Spring Security) │
├───────────────────┼───────────────────────────────────────────────────────────────────────────┤
│ Role              │ Enum: ADMIN, ALUNO                                                        │
├───────────────────┼───────────────────────────────────────────────────────────────────────────┤
│ UsuarioRepository │ Repositório JPA: findByEmail, existsByEmail, findAllByAtivoTrue           │
└───────────────────┴───────────────────────────────────────────────────────────────────────────┘

domain.materia

┌───────────────────┬────────────────────────────────────────────────────────────────────────┐
│      Classe       │                            Responsabilidade                            │
├───────────────────┼────────────────────────────────────────────────────────────────────────┤
│ Materia           │ Entidade JPA da tabela materias, com FK para Usuario                   │
├───────────────────┼────────────────────────────────────────────────────────────────────────┤
│ MateriaRepository │ Repositório: busca por usuário, por nome+usuário, verifica duplicidade │
└───────────────────┴────────────────────────────────────────────────────────────────────────┘

domain.topico

┌──────────────────┬─────────────────────────────────────────────────────────────────────┐
│      Classe      │                          Responsabilidade                           │
├──────────────────┼─────────────────────────────────────────────────────────────────────┤
│ Topico           │ Entidade JPA da tabela topicos, com FK para Materia e Usuario       │
├──────────────────┼─────────────────────────────────────────────────────────────────────┤
│ NivelDificuldade │ Enum: FACIL, MEDIO, DIFICIL                                         │
├──────────────────┼─────────────────────────────────────────────────────────────────────┤
│ TopicoRepository │ Repositório: busca por usuário, por matéria, JOIN FETCH com matéria │
└──────────────────┴─────────────────────────────────────────────────────────────────────┘

domain.questao

┌───────────────────┬────────────────────────────────────────────────────────┐
│      Classe       │                    Responsabilidade                    │
├───────────────────┼────────────────────────────────────────────────────────┤
│ Questao           │ Entidade JPA da tabela questoes, com FK para Topico    │
├───────────────────┼────────────────────────────────────────────────────────┤
│ TipoQuestao       │ Enum: MULTIPLA_ESCOLHA, VERDADEIRO_FALSO, DISSERTATIVA │
├───────────────────┼────────────────────────────────────────────────────────┤
│ QuestaoRepository │ Repositório: busca por tópico, paginação               │
└───────────────────┴────────────────────────────────────────────────────────┘

domain.resultado

┌───────────────────────────┬─────────────────────────────────────────────────────────────────────────┐
│          Classe           │                            Responsabilidade                             │
├───────────────────────────┼─────────────────────────────────────────────────────────────────────────┤
│ Resultado                 │ Entidade JPA da tabela resultados: resposta individual a uma questão    │
├───────────────────────────┼─────────────────────────────────────────────────────────────────────────┤
│ RespostaStatus            │ Enum: CORRETO, INCORRETO, PULADO                                        │
├───────────────────────────┼─────────────────────────────────────────────────────────────────────────┤
│ ResultadoSessao           │ Entidade JPA da tabela resultado_sessoes: sumariza uma sessão de estudo │
├───────────────────────────┼─────────────────────────────────────────────────────────────────────────┤
│ ResultadoRepository       │ Repositório: busca por usuário+tópico, contagem por status              │
├───────────────────────────┼─────────────────────────────────────────────────────────────────────────┤
│ ResultadoSessaoRepository │ Repositório: busca por usuário ordenado por data DESC                   │
└───────────────────────────┴─────────────────────────────────────────────────────────────────────────┘

domain.tarefa

┌──────────────────┬─────────────────────────────────────────────────────────────────────────┐
│      Classe      │                            Responsabilidade                             │
├──────────────────┼─────────────────────────────────────────────────────────────────────────┤
│ Tarefa           │ Entidade JPA da tabela tarefas, com FK para Usuario e Topico (nullable) │
├──────────────────┼─────────────────────────────────────────────────────────────────────────┤
│ TipoTarefa       │ Enum: QUESTOES, REVISAO, META_ACERTO                                    │
├──────────────────┼─────────────────────────────────────────────────────────────────────────┤
│ TarefaStatus     │ Enum: PENDENTE, EM_ANDAMENTO, CONCLUIDA, CANCELADA                      │
├──────────────────┼─────────────────────────────────────────────────────────────────────────┤
│ TarefaRepository │ Repositório: busca com JOIN FETCH, filtros por status                   │
└──────────────────┴─────────────────────────────────────────────────────────────────────────┘

domain.plano

┌───────────────────────┬────────────────────────────────────────────────────────────────────────────┐
│        Classe         │                              Responsabilidade                              │
├───────────────────────┼────────────────────────────────────────────────────────────────────────────┤
│ PlanoEstudo           │ Entidade JPA da tabela plano_estudo: armazena JSON do plano gerado pela IA │
├───────────────────────┼────────────────────────────────────────────────────────────────────────────┤
│ PlanoEstudoRepository │ Repositório: busca plano ativo, histórico por versão                       │
└───────────────────────┴────────────────────────────────────────────────────────────────────────────┘

domain.chat

┌────────────────────────┬─────────────────────────────────────────────────────────────────────────────┐
│         Classe         │                              Responsabilidade                               │
├────────────────────────┼─────────────────────────────────────────────────────────────────────────────┤
│ ChatMensagem           │ Entidade JPA da tabela chat_mensagens: histórico de mensagens com a IA      │
├────────────────────────┼─────────────────────────────────────────────────────────────────────────────┤
│ RoleChat               │ Enum: USER, ASSISTANT                                                       │
├────────────────────────┼─────────────────────────────────────────────────────────────────────────────┤
│ ChatMensagemRepository │ Repositório: busca por usuário ordenado por data ASC, deleteAll por usuário │
└────────────────────────┴─────────────────────────────────────────────────────────────────────────────┘

service

┌────────────────────────────┬─────────────────────────────────────────────────────────────────────┐
│           Classe           │                          Responsabilidade                           │
├────────────────────────────┼─────────────────────────────────────────────────────────────────────┤
│ UsuarioService             │ CRUD de usuários com validações de negócio                          │
├────────────────────────────┼─────────────────────────────────────────────────────────────────────┤
│ MateriaService             │ CRUD de matérias por usuário                                        │
├────────────────────────────┼─────────────────────────────────────────────────────────────────────┤
│ TopicoService              │ CRUD de tópicos por usuário/matéria                                 │
├────────────────────────────┼─────────────────────────────────────────────────────────────────────┤
│ QuestaoService             │ CRUD de questões                                                    │
├────────────────────────────┼─────────────────────────────────────────────────────────────────────┤
│ ResultadoService           │ Registro e consulta de respostas individuais                        │
├────────────────────────────┼─────────────────────────────────────────────────────────────────────┤
│ ResultadoSessaoService     │ Registro de sessões de estudo com cálculo de taxa de acerto         │
├────────────────────────────┼─────────────────────────────────────────────────────────────────────┤
│ TarefaService              │ CRUD de tarefas com validações de status                            │
├────────────────────────────┼─────────────────────────────────────────────────────────────────────┤
│ PlanoEstudoService         │ Consulta e desativação de planos de estudo                          │
├────────────────────────────┼─────────────────────────────────────────────────────────────────────┤
│ OnboardingService          │ Orquestra o chat com a IA para gerar o plano inicial e revisões     │
├────────────────────────────┼─────────────────────────────────────────────────────────────────────┤
│ AulaService                │ Gera conteúdo de aula e questões via IA (por ID ou por nome)        │
├────────────────────────────┼─────────────────────────────────────────────────────────────────────┤
│ TarefaDescricaoService     │ Gera descrição detalhada de tarefa via IA                           │
├────────────────────────────┼─────────────────────────────────────────────────────────────────────┤
│ PerformanceAnalyzerService │ Agrega dados de ResultadoSessao para calcular desempenho por tópico │
├────────────────────────────┼─────────────────────────────────────────────────────────────────────┤
│ RecommendationService      │ Gera recomendações de estudo via IA com base no desempenho          │
└────────────────────────────┴─────────────────────────────────────────────────────────────────────┘

service.parser

┌───────────────────┬─────────────────────────────────────────────────────────────────────────────────────────┐
│      Classe       │                                    Responsabilidade                                     │
├───────────────────┼─────────────────────────────────────────────────────────────────────────────────────────┤
│ PlanoEstudoParser │ Faz parse do JSON do plano e cria/reutiliza entidades Materia, Topico e Tarefa no banco │
└───────────────────┴─────────────────────────────────────────────────────────────────────────────────────────┘

controller

┌───────────────────────────┬──────────────────────────────────────────────┐
│          Classe           │               Responsabilidade               │
├───────────────────────────┼──────────────────────────────────────────────┤
│ AuthController            │ Login e registro de usuário (rotas públicas) │
├───────────────────────────┼──────────────────────────────────────────────┤
│ UsuarioController         │ CRUD de usuários                             │
├───────────────────────────┼──────────────────────────────────────────────┤
│ MateriaController         │ CRUD de matérias do usuário autenticado      │
├───────────────────────────┼──────────────────────────────────────────────┤
│ TopicoController          │ CRUD de tópicos do usuário autenticado       │
├───────────────────────────┼──────────────────────────────────────────────┤
│ QuestaoController         │ CRUD de questões                             │
├───────────────────────────┼──────────────────────────────────────────────┤
│ ResultadoController       │ Registro e consulta de respostas individuais │
├───────────────────────────┼──────────────────────────────────────────────┤
│ ResultadoSessaoController │ Registro e listagem de sessões de estudo     │
├───────────────────────────┼──────────────────────────────────────────────┤
│ TarefaController          │ CRUD de tarefas                              │
├───────────────────────────┼──────────────────────────────────────────────┤
│ AulaController            │ Geração de conteúdo e questões via IA        │
├───────────────────────────┼──────────────────────────────────────────────┤
│ PlanoEstudoController     │ Consulta de plano de estudo ativo            │
├───────────────────────────┼──────────────────────────────────────────────┤
│ OnboardingController      │ Chat de onboarding e review do plano         │
├───────────────────────────┼──────────────────────────────────────────────┤
│ DashboardController       │ Retorna dados agregados de desempenho        │
├───────────────────────────┼──────────────────────────────────────────────┤
│ DiagnosticoController     │ Retorna breakdown por tópico                 │
├───────────────────────────┼──────────────────────────────────────────────┤
│ RecomendacaoController    │ Retorna recomendação gerada pela IA          │
└───────────────────────────┴──────────────────────────────────────────────┘

infra.security

┌────────────────────────┬────────────────────────────────────────────────────────────────────┐
│         Classe         │                          Responsabilidade                          │
├────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ SecurityConfig         │ Configura filtros, CORS, JWT, regras de acesso por rota            │
├────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ JwtService             │ Gera e valida tokens JWT com HMAC256                               │
├────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ AuthFilter             │ Intercepta requests e injeta autenticação no SecurityContextHolder │
├────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ UserDetailsServiceImpl │ Carrega Usuario por email para o Spring Security                   │
├────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ SecurityUtils          │ Helpers estáticos: extrai usuário autenticado, verifica ownership  │
└────────────────────────┴────────────────────────────────────────────────────────────────────┘

infra.ia

┌─────────────────┬───────────────────────────────────────────────────────────┐
│     Classe      │                     Responsabilidade                      │
├─────────────────┼───────────────────────────────────────────────────────────┤
│ AIClient        │ Interface com método gerarResposta(String prompt): String │
├─────────────────┼───────────────────────────────────────────────────────────┤
│ AnthropicClient │ Implementação via HTTP para a API da Anthropic            │
└─────────────────┴───────────────────────────────────────────────────────────┘

exception

┌───────────────────────────────┬────────────────────────────────────────────────────────────────────┐
│            Classe             │                          Responsabilidade                          │
├───────────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ RecursoNaoEncontradoException │ Lançada quando entidade não existe → HTTP 404                      │
├───────────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ RegrasDeNegocioException      │ Violação de regra de negócio → HTTP 422                            │
├───────────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ ErroIntegracaoIAException     │ Falha ao chamar ou parsear resposta da IA → HTTP 502               │
├───────────────────────────────┼────────────────────────────────────────────────────────────────────┤
│ GlobalExceptionHandler        │ @RestControllerAdvice: mapeia exceções para respostas padronizadas │
└───────────────────────────────┴────────────────────────────────────────────────────────────────────┘

dto/input e dto/output

Aproximadamente 30 records Java distribuídos em subpacotes por domínio (usuario, materia, topico, questao, resultado, tarefa, plano, onboarding, performace [sic], recomendacao, aulaoutput, questaogerada, questoesoutput,
tarefadescricaooutput).

  ---
2. Banco de Dados

Migrations Flyway (V1–V14)

┌───────────┬───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│ Migration │                                                                       O que faz                                                                       │
├───────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ V1        │ Cria tabela usuarios (id, nome, email UNIQUE, senha, role, ativo, criado_em)                                                                          │
├───────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ V2        │ Cria tabela materias (id, nome, descricao, ativa)                                                                                                     │
├───────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ V3        │ Cria tabela topicos (id, nome, descricao, materia_id FK, nivel, ativo)                                                                                │
├───────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ V4        │ Cria tabela questoes (id, enunciado, tipo, topico_id FK, ativa)                                                                                       │
├───────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ V5        │ Cria tabela resultados (id, usuario_id FK, questao_id FK, status, resposta_usuario, respondido_em)                                                    │
├───────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ V6        │ Cria tabela tarefas (id, usuario_id FK, topico_id FK nullable, tipo, descricao, meta, prazo, status DEFAULT 'PENDENTE', criada_em)                    │
├───────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ V7        │ Cria índices em usuarios(email), topicos(materia_id), questoes(topico_id), resultados(usuario_id, questao_id), tarefas(usuario_id, topico_id, status) │
├───────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ V8        │ Adiciona coluna onboarding_concluido BOOLEAN DEFAULT FALSE em usuarios                                                                                │
├───────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ V9        │ Cria tabela chat_mensagens (id, usuario_id FK, role, conteudo TEXT, criado_em) + índice                                                               │
├───────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ V10       │ Cria tabela plano_estudo (id, usuario_id UNIQUE, conteudo_json TEXT, criado_em)                                                                       │
├───────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ V11       │ Remove constraint UNIQUE de plano_estudo.usuario_id; adiciona versao INT e ativo BOOLEAN; cria índice em (usuario_id, ativo)                          │
├───────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ V12       │ Adiciona usuario_id BIGINT NOT NULL DEFAULT 1 em materias e topicos com FK para usuarios; cria índices                                                │
├───────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ V13       │ Cria tabela resultado_sessoes (id, usuario_id FK, topico_nome, materia_nome, total_questoes, acertos, taxa_acerto DECIMAL(5,2), respondido_em)        │
├───────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ V14       │ Altera tipo de resultado_sessoes.taxa_acerto de DECIMAL(5,2) para FLOAT8                                                                              │
└───────────┴───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘

Alinhamento Entidades × Migrations

┌─────────────────┬────────────────────────────────────────────────────────┬─────────────┐
│    Entidade     │                         Campo                          │   Status    │
├─────────────────┼────────────────────────────────────────────────────────┼─────────────┤
│ Usuario         │ onboardingConcluido → coluna onboarding_concluido (V8) │ ✅  Alinhado │
├─────────────────┼────────────────────────────────────────────────────────┼─────────────┤
│ Materia         │ usuario (FK) → coluna usuario_id (V12)                 │ ✅  Alinhado │
├─────────────────┼────────────────────────────────────────────────────────┼─────────────┤
│ Topico          │ usuario (FK) → coluna usuario_id (V12)                 │ ✅  Alinhado │
├─────────────────┼────────────────────────────────────────────────────────┼─────────────┤
│ PlanoEstudo     │ versao, ativo → adicionados em V11                     │ ✅  Alinhado │
├─────────────────┼────────────────────────────────────────────────────────┼─────────────┤
│ ResultadoSessao │ taxaAcerto Double → FLOAT8 (V14)                       │ ✅  Alinhado │
└─────────────────┴────────────────────────────────────────────────────────┴─────────────┘

Divergência identificada: A migration V10 cria plano_estudo.usuario_id como UNIQUE. A V11 remove essa constraint, o que indica que houve uma mudança de requisito (de um plano por usuário para múltiplas versões). A entidade
atual não tem anotação @Column(unique=true) para usuario_id, compatível com V11. Sem divergências críticas entre entidade e schema atual.

  ---
3. Camada de Serviço

UsuarioService

┌─────────────────────────────────────────────────┬────────────────────────────────────────────────────────────────────────────┐
│                     Método                      │                                 O que faz                                  │
├─────────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────────┤
│ cadastrarUsuario(DadosCadastroUsuario)          │ Valida unicidade de email, encripta senha com BCrypt, salva com role=ALUNO │
├─────────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────────┤
│ listar(Pageable)                                │ Retorna usuários ativos paginados                                          │
├─────────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────────┤
│ buscarPorId(Long)                               │ Retorna detalhamento ou lança 404                                          │
├─────────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────────┤
│ atualizarUsuario(Long, DadosAtualizacaoUsuario) │ Atualiza nome/email/senha parcialmente; valida duplicidade de email        │
├─────────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────────┤
│ desativar(Long)                                 │ Soft delete: seta ativo=false                                              │
└─────────────────────────────────────────────────┴────────────────────────────────────────────────────────────────────────────┘

MateriaService

┌─────────────────────────────────────────────────┬──────────────────────────────────────────────────────────────────────────┐
│                     Método                      │                                O que faz                                 │
├─────────────────────────────────────────────────┼──────────────────────────────────────────────────────────────────────────┤
│ cadastrarMateria(DadosCadastroMateria, Long)    │ Valida usuário existe, verifica nome duplicado por usuário, cria matéria │
├─────────────────────────────────────────────────┼──────────────────────────────────────────────────────────────────────────┤
│ listarMateria(Long)                             │ Lista matérias ativas do usuário                                         │
├─────────────────────────────────────────────────┼──────────────────────────────────────────────────────────────────────────┤
│ buscarPorID(Long)                               │ Retorna detalhamento ou lança 404                                        │
├─────────────────────────────────────────────────┼──────────────────────────────────────────────────────────────────────────┤
│ atualizarMateria(Long, DadosAtualizacaoMateria) │ Atualiza nome/descricao/ativa parcialmente                               │
├─────────────────────────────────────────────────┼──────────────────────────────────────────────────────────────────────────┤
│ desativarMateria(Long)                          │ Soft delete: seta ativa=false                                            │
└─────────────────────────────────────────────────┴──────────────────────────────────────────────────────────────────────────┘

TopicoService

┌────────────────────────────────────────────┬────────────────────────────────────────────────────────────────────────────┐
│                   Método                   │                                 O que faz                                  │
├────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────────┤
│ cadastrarTopico(DadosCadastroTopico, Long) │ Valida usuário e matéria, verifica nome duplicado por matéria, cria tópico │
├────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────────┤
│ listarTopicos(Long, Long)                  │ Por materiaId ou por usuárioId, filtrando ativos                           │
├────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────────┤
│ buscarPorId(Long)                          │ Retorna detalhamento ou lança 404                                          │
├────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────────┤
│ atualizar(Long, DadosAtualizacaoTopico)    │ Atualiza campos parcialmente                                               │
├────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────────┤
│ desativar(Long)                            │ Soft delete                                                                │
└────────────────────────────────────────────┴────────────────────────────────────────────────────────────────────────────┘

QuestaoService

┌──────────────────────────────────────────┬────────────────────────────────────┐
│                  Método                  │             O que faz              │
├──────────────────────────────────────────┼────────────────────────────────────┤
│ cadastrar(DadosCadastroQuestao)          │ Valida tópico existe, cria questão │
├──────────────────────────────────────────┼────────────────────────────────────┤
│ listar(Pageable)                         │ Lista questões ativas paginado     │
├──────────────────────────────────────────┼────────────────────────────────────┤
│ atualizar(Long, DadosAtualizacaoQuestao) │ Atualização parcial                │
├──────────────────────────────────────────┼────────────────────────────────────┤
│ inativar(Long)                           │ Soft delete                        │
└──────────────────────────────────────────┴────────────────────────────────────┘

ResultadoService

┌───────────────────────────────────┬─────────────────────────────────────────────────────┐
│              Método               │                      O que faz                      │
├───────────────────────────────────┼─────────────────────────────────────────────────────┤
│ cadastrar(DadosCadastroResultado) │ Valida usuário e questão, salva resposta individual │
├───────────────────────────────────┼─────────────────────────────────────────────────────┤
│ listarPorUsuario(Long, Pageable)  │ Lista respostas do usuário paginado                 │
├───────────────────────────────────┼─────────────────────────────────────────────────────┤
│ detalharResultado(Long)           │ Retorna detalhamento ou lança 404                   │
└───────────────────────────────────┴─────────────────────────────────────────────────────┘

ResultadoSessaoService

┌──────────────────────────────────────┬───────────────────────────────────────────────────────────────────────────┐
│                Método                │                                 O que faz                                 │
├──────────────────────────────────────┼───────────────────────────────────────────────────────────────────────────┤
│ salvar(DadosCadastroResultadoSessao) │ Calcula taxaAcerto = (acertos/total)*100, arredonda 2 casas, salva sessão │
├──────────────────────────────────────┼───────────────────────────────────────────────────────────────────────────┤
│ listarPorUsuario(Long)               │ Lista sessões ordenadas por data DESC                                     │
└──────────────────────────────────────┴───────────────────────────────────────────────────────────────────────────┘

TarefaService

┌───────────────────────────────────────────────┬────────────────────────────────────────────────────────────────────────┐
│                    Método                     │                               O que faz                                │
├───────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────┤
│ cadastrar(DadosCadastroTarefa)                │ Valida usuário/tópico, verifica tarefa PENDENTE duplicada, cria tarefa │
├───────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────┤
│ listarPorUsuario(Long, TarefaStatus)          │ Filtra por status ou exclui CANCELADAS                                 │
├───────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────┤
│ atualizarTarefa(Long, DadosAtualizacaoTarefa) │ Bloqueia atualização se CONCLUIDA/CANCELADA, atualiza parcialmente     │
├───────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────┤
│ cancelar(Long)                                │ Bloqueia se CONCLUIDA, seta CANCELADA                                  │
└───────────────────────────────────────────────┴────────────────────────────────────────────────────────────────────────┘

PlanoEstudoService

┌─────────────────────────────────┬──────────────────────────────────────────────────────────────┐
│             Método              │                          O que faz                           │
├─────────────────────────────────┼──────────────────────────────────────────────────────────────┤
│ buscarPorUsuario(Long)          │ Retorna plano ativo ou lança 404                             │
├─────────────────────────────────┼──────────────────────────────────────────────────────────────┤
│ buscarHistoricoPorUsuario(Long) │ Retorna todos os planos ordenados por versão ASC             │
├─────────────────────────────────┼──────────────────────────────────────────────────────────────┤
│ desativarPlanoEstudo(Long)      │ Seta ativo=false no plano ativo (sem exceção se não existir) │
└─────────────────────────────────┴──────────────────────────────────────────────────────────────┘

OnboardingService

┌──────────────────────────────────────────────────────┬───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                        Método                        │                                                                         O que faz                                                                         │
├──────────────────────────────────────────────────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ getStatus(Long)                                      │ Retorna onboardingConcluido do usuário                                                                                                                    │
├──────────────────────────────────────────────────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ enviarMensagem(Long, String)                         │ Bloqueia se onboarding já concluído; chama processarMensagem com SYSTEM_PROMPT_ONBOARDING                                                                 │
├──────────────────────────────────────────────────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ enviarMensagemReview(Long, String)                   │ Bloqueia se onboarding não concluído; chama processarMensagem com SYSTEM_PROMPT_REVIEW                                                                    │
├──────────────────────────────────────────────────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ processarMensagem(Usuario, String, String) (private) │ Salva mensagem do usuário, busca histórico, monta prompt, chama IA, detecta marcador ONBOARDING_COMPLETO, salva plano se completo                         │
├──────────────────────────────────────────────────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ montarPromptComHistorico(List, String) (private)     │ Formata system prompt + histórico de mensagens em um único string                                                                                         │
├──────────────────────────────────────────────────────┼───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ salvarPlanoEFinalizar(Usuario, String) (private)     │ Faz parse do JSON, desativa plano anterior (se review), calcula próxima versão, salva PlanoEstudo, chama PlanoEstudoParser, seta onboardingConcluido=true │
└──────────────────────────────────────────────────────┴───────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘

AulaService

┌──────────────────────────────────────────────┬────────────────────────────────────────────────────────────────────────┐
│                    Método                    │                               O que faz                                │
├──────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────┤
│ gerarConteudoAula(Long)                      │ Valida tópico no banco, monta prompt, chama IA, parseia JSON retornado │
├──────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────┤
│ gerarQuestoes(Long, int)                     │ Valida tópico, solicita N questões com 5 alternativas cada             │
├──────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────┤
│ gerarConteudoPorNome(String, String, String) │ Mesma lógica mas sem lookup no banco (recebe nomes diretamente)        │
├──────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────┤
│ gerarQuestoesPorNome(String, String, int)    │ Mesma lógica para questões por nome                                    │
├──────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────┤
│ parseAulaJson(String) (private)              │ Limpa markdown, parseia JSON, extrai campos                            │
├──────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────┤
│ parseQuestoesJson(String) (private)          │ Limpa markdown, parseia array de questões                              │
└──────────────────────────────────────────────┴────────────────────────────────────────────────────────────────────────┘

TarefaDescricaoService

┌──────────────────────────────────────┬───────────────────────────────────────────────────────────────────────────────┐
│                Método                │                                   O que faz                                   │
├──────────────────────────────────────┼───────────────────────────────────────────────────────────────────────────────┤
│ gerarDescricao(Long)                 │ Busca tarefa com LEFT JOIN FETCH topico, monta prompt, chama IA, parseia JSON │
├──────────────────────────────────────┼───────────────────────────────────────────────────────────────────────────────┤
│ parseDescricaoJson(String) (private) │ Extrai titulo, descricaoDetalhada, passos[] do JSON                           │
└──────────────────────────────────────┴───────────────────────────────────────────────────────────────────────────────┘

PerformanceAnalyzerService

┌──────────────────────────┬────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│          Método          │                                                     O que faz                                                      │
├──────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ analisarDesempenho(Long) │ Busca todos os ResultadoSessao, agrupa por topicoNome+materiaNome, calcula taxas, identifica 5 tópicos mais fracos │
└──────────────────────────┴────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘

RecommendationService

┌─────────────────────────────────────────────────────────┬────────────────────────────────────────────────────────────────────────────────────────────────┐
│                         Método                          │                                           O que faz                                            │
├─────────────────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────────────┤
│ gerarRecomendacao(Long)                                 │ Obtém análise de desempenho, monta prompt com dados dos tópicos fracos, chama IA, parseia JSON │
├─────────────────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────────────┤
│ montarPrompt(DadosDesempenhoUsuario) (private)          │ Formata texto com estatísticas para enviar à IA                                                │
├─────────────────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────────────┤
│ parseResposta(String, DadosDesempenhoUsuario) (private) │ Limpa markdown, parseia JSON da IA                                                             │
└─────────────────────────────────────────────────────────┴────────────────────────────────────────────────────────────────────────────────────────────────┘

PlanoEstudoParser

┌────────────────────────────────────────────────────────┬──────────────────────────────────────────────────────────────────────────┐
│                         Método                         │                                O que faz                                 │
├────────────────────────────────────────────────────────┼──────────────────────────────────────────────────────────────────────────┤
│ parsearEPopular(Usuario, String)                       │ Valida campos materias[] e semanas[], chama sub-métodos                  │
├────────────────────────────────────────────────────────┼──────────────────────────────────────────────────────────────────────────┤
│ processarMaterias(Usuario, JsonNode) (private)         │ Cria ou reutiliza Materia por nome+usuário; retorna Map<String, Materia> │
├────────────────────────────────────────────────────────┼──────────────────────────────────────────────────────────────────────────┤
│ processarTopicos(Usuario, JsonNode, Materia) (private) │ Cria ou reutiliza Topico por nome+matéria+usuário                        │
├────────────────────────────────────────────────────────┼──────────────────────────────────────────────────────────────────────────┤
│ processarSemanas(Usuario, JsonNode, Map) (private)     │ Sempre cria nova Tarefa para cada entrada nas semanas                    │
└────────────────────────────────────────────────────────┴──────────────────────────────────────────────────────────────────────────┘

Métodos vazios, incompletos ou com TODO: Nenhum método vazio identificado nos services. Existe um TODO comentado na SecurityConfig sobre controle de acesso por papel (ADMIN), mas não é um método incompleto.

  ---
4. Controllers e Endpoints

Rotas Públicas (permitAll)

┌────────┬────────────────┬──────────────────────────────────────────────────┬──────────────────────────────┐
│ Método │      Path      │                     Entrada                      │            Saída             │
├────────┼────────────────┼──────────────────────────────────────────────────┼──────────────────────────────┤
│ POST   │ /auth/login    │ DadosLogin (email, senha) @Valid                 │ 200 DadosTokenJwt            │
├────────┼────────────────┼──────────────────────────────────────────────────┼──────────────────────────────┤
│ POST   │ /auth/registro │ DadosCadastroUsuario (nome, email, senha) @Valid │ 201 DadosDetalhamentoUsuario │
└────────┴────────────────┴──────────────────────────────────────────────────┴──────────────────────────────┘

Rotas Autenticadas (JWT obrigatório)

┌────────┬───────────────────────────────────────┬──────────────────────────────────────┬──────────────────────────────────┬──────────────────────────────────────┐
│ Método │                 Path                  │               Entrada                │              Saída               │              Ownership               │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ GET    │ /usuarios                             │ Pageable                             │ Page<DadosListagemUsuario>       │ Não verificado                       │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ GET    │ /usuarios/{id}                        │ —                                    │ DadosDetalhamentoUsuario         │ Não verificado                       │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ PUT    │ /usuarios/{id}                        │ DadosAtualizacaoUsuario              │ DadosDetalhamentoUsuario         │ Não verificado                       │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ DELETE │ /usuarios/{id}                        │ —                                    │ 204                              │ Não verificado                       │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ POST   │ /materias                             │ DadosCadastroMateria @Valid          │ 201 DadosDetalhamentoMateria     │ Extrai usuário do JWT                │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ GET    │ /materias                             │ —                                    │ List<DadosListagemMateria>       │ Extrai usuário do JWT                │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ GET    │ /materias/{id}                        │ —                                    │ DadosDetalhamentoMateria         │ Não verificado                       │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ PUT    │ /materias/{id}                        │ DadosAtualizacaoMateria              │ DadosDetalhamentoMateria         │ Não verificado                       │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ DELETE │ /materias/{id}                        │ —                                    │ 204                              │ Não verificado                       │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ POST   │ /topicos                              │ DadosCadastroTopico @Valid           │ 201 DadosDetalhamentoTopico      │ Extrai usuário do JWT                │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ GET    │ /topicos                              │ ?materiaID opcional                  │ List<DadosListagemTopico>        │ Extrai usuário do JWT                │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ GET    │ /topicos/{id}                         │ —                                    │ DadosDetalhamentoTopico          │ Não verificado                       │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ PUT    │ /topicos/{id}                         │ DadosAtualizacaoTopico               │ DadosDetalhamentoTopico          │ Não verificado                       │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ DELETE │ /topicos/{id}                         │ —                                    │ 204                              │ Não verificado                       │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ POST   │ /questoes                             │ DadosCadastroQuestao @Valid          │ 201 DadosDetalhamentoQuestao     │ Sem verificação                      │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ GET    │ /questoes                             │ Pageable                             │ Page<DadosListagemQuestao>       │ Sem verificação                      │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ PUT    │ /questoes/{id}                        │ DadosAtualizacaoQuestao              │ DadosDetalhamentoQuestao         │ Sem verificação                      │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ DELETE │ /questoes/{id}                        │ —                                    │ 204                              │ Sem verificação                      │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ POST   │ /resultados                           │ DadosCadastroResultado @Valid        │ 201 DadosDetalhamentoResultado   │ Sem verificação de ownership no POST │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ GET    │ /resultados/usuario/{usuarioId}       │ Pageable                             │ Page<DadosListagemResultados>    │ verificarOwnership ✅                 │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ GET    │ /resultados/{id}                      │ —                                    │ DadosDetalhamentoResultado       │ verificarOwnership no resultado ✅    │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ POST   │ /resultado-sessao                     │ DadosCadastroResultadoSessao @Valid  │ 201 DadosResultadoSessaoOutput   │ Sem verificação                      │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ GET    │ /resultado-sessao/usuario/{usuarioId} │ —                                    │ List<DadosResultadoSessaoOutput> │ Sem verificação                      │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ POST   │ /tarefas                              │ DadosCadastroTarefa @Valid           │ 201 DadosDetalhamentoTarefa      │ Sem verificação no POST              │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ GET    │ /tarefas/usuario/{usuarioId}          │ ?status opcional                     │ List<DadosListagemTarefa>        │ verificarOwnership ✅                 │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ PUT    │ /tarefas/{id}                         │ DadosAtualizacaoTarefa               │ DadosDetalhamentoTarefa          │ Sem verificação                      │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ DELETE │ /tarefas/{id}                         │ —                                    │ 204                              │ Sem verificação                      │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ GET    │ /aula/topico/{topicoId}/conteudo      │ —                                    │ DadosAulaOutput                  │ Sem verificação                      │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ GET    │ /aula/topico/{topicoId}/questoes      │ ?quantidade=5                        │ DadosQuestoesOutput              │ Sem verificação                      │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ GET    │ /aula/tarefa/{tarefaId}/descricao     │ —                                    │ DadosTarefaDescricao             │ Sem verificação                      │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ GET    │ /aula/topico/por-nome/conteudo        │ ?topicoNome&materiaNome&nivel        │ DadosAulaOutput                  │ Sem verificação                      │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ GET    │ /aula/topico/por-nome/questoes        │ ?topicoNome&materiaNome&quantidade=5 │ DadosQuestoesOutput              │ Sem verificação                      │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ GET    │ /plano-estudo/usuario/{usuarioId}     │ —                                    │ DadosPlanoEstudo                 │ verificarOwnership ✅                 │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ GET    │ /onboarding/status/{usuarioId}        │ —                                    │ DadosStatusOnboarding            │ verificarOwnership ✅                 │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ POST   │ /onboarding/mensagem/{usuarioId}      │ DadosMensagemChat @Valid             │ DadosRespostaChat                │ verificarOwnership ✅                 │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ POST   │ /onboarding/review/{usuarioId}        │ DadosMensagemChat @Valid             │ DadosRespostaChat                │ verificarOwnership ✅                 │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ GET    │ /dashboard/usuario/{usuarioId}        │ —                                    │ DadosDesempenhoUsuario           │ verificarOwnership ✅                 │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ GET    │ /diagnostico/usuario/{usuarioId}      │ —                                    │ List<DadosDesempenhoTopico>      │ verificarOwnership ✅                 │
├────────┼───────────────────────────────────────┼──────────────────────────────────────┼──────────────────────────────────┼──────────────────────────────────────┤
│ GET    │ /recomendacao/usuario/{usuarioId}     │ —                                    │ DadosRecomendacao                │ verificarOwnership ✅                 │
└────────┴───────────────────────────────────────┴──────────────────────────────────────┴──────────────────────────────────┴──────────────────────────────────────┘

Endpoints sem validação ou sem tratamento adequado identificados:
- POST /resultados — recebe usuarioId no body sem verificar se o usuário autenticado é o dono; qualquer usuário autenticado pode registrar respostas em nome de outro.
- POST /resultado-sessao — mesma questão: usuarioId no body sem ownership check.
- GET /resultado-sessao/usuario/{usuarioId} — sem verificarOwnership.
- PUT /tarefas/{id} e DELETE /tarefas/{id} — sem verificar se o usuário autenticado é dono da tarefa.
- PUT /materias/{id} e DELETE /materias/{id} — sem verificar se é dono da matéria.
- PUT /topicos/{id} e DELETE /topicos/{id} — sem verificar ownership.
- Endpoints /aula/** — sem nenhum controle de ownership; qualquer usuário autenticado pode gerar conteúdo por topicoId de outro usuário.

  ---
5. Integração com IA (Anthropic)

Configuração do AnthropicClient

// AnthropicClient.java
private static final String API_URL = "https://api.anthropic.com/v1/messages";
private static final String MODEL   = "claude-haiku-4-5-20251001";
// max_tokens: 4096
// anthropic-version: "2023-06-01"
// API Key via @Value("${anthropic.api.key}")
Timeout: Não há timeout configurado explicitamente — usa o default do RestClient.create() sem customização. Isso é um risco: se a Anthropic demorar, a requisição fica presa indefinidamente.

Fluxo completo do Onboarding

1. POST /onboarding/mensagem/{usuarioId}
   └─ OnboardingController.enviarMensagem()
   └─ SecurityUtils.verificarOwnership()
   └─ OnboardingService.enviarMensagem(usuarioId, mensagem)
   └─ Valida: onboardingConcluido == false
   └─ Carrega Usuario do banco
   └─ processarMensagem(usuario, mensagem, SYSTEM_PROMPT_ONBOARDING)

2. processarMensagem():
   a. Salva ChatMensagem{role=USER, conteudo=mensagem}
   b. Busca todo histórico: findAllByUsuarioIdOrderByCriadoEmAsc(usuarioId)
   c. montarPromptComHistorico(historico, systemPrompt)
   → Formato: "[SISTEMA]\n{systemPrompt}\n\n[HISTÓRICO]\nUsuário: ...\nAssistente: ...\nUsuário: {nova}\nAssistente:"
   d. aiClient.gerarResposta(promptCompleto)
   → POST https://api.anthropic.com/v1/messages
   → body: {model, max_tokens:4096, messages:[{role:user, content:prompt}]}
   → extrai response.content[0].text
   e. Detecta marcador: respostaIA.contains("ONBOARDING_COMPLETO")
   → Se NÃO: salva ChatMensagem{role=ASSISTANT}, retorna DadosRespostaChat(resposta, false)
   → Se SIM: chama salvarPlanoEFinalizar(usuario, respostaIA)

3. salvarPlanoEFinalizar():
   a. Extrai JSON da resposta (tudo após "ONBOARDING_COMPLETO\n")
   b. objectMapper.readTree(json) — valida estrutura
   c. Se plano ativo existe → plano.setAtivo(false)
   d. Calcula versao = max(versoes existentes) + 1
   e. Cria PlanoEstudo{conteudoJson, versao, ativo=true}
   f. planoEstudoRepository.save(plano)
   g. planoEstudoParser.parsearEPopular(usuario, json) → cria Materia/Topico/Tarefa
   h. usuario.setOnboardingConcluido(true)
   i. Salva ChatMensagem{role=ASSISTANT, conteudo=resposta limpa}
   j. Retorna DadosRespostaChat("Plano de estudos criado com sucesso!", true)

Formato do JSON em conteudoJson

O SYSTEM_PROMPT_ONBOARDING instrui a IA a gerar:

{
"vestibular": "ENEM",
"dataExame": "2025-11-01",
"horasPorDia": 3,
"versao": 1,
"materias": [
{
"nome": "Matemática",
"descricao": "...",
"topicos": [
{ "nome": "Funções", "nivel": "MEDIO", "descricao": "..." }
]
}
],
"semanas": [
{
"numero": 1,
"tarefas": [
{
"topicoNome": "Funções",
"materiaNome": "Matemática",
"tipo": "REVISAO",
"descricao": "Revisar funções quadráticas",
"meta": 10
}
]
}
]
}

Onde conteudoJson é parseado

Sim, é parseado em dois lugares:

1. PlanoEstudoParser.parsearEPopular() — chamado por OnboardingService.salvarPlanoEFinalizar() imediatamente após salvar o PlanoEstudo. Usa ObjectMapper.readTree() para criar Materia, Topico e Tarefa no banco.
2. OnboardingService.salvarPlanoEFinalizar() — faz objectMapper.readTree(json) antes de salvar, apenas para validar que o JSON é válido (não o usa para criar entidades).

O conteudoJson armazenado no banco não é re-parseado ao ser lido via GET /plano-estudo/usuario/{id} — ele é retornado como String bruta dentro de DadosPlanoEstudo.conteudoJson. O frontend é responsável por interpretar esse
JSON.

  ---
6. Segurança

Rotas protegidas vs públicas

// SecurityConfig.java
.requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
.requestMatchers(HttpMethod.POST, "/auth/registro").permitAll()
.requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
.anyRequest().authenticated()

Todo o restante exige JWT válido. Não há diferenciação por papel (ROLE_ADMIN vs ROLE_ALUNO) — isso está marcado como TODO no código.

Validação do JWT (JwtService)

- Algoritmo: HMAC256 com secret via variável de ambiente ${api.security.token.secret}
- Expiração: 2 horas
- validarToken() retorna null em caso de qualquer JWTVerificationException (token expirado, assinatura inválida, etc.)
- AuthFilter verifica se o token retornado não é null e não está em branco antes de autenticar

A validação está correta para o que foi implementado.

Endpoints que deveriam estar mais protegidos

- GET /usuarios e GET /usuarios/{id} — qualquer usuário autenticado pode listar/ver todos os outros usuários. Deveria ser restrito a ADMIN.
- DELETE /usuarios/{id} — qualquer usuário autenticado pode desativar qualquer conta.
- POST /questoes, PUT /questoes/{id}, DELETE /questoes/{id} — operações de admin acessíveis a qualquer usuário autenticado.
- POST /resultado-sessao e GET /resultado-sessao/usuario/{id} — sem verificarOwnership, qualquer usuário pode ver sessões de outros.

  ---
7. Testes

StudymindApplicationTests

- Tipo: Smoke test (@SpringBootTest)
- Cenário: Verifica se o contexto da aplicação sobe sem erro
- Status: Deve passar se as variáveis de ambiente estiverem configuradas (usa TestSecurityConfig para desabilitar segurança nos testes)

TestSecurityConfig

- Configuração de segurança alternativa para testes: desabilita o AuthFilter e permite todas as requisições

UsuarioServiceTest (5 testes, Mockito puro)

┌────────────────────────────────────────────────────────────┬─────────────────┐
│                          Cenário                           │ Status esperado │
├────────────────────────────────────────────────────────────┼─────────────────┤
│ cadastrarUsuario_sucesso                                   │ ✅               │
├────────────────────────────────────────────────────────────┼─────────────────┤
│ cadastrarUsuario_emailDuplicado → RegrasDeNegocioException │ ✅               │
├────────────────────────────────────────────────────────────┼─────────────────┤
│ buscarPorId_sucesso                                        │ ✅               │
├────────────────────────────────────────────────────────────┼─────────────────┤
│ buscarPorId_naoEncontrado → RecursoNaoEncontradoException  │ ✅               │
├────────────────────────────────────────────────────────────┼─────────────────┤
│ atualizarUsuario_emailDuplicado → RegrasDeNegocioException │ ✅               │
├────────────────────────────────────────────────────────────┼─────────────────┤
│ desativar_sucesso                                          │ ✅               │
└────────────────────────────────────────────────────────────┴─────────────────┘

MateriaServiceTest (4 testes)

┌────────────────────────────────────────────────────────────────┬────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                            Cenário                             │                                                             Observação                                                             │
├────────────────────────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ cadastrarMateria_sucesso                                       │ ✅                                                                                                                                  │
├────────────────────────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ cadastrarMateria_nomeDuplicado → RegrasDeNegocioException      │ ✅                                                                                                                                  │
├────────────────────────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ listarMateria_sucesso                                          │ ✅                                                                                                                                  │
├────────────────────────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ buscarPorId_sucesso — ATENÇÃO: nome do teste é enganoso        │ O teste chama desativarMateria(1L), não buscarPorID(). O @DisplayName diz "buscar por ID" mas o comportamento testado é desativar. │
├────────────────────────────────────────────────────────────────┼────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ desativarMateria_naoEncontrado → RecursoNaoEncontradoException │ ✅                                                                                                                                  │
└────────────────────────────────────────────────────────────────┴────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┘

TopicoServiceTest (5 testes)

┌───────────────────────────────┬─────────────────┐
│            Cenário            │ Status esperado │
├───────────────────────────────┼─────────────────┤
│ cadastrarTopico_sucesso       │ ✅               │
├───────────────────────────────┼─────────────────┤
│ cadastrarTopico_nomeDuplicado │ ✅               │
├───────────────────────────────┼─────────────────┤
│ listarTopicos_sucesso         │ ✅               │
├───────────────────────────────┼─────────────────┤
│ buscarPorId_naoEncontrado     │ ✅               │
├───────────────────────────────┼─────────────────┤
│ desativar_sucesso             │ ✅               │
└───────────────────────────────┴─────────────────┘

questaoServiceTest (4 testes — nome com letra minúscula, violação de convenção)

┌────────────────────────────────────────┬─────────────────┐
│                Cenário                 │ Status esperado │
├────────────────────────────────────────┼─────────────────┤
│ cadastrar_sucesso (com ArgumentCaptor) │ ✅               │
├────────────────────────────────────────┼─────────────────┤
│ cadastrar_topicoNaoEncontrado          │ ✅               │
├────────────────────────────────────────┼─────────────────┤
│ atualizar_naoEncontrado                │ ✅               │
├────────────────────────────────────────┼─────────────────┤
│ inativar_sucesso                       │ ✅               │
├────────────────────────────────────────┼─────────────────┤
│ inativar_naoEncontrado                 │ ✅               │
└────────────────────────────────────────┴─────────────────┘

ResultadoServiceTest (4 testes)

┌────────────────────────────────┬─────────────────┐
│            Cenário             │ Status esperado │
├────────────────────────────────┼─────────────────┤
│ cadastrar_sucesso              │ ✅               │
├────────────────────────────────┼─────────────────┤
│ cadastrar_usuarioNaoEncontrado │ ✅               │
├────────────────────────────────┼─────────────────┤
│ cadastrar_questaoNaoEncontrada │ ✅               │
├────────────────────────────────┼─────────────────┤
│ detalhar_naoEncontrado         │ ✅               │
└────────────────────────────────┴─────────────────┘

TarefaServiceTest (6 testes)

┌────────────────────────────────┬─────────────────┐
│            Cenário             │ Status esperado │
├────────────────────────────────┼─────────────────┤
│ cadastrar_sucesso              │ ✅               │
├────────────────────────────────┼─────────────────┤
│ cadastrar_tarefaDuplicada      │ ✅               │
├────────────────────────────────┼─────────────────┤
│ cadastrar_usuarioNaoEncontrado │ ✅               │
├────────────────────────────────┼─────────────────┤
│ atualizar_tarefaConcluida      │ ✅               │
├────────────────────────────────┼─────────────────┤
│ cancelar_sucesso               │ ✅               │
├────────────────────────────────┼─────────────────┤
│ cancelar_tarefaConcluida       │ ✅               │
└────────────────────────────────┴─────────────────┘

PlanoEstudoServiceTest (5 testes)

┌───────────────────────────────────────┬─────────────────┐
│                Cenário                │ Status esperado │
├───────────────────────────────────────┼─────────────────┤
│ buscarPorUsuario_retornaPlano         │ ✅               │
├───────────────────────────────────────┼─────────────────┤
│ buscarPorUsuario_planoNaoEncontrado   │ ✅               │
├───────────────────────────────────────┼─────────────────┤
│ buscarHistorico_retornaLista          │ ✅               │
├───────────────────────────────────────┼─────────────────┤
│ buscarHistorico_semPlanos             │ ✅               │
├───────────────────────────────────────┼─────────────────┤
│ desativar_planoExistente              │ ✅               │
├───────────────────────────────────────┼─────────────────┤
│ desativar_semPlanoAtivo (sem exceção) │ ✅               │
└───────────────────────────────────────┴─────────────────┘

PlanoEstudoParserTest (5 testes)

┌────────────────────────────────────────────┬─────────────────┐
│                  Cenário                   │ Status esperado │
├────────────────────────────────────────────┼─────────────────┤
│ parsear_criaMateriaNova                    │ ✅               │
├────────────────────────────────────────────┼─────────────────┤
│ parsear_reutilizaMateriaExistente          │ ✅               │
├────────────────────────────────────────────┼─────────────────┤
│ parsear_reutilizaTopicoExistente           │ ✅               │
├────────────────────────────────────────────┼─────────────────┤
│ parsear_jsonSemMaterias → RuntimeException │ ✅               │
├────────────────────────────────────────────┼─────────────────┤
│ parsear_sempreCriaNovasTarefas             │ ✅               │
└────────────────────────────────────────────┴─────────────────┘

OnboardingServiceTest (6 testes)

┌─────────────────────────────────────────────┬─────────────────┐
│                   Cenário                   │ Status esperado │
├─────────────────────────────────────────────┼─────────────────┤
│ getStatus_naoConcluido                      │ ✅               │
├─────────────────────────────────────────────┼─────────────────┤
│ getStatus_usuarioNaoEncontrado              │ ✅               │
├─────────────────────────────────────────────┼─────────────────┤
│ enviarMensagem_respostaSimples              │ ✅               │
├─────────────────────────────────────────────┼─────────────────┤
│ enviarMensagem_onboardingJaConcluido        │ ✅               │
├─────────────────────────────────────────────┼─────────────────┤
│ enviarMensagem_concluidoComSucesso          │ ✅               │
├─────────────────────────────────────────────┼─────────────────┤
│ enviarMensagemReview_onboardingNaoConcluido │ ✅               │
├─────────────────────────────────────────────┼─────────────────┤
│ enviarMensagemReview_desativaPlanoAnterior  │ ✅               │
└─────────────────────────────────────────────┴─────────────────┘

PerformanceAnalyzerServiceTest (5 testes)

┌────────────────────────────────────────────────────────┬─────────────────┐
│                        Cenário                         │ Status esperado │
├────────────────────────────────────────────────────────┼─────────────────┤
│ analisarDesempenho_semSessoes (zeros)                  │ ✅               │
├────────────────────────────────────────────────────────┼─────────────────┤
│ analisarDesempenho_calculaTaxaCorretamente             │ ✅               │
├────────────────────────────────────────────────────────┼─────────────────┤
│ analisarDesempenho_agrupaSessoesDoMesmoTopico          │ ✅               │
├────────────────────────────────────────────────────────┼─────────────────┤
│ analisarDesempenho_identificaTopicosMaisFracos (top 5) │ ✅               │
├────────────────────────────────────────────────────────┼─────────────────┤
│ analisarDesempenho_separaTopicosPorMateria             │ ✅               │
└────────────────────────────────────────────────────────┴─────────────────┘

RecommendationServiceTest (5 testes)

┌───────────────────────────────────────────────────────────────────────┬─────────────────┐
│                                Cenário                                │ Status esperado │
├───────────────────────────────────────────────────────────────────────┼─────────────────┤
│ gerarRecomendacao_sucesso                                             │ ✅               │
├───────────────────────────────────────────────────────────────────────┼─────────────────┤
│ gerarRecomendacao_limpaBlocoMarkdown                                  │ ✅               │
├───────────────────────────────────────────────────────────────────────┼─────────────────┤
│ gerarRecomendacao_jsonInvalido → ErroIntegracaoIAException            │ ✅               │
├───────────────────────────────────────────────────────────────────────┼─────────────────┤
│ gerarRecomendacao_iaIndisponivel                                      │ ✅               │
├───────────────────────────────────────────────────────────────────────┼─────────────────┤
│ gerarRecomendacao_incluiTopicosNoPrompt (verifica conteúdo do prompt) │ ✅               │
└───────────────────────────────────────────────────────────────────────┴─────────────────┘

AuthControllerTest (4 testes — @SpringBootTest + MockMvc)

┌──────────────────────────────────────────────┬─────────────────┐
│                   Cenário                    │ Status esperado │
├──────────────────────────────────────────────┼─────────────────┤
│ login_sucesso → token JWT retornado          │ ✅               │
├──────────────────────────────────────────────┼─────────────────┤
│ login_semEmail → 400                         │ ✅               │
├──────────────────────────────────────────────┼─────────────────┤
│ registro_sucesso → 201                       │ ✅               │
├──────────────────────────────────────────────┼─────────────────┤
│ registro_senhaFraca (menos de 8 chars) → 400 │ ✅               │
└──────────────────────────────────────────────┴─────────────────┘

Partes sem teste

- AulaService — sem nenhum teste unitário
- TarefaDescricaoService — sem nenhum teste
- ResultadoSessaoService — sem nenhum teste
- PerformanceAnalyzerService (campo topicoId em DadosDesempenhoTopico nunca é populado — sempre null — porque o agrupamento é feito por string, não por ID de entidade)
- Todos os controllers exceto AuthController — sem testes de integração/camada web
- SecurityUtils.verificarOwnership() — sem teste dedicado
- JwtService — sem teste unitário para geração/validação de tokens

  ---
8. Problemas e Inconsistências

Segurança

1. POST /resultados — usuarioId vem no body sem verificação de ownership. Um usuário autenticado pode registrar respostas em nome de qualquer outro (DadosCadastroResultado.usuarioId).
2. POST /resultado-sessao — mesmo problema.
3. GET /resultado-sessao/usuario/{id} — sem verificarOwnership, qualquer usuário autenticado vê sessões de qualquer outro.
4. DELETE /usuarios/{id}, PUT /usuarios/{id} — não verifica se o usuário autenticado é ADMIN ou o próprio dono da conta.
5. PUT /tarefas/{id}, DELETE /tarefas/{id} — sem verificação de ownership.
6. Sem controle de papel — TODO existente em SecurityConfig. Qualquer usuário ALUNO pode criar/editar/apagar questões.

Nome de pacote com typo

- dto/output/performace/ — deveria ser performance. Não causa erro de compilação mas é inconsistente.

Teste com nome enganoso

- MateriaServiceTest.buscarPorId_sucesso() — o @DisplayName diz "buscar por ID" mas o corpo do teste chama desativarMateria(1L). Provavelmente copy-paste de outro teste.

Convenção de nome de classe

- questaoServiceTest — começa com letra minúscula, violando a convenção Java para nomes de classe.

Falta de timeout no AnthropicClient

- RestClient.create() sem configuração de timeout. Uma chamada lenta à Anthropic pode bloquear a thread por minutos, potencialmente esgotando o pool de threads do servidor.

Campo topicoId sempre nulo em DadosDesempenhoTopico

- PerformanceAnalyzerService constrói DadosDesempenhoTopico passando null como topicoId:
  new DadosDesempenhoTopico(null, topicoNome, materiaNome, total, acertos, taxa)
- O agrupamento é feito por topicoNome + "|" + materiaNome (string), sem lookup de ID. Isso significa que o frontend recebe topicoId: null e não pode navegar diretamente para o tópico.

Histórico de chat não é limpo no review

- ChatMensagemRepository.deleteAllByUsuarioId() existe mas nunca é chamado no review. O histórico cresce indefinidamente e é enviado inteiro a cada mensagem, podendo atingir o limite de tokens da Anthropic em usuários com
  histórico longo.

DadosAtualizacaoTopico sem materiaId

- Não é possível mover um tópico para outra matéria via PUT /topicos/{id}. O campo materiaId não existe no DTO de atualização. Pode ser intencional, mas é uma limitação não documentada.

PlanoEstudoController falta o endpoint de histórico

- PlanoEstudoService.buscarHistoricoPorUsuario() existe e tem testes, mas não há nenhum endpoint em PlanoEstudoController que o exponha. O método está inacessível pelo frontend.

Exception genérica no GlobalExceptionHandler

- O handler de Exception genérico faz e.printStackTrace() em vez de usar um logger (@Slf4j), o que pode vazar stack traces em ambientes de produção nos logs mas não na resposta HTTP.

  ---

O projeto está em um estado funcional e bem estruturado para um MVP: a arquitetura em camadas está correta, o fluxo de onboarding com IA está implementado e testado, há 14 migrations Flyway sem divergências com as entidades,
e a cobertura de testes unitários cobre os services principais (cerca de 55 testes no total). Os pontos mais críticos pendentes são de segurança: especificamente, os endpoints POST /resultados, POST /resultado-sessao, GET
/resultado-sessao/usuario/{id}, PUT/DELETE /tarefas/{id}, PUT/DELETE /materias/{id} e os endpoints de /usuarios carecem de verificação de ownership ou restrição por papel — qualquer token JWT válido concede acesso excessivo a
dados de outros usuários. O próximo passo lógico é fechar essas brechas de segurança: extrair o usuarioId do JWT em vez de receber no body nos endpoints de resultado/sessão, adicionar verificarOwnership nos PUT/DELETE de
tarefas, matérias e tópicos, e implementar a distinção ADMIN/ALUNO que já está marcada como TODO na SecurityConfig.

