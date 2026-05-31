# Relatório Técnico — StudyMind v3

> Gerado em: 2026-05-31  
> Repositório: `C:\Users\eduar\studymind` (branch `main`)  
> Base: análise estática de todos os arquivos `.java`, `.sql`, `.properties` e `pom.xml`

---

## 1. Visão Geral do Projeto

O **StudyMind** é uma plataforma educacional com IA generativa voltada para vestibulandos brasileiros. O sistema conduz um onboarding conversacional com o aluno, coleta preferências de estudo e gera automaticamente um plano personalizado de 6 semanas usando a API da Anthropic (Claude Haiku).

### Stack tecnológica

| Camada | Tecnologia |
|--------|-----------|
| Linguagem | Java 17 |
| Framework | Spring Boot 3.5.14 |
| Banco de dados | PostgreSQL 17 |
| Migrações | Flyway (V1 a V15) |
| Segurança | Spring Security + JWT (Auth0 `java-jwt` 4.4.0) |
| IA Generativa | Anthropic Claude Haiku (`claude-haiku-4-5-20251001`) |
| Documentação | SpringDoc OpenAPI 2.8.8 (Swagger UI) |
| Email | Spring Mail via SMTP Gmail |
| Build | Maven 3.x |
| Testes | JUnit 5 + Mockito + MockMvc |

### Arquitetura

```
Controller (REST) → Service → Repository (JPA) → PostgreSQL
                ↓
           AIClient (interface)
                ↓
        AnthropicClient (HTTP/RestClient)
```

**Pacotes principais:**
- `com.eduardo.studymind.controller` — 12 controllers REST
- `com.eduardo.studymind.service` — 13 services + 1 parser
- `com.eduardo.studymind.domain` — Entidades JPA e repositórios
- `com.eduardo.studymind.dto` — Records de entrada/saída (separados em `/input` e `/output`)
- `com.eduardo.studymind.infra.security` — JWT, filtro de autenticação, config do Spring Security
- `com.eduardo.studymind.infra.ia` — Interface `AIClient` e implementação `AnthropicClient`
- `com.eduardo.studymind.exception` — Handler global e exceções de domínio

**Entidades do modelo de domínio:**

```
Usuario → Materia → Topico → Questao → Resultado
Usuario → Tarefa (vinculada a Topico)
Usuario → PlanoEstudo (JSON gerado por IA)
Usuario → ChatMensagem (histórico do onboarding)
Usuario → ResultadoSessao (agregado por sessão de estudo)
Usuario → TokenVerificacao (verificação de e-mail)
```

**Migrações Flyway (V1–V15):** criação de tabelas, índices, adição de colunas (`onboarding_concluido`, `usuario_id` em matérias/tópicos), tabelas de sessão, plano de estudo, chat e tokens de verificação.

---

## 2. Defeitos e Bugs Identificados

### BUG-01 — NullPointerException garantido em `DadosListagemTarefa`

- **Arquivo:** `src/main/java/com/eduardo/studymind/dto/output/tarefa/DadosListagemTarefa.java` — linhas 28–29
- **Descrição:** O construtor chama `tarefa.getTopico().getId()` e `tarefa.getTopico().getNome()` diretamente, sem verificação de nulo. O campo `topico_id` é `nullable` na tabela `tarefas` (confirmado na migration V6). Qualquer tarefa sem tópico vinculado lança `NullPointerException` ao ser listada. A classe irmã `DadosDetalhamentoTarefa` (linhas 29–30) já faz a checagem correta com operador ternário, evidenciando que o problema foi resolvido em um lugar mas esquecido no outro.
- **Impacto:** **Alto** — quebra o endpoint `GET /tarefas` para usuários com tarefas sem tópico.

---

### BUG-02 — Stack trace exposto em produção no handler genérico

- **Arquivo:** `src/main/java/com/eduardo/studymind/exception/GlobalExceptionHandler.java` — linha 39
- **Descrição:** O handler `handleErroGenerico(Exception ex)` chama `ex.printStackTrace()`, que imprime o stack trace completo no `stderr` do servidor. Em ambientes com logs agregados (CloudWatch, Datadog, Grafana Loki), isso pode vazar nomes de classes internas, estrutura de pacotes e detalhes de implementação para logs potencialmente acessíveis. O correto seria substituir por `log.error("Erro inesperado", ex)` usando SLF4J.
- **Impacto:** **Médio** — vazamento de informações internas; violação de boas práticas de segurança (OWASP A05: Security Misconfiguration).

---

### BUG-03 — `enviadorEmail.send()` fora do bloco try-catch em `EmailService`

- **Arquivo:** `src/main/java/com/eduardo/studymind/service/EmailService.java` — linhas 36–49
- **Descrição:** O método `enviarEmail()` constrói a mensagem dentro de um `try-catch` que captura `MessagingException` e `UnsupportedEncodingException`, mas a chamada `enviadorEmail.send(message)` está na **linha 49, fora do bloco try**. `JavaMailSender.send()` lança `MailException` (que é `RuntimeException`), que não é capturada. Como o método é invocado de dentro de um contexto `@Async`, essa exceção é engolida silenciosamente — o usuário nunca recebe o email de verificação e nenhum erro é registrado.
- **Impacto:** **Alto** — emails de verificação podem falhar silenciosamente; o usuário não consegue ativar a conta e não recebe feedback de erro.

---

### BUG-04 — `AnthropicClient` sem timeout de leitura (read timeout)

- **Arquivo:** `src/main/java/com/eduardo/studymind/infra/ia/AnthropicClient.java` — linhas 28–35
- **Descrição:** O `HttpClient` configurado em `@PostConstruct` define apenas `connectTimeout(Duration.ofSeconds(10))`. Não existe `readTimeout` ou `requestTimeout`. Chamadas à API da Anthropic para geração de aulas, onboarding e recomendações podem demorar 15–60 segundos. Sem read timeout, as threads do pool de request do Spring ficam bloqueadas indefinidamente aguardando resposta, podendo causar esgotamento do thread pool (thread starvation) sob carga moderada e indisponibilidade total da aplicação.
- **Impacto:** **Alto** — risco de indisponibilidade total sob carga; impossível de diagnosticar sem logs estruturados.

---

### BUG-05 — `PerformanceAnalyzerService` faz lookup de tópico sem filtrar por usuário

- **Arquivo:** `src/main/java/com/eduardo/studymind/service/PerformanceAnalyzerService.java` — linhas 49–52
- **Descrição:** Para resolver o `topicoId`, o serviço chama `topicoRepository.findByNomeAndMateriaNome(topicoNome, materiaNome)`. Essa query não filtra por `usuario_id`. Se dois usuários diferentes tiverem tópicos com o mesmo nome e matéria (ex: "Funções" em "Matemática"), a query pode retornar o tópico de outro usuário. Ironicamente, o `topicoId` resolvido nunca é utilizado — o construtor de `DadosDesempenhoTopico` recebe `null` explicitamente (linha 54: `null, // ResultadoSessao não tem topicoId`), tornando o lookup inútil além de incorreto.
- **Impacto:** **Médio** — vazamento potencial de dados de outros usuários; funcionalidade de diagnóstico por tópico comprometida.

---

### BUG-06 — Histórico de chat deletado antes da validação completa do plano

- **Arquivo:** `src/main/java/com/eduardo/studymind/service/OnboardingService.java` — linhas 190 e 207
- **Descrição:** Em `salvarPlanoEFinalizar()`, o histórico de chat é apagado via `chatMensagemRepository.deleteAllByUsuarioId()` (linha 190) antes de `planoEstudoParser.parsearEPopular()` ser chamado (linha 207). Se o parser falhar, o `@Transactional` fará rollback de todas as operações JPA, incluindo o delete do histórico. Porém, se a falha ocorrer **fora** do contexto transacional (ex: erro de rede antes de chegar ao parser), o histórico pode ser perdido sem que o plano tenha sido salvo. Semanticamente, apagar o histórico deveria ser a última operação após confirmação de sucesso.
- **Impacto:** **Médio** — perda de contexto de conversação em cenários de falha parcial.

---

### BUG-07 — Inconsistência de regras entre os dois system prompts de onboarding

- **Arquivo:** `src/main/java/com/eduardo/studymind/service/OnboardingService.java` — linha 55 vs linha 84
- **Descrição:** `SYSTEM_PROMPT_ONBOARDING` instrui a IA: "Cada semana deve ter **exatamente 3 tarefas**, nem mais nem menos". `SYSTEM_PROMPT_REVIEW` instrui: "Cada semana deve ter **no mínimo 3 tarefas e no máximo 5 tarefas**". O `PlanoEstudoParser` não valida a quantidade de tarefas geradas — persiste tudo que vem no JSON. Isso cria inconsistência no volume de tarefas entre planos iniciais e planos de revisão.
- **Impacto:** **Baixo** — inconsistência de dados entre ciclos; sem falha crítica, mas dificulta análise de performance ao longo do tempo.

---

### BUG-08 — `ObjectMapper` instanciado a cada chamada em `AulaService`

- **Arquivo:** `src/main/java/com/eduardo/studymind/service/AulaService.java` — linhas 177 e 200
- **Descrição:** Os métodos privados `parseAulaJson()` e `parseQuestoesJson()` criam `new ObjectMapper()` a cada invocação. `ObjectMapper` é thread-safe e caro para instanciar (envolve carregamento de módulos e configuração interna). O projeto já expõe `ObjectMapper` como `@Bean` e o injeta em outros services (ex: `OnboardingService`), mas `AulaService` não o recebe por injeção.
- **Impacto:** **Baixo** — desperdício de memória e CPU a cada geração de aula ou questão; pode agravar sob carga.

---

## 3. Pontos de Melhoria Técnica

### MEL-01 — Entidade `ResultadoSessao` desnormalizada (sem chaves estrangeiras para tópico/matéria)

- **Arquivo:** `src/main/java/com/eduardo/studymind/domain/resultado/ResultadoSessao.java` — linhas 27–31
- **Descrição:** Os campos `topicoNome` (`VARCHAR(150)`) e `materiaNome` (`VARCHAR(100)`) armazenam strings literais em vez de referências `@ManyToOne` para `Topico` e `Materia`. Se o usuário renomear um tópico (via `PUT /topicos/{id}`), todos os `ResultadoSessao` históricos ficam associados ao nome antigo. O `PerformanceAnalyzerService` agrupa por chave composta `topicoNome + "|" + materiaNome`, tornando toda a análise de performance frágil a mudanças de nome.
- **Impacto:** **Alto** — integridade referencial comprometida; análises de desempenho ficam inconsistentes após renomeações.

---

### MEL-02 — Ausência de rate limiting em endpoints críticos

- **Arquivo:** `src/main/java/com/eduardo/studymind/infra/security/SecurityConfig.java`
- **Descrição:** Não há nenhum mecanismo de limitação de taxa. Endpoints vulneráveis: `/auth/login` (força bruta de senhas), `/auth/registro` (cadastros em massa/bots), `/auth/verificar` (enumeração de tokens), `/onboarding/mensagem` e `/aula/**` (abuso da API paga Anthropic). Um atacante pode esgotar todo o crédito da API da Anthropic com poucas requisições automatizadas.
- **Impacto:** **Alto** — risco financeiro direto (API paga por token) e risco de segurança (OWASP A04: Insecure Design).

---

### MEL-03 — Ausência de token de refresh JWT

- **Arquivo:** `src/main/java/com/eduardo/studymind/infra/security/JwtService.java` — linha 19
- **Descrição:** `EXPIRATION_TIME = 2 * 60 * 60 * 1000` (2 horas). Não há endpoint de refresh. Após 2 horas o usuário é deslogado e precisa autenticar novamente. Para uma aplicação de estudos que fica aberta o dia todo (sessões longas de estudo), isso representa uma fricção significativa na experiência do usuário.
- **Impacto:** **Médio** — UX degradada; pode levar a abandono da sessão de estudos.

---

### MEL-04 — Histórico de chat carregado integralmente em memória

- **Arquivo:** `src/main/java/com/eduardo/studymind/service/OnboardingService.java` — linha 132
- **Descrição:** `chatMensagemRepository.findAllByUsuarioIdOrderByCriadoEmAsc(usuario.getId())` carrega **todo** o histórico de uma vez e o concatena como texto plano no prompt enviado à IA. Conversas longas (onboardings com muitos ciclos de review) acumulam dezenas de mensagens. O custo por chamada cresce linearmente com o número de mensagens, e há risco de ultrapassar o limite de contexto do modelo Claude Haiku (200k tokens).
- **Impacto:** **Médio** — custo crescente com a IA; possível falha ao exceder o limite de tokens do modelo.

---

### MEL-05 — Ausência de cache para geração de aulas e questões

- **Arquivo:** `src/main/java/com/eduardo/studymind/service/AulaService.java`
- **Descrição:** Cada chamada a `gerarConteudoAula(topicoId)` ou `gerarQuestoes(topicoId, quantidade)` faz uma requisição completa à API da Anthropic. O conteúdo de uma aula sobre "Funções Quadráticas" nível MEDIO é praticamente o mesmo para qualquer usuário em qualquer momento. Poderia ser cacheado com `@Cacheable` do Spring (usando Redis ou Caffeine) por `(topicoId, tipo)`.
- **Impacto:** **Médio** — custo desnecessário de API e latência alta (10–30 segundos por chamada) para conteúdo que poderia ser servido em milissegundos.

---

### MEL-06 — Ausência de paginação em listagens de tópicos, questões e resultados

- **Arquivo:** `src/main/java/com/eduardo/studymind/service/TopicoService.java`, `QuestaoService.java`, `ResultadoService.java`
- **Descrição:** Os métodos `listarPorMateria()`, `listarPorTopico()` e `listarPorUsuario()` retornam `List<>` completas sem paginação. Apenas `UsuarioService.listar()` usa `Pageable`. Em um cenário de uso real com centenas de questões por tópico ou dezenas de resultados, essas listagens se tornam ineficientes.
- **Impacto:** **Médio** — degradação de performance e uso de memória à medida que o volume de dados cresce.

---

### MEL-07 — Ausência de logs estruturados em toda a camada de serviço

- **Arquivo:** Todos os services em `com.eduardo.studymind.service`
- **Descrição:** Nenhum service utiliza `@Slf4j` + `log.info()`/`log.error()` para registrar operações de negócio. Não há logs de: criação de usuário, geração de plano, chamadas à API da Anthropic (tempo de resposta, tokens usados), falhas de autenticação, ou envio de emails. Em produção, depurar um problema exige acesso direto ao banco ou tentativa de reprodução.
- **Impacto:** **Médio** — observabilidade zero; MTTR (tempo médio de resolução) de incidentes muito elevado.

---

### MEL-08 — Configuração CORS com headers wildcard combinado com `allowCredentials(true)`

- **Arquivo:** `src/main/java/com/eduardo/studymind/infra/security/SecurityConfig.java` — linhas 72–77
- **Descrição:** `config.setAllowedHeaders(List.of("*"))` combinado com `config.setAllowCredentials(true)` viola a especificação CORS (RFC 6454). Browsers modernos (Chrome 94+, Firefox) rejeitam respostas CORS com wildcard em `Access-Control-Allow-Headers` quando `Access-Control-Allow-Credentials: true` está presente. O correto é listar headers explícitos: `"Authorization"`, `"Content-Type"`, `"Accept"`.
- **Impacto:** **Médio** — pode causar erros de CORS intermitentes dependendo do browser e versão.

---

### MEL-09 — Ausência de validação de tamanho nas mensagens do chat e campos de texto livre

- **Arquivo:** `src/main/java/com/eduardo/studymind/dto/input/onboarding/DadosMensagemChat.java`
- **Descrição:** O DTO `DadosMensagemChat` valida apenas `@NotNull`. Sem `@Size(max = N)`, um usuário pode enviar mensagens de vários megabytes que serão salvas na coluna `TEXT` e reenviadas integralmente para a API paga da Anthropic. O mesmo se aplica a campos como `descricao` em `DadosCadastroMateria` e `DadosCadastroTopico`.
- **Impacto:** **Médio** — risco de abuso e custo descontrolado com a API da Anthropic.

---

### MEL-10 — Cobertura de testes insuficiente para componentes críticos

- **Arquivos:** `src/test/java/com/eduardo/studymind/`
- **Descrição:** Não existem testes para: `EmailService` (falha de envio, `send()` fora do try-catch), `AuthFilter` (token inválido, token expirado, ausência de token), `AnthropicClient` (timeout, resposta malformada), e cenários de falha do `PlanoEstudoParser` (JSON incompleto, enum inválido). Os testes existentes cobrem o caminho feliz dos services, mas os cenários de erro críticos permanecem sem cobertura.
- **Impacto:** **Médio** — regressões nos componentes mais críticos só são detectadas em produção.

---

## 4. Dívidas Técnicas e Code Smells

### DT-01 — Prompts de IA hardcoded como constantes estáticas no service

- **Arquivo:** `src/main/java/com/eduardo/studymind/service/OnboardingService.java` — linhas 35–93
- **Descrição:** `SYSTEM_PROMPT_ONBOARDING` e `SYSTEM_PROMPT_REVIEW` são constantes `private static final String` com ~60 linhas de texto cada, embutidas na classe de serviço. Qualquer ajuste de instrução à IA exige recompilação e redeploy. Prompts deveriam estar em arquivos de configuração (`classpath:prompts/onboarding.txt`), banco de dados ou um sistema de prompt versioning externo.

---

### DT-02 — Modelo da IA hardcoded em `AnthropicClient`

- **Arquivo:** `src/main/java/com/eduardo/studymind/infra/ia/AnthropicClient.java` — linha 23
- **Descrição:** `private static final String MODEL = "claude-haiku-4-5-20251001"` está fixo. Alterar para um modelo mais capaz (ex: Claude Sonnet para onboarding mais sofisticado) exige modificação de código e redeploy. Deveria ser configurável via `application.properties`: `anthropic.model=${ANTHROPIC_MODEL:claude-haiku-4-5-20251001}`.

---

### DT-03 — Remetente de email hardcoded em `EmailService`

- **Arquivo:** `src/main/java/com/eduardo/studymind/service/EmailService.java` — linha 25
- **Descrição:** `private static final String EMAIL_ORIGEM = "studymind@gmail.com"` está fixo no código. Em uma migração de domínio de email (ex: para `noreply@studymind.com.br`) seria necessário alterar o código em vez de apenas mudar uma variável de ambiente.

---

### DT-04 — `DadosListagemTarefa` e `DadosDetalhamentoTarefa` com mapeamento duplicado

- **Arquivos:** `dto/output/tarefa/DadosListagemTarefa.java` e `DadosDetalhamentoTarefa.java`
- **Descrição:** Ambos os records mapeiam os mesmos campos de `Tarefa` (id, usuario, topico, tipo, descricao, meta, prazo, status). A única diferença real é o campo `criadaEm` em `DadosDetalhamento`. A duplicação de lógica já resultou no BUG-01 (verificação de nulo presente num record mas ausente no outro).

---

### DT-05 — `OnboardingService` não usa a API de mensagens estruturadas da Anthropic

- **Arquivo:** `src/main/java/com/eduardo/studymind/service/OnboardingService.java` — linhas 160–171
- **Descrição:** O histórico de conversa é serializado como texto simples (`"Aluno: ...\nAssistente: ..."`), concatenado num único prompt textual. A API da Anthropic suporta a estrutura de `messages` com `role: user/assistant`, que é semanticamente mais precisa, melhora a qualidade das respostas e separa claramente o `system prompt` das mensagens do usuário. A interface `AIClient` só aceita uma `String`, forçando essa serialização manual e impedindo uso de recursos avançados (system prompt separado, streaming, etc.).

---

### DT-06 — TODO obsoleto em `SecurityConfig`

- **Arquivo:** `src/main/java/com/eduardo/studymind/infra/security/SecurityConfig.java` — linha 74
- **Descrição:** O comentário `// TODO: controle de acesso por role será implementado em versão futura` existe ao lado da linha comentada `// .requestMatchers("/admin/**").hasRole("ADMIN")`. O controle de admin já foi implementado nas linhas 49–55 com `hasAuthority("ADMIN")` em rotas específicas. O TODO é obsoleto e confuso para quem lê o código pela primeira vez.

---

### DT-07 — Verificação de autoridade como string literal em vez de enum

- **Arquivo:** `src/main/java/com/eduardo/studymind/infra/security/SecurityConfig.java` — linhas 49–55
- **Descrição:** `.hasAuthority("ADMIN")` usa string literal. O projeto já tem `com.eduardo.studymind.domain.usuario.Role` com o valor `ADMIN`. Usar `Role.ADMIN.name()` tornaria o código refatorável com segurança de tipo (o compilador detectaria um enum renomeado).

---

### DT-08 — Pacotes de DTOs de saída com nomenclatura inconsistente

- **Arquivos:** `src/main/java/com/eduardo/studymind/dto/output/`
- **Descrição:** A maioria dos pacotes segue o padrão `dto/output/[entidade]/` (ex: `tarefa`, `usuario`, `materia`), mas há exceções: `aulaoutput`, `questaogerada`, `questoesoutput`, `tarefadescricaooutput`. Os nomes deveriam ser `aula`, `questao`, `tarefadescricao` para consistência.

---

### DT-09 — `PlanoEstudoParser` usa `RuntimeException` genérica como mecanismo de erro

- **Arquivo:** `src/main/java/com/eduardo/studymind/service/parser/PlanoEstudoParser.java` — linhas 52–53 e linha 139
- **Descrição:** O bloco catch captura `Exception` e relança como `new RuntimeException("Erro ao parsear plano de estudos", e)`. O projeto possui exceções customizadas (`RegrasDeNegocioException`, `ErroIntegracaoIAException`) que seriam semanticamente mais corretas e teriam handlers específicos no `GlobalExceptionHandler`. Com `RuntimeException`, o handler genérico é acionado e o `ex.printStackTrace()` vaza o stack trace (BUG-02).

---

### DT-10 — `AIClient` com interface de baixo nível (apenas aceita String)

- **Arquivo:** `src/main/java/com/eduardo/studymind/infra/ia/AIClient.java`
- **Descrição:** A interface define apenas `String gerarResposta(String prompt)`. Isso força todos os callers a concatenar manualmente o histórico de conversa em texto (DT-05), impede uso de `system prompt` separado, streaming, controle de `max_tokens` por chamada ou uso de ferramentas (tool use). A interface deveria evoluir para aceitar uma lista de mensagens estruturadas.

---

## 5. Ideias de Novas Features

### F-01 — Simulado Cronometrado

- **Descrição funcional:** O aluno seleciona tópicos e quantidade de questões; o sistema monta um simulado com timer configurável (ex: 90 minutos para 30 questões). Ao finalizar, exibe gabarito comentado, taxa de acerto por tópico e salva os resultados automaticamente.
- **Viabilidade técnica:** Aproveita `Questao`, `Resultado`, `ResultadoSessao` e `AulaService.gerarQuestoes()`. Requer nova entidade `Simulado` com campos `prazo`, `status`, `questoes`; endpoints `POST /simulado` e `POST /simulado/{id}/finalizar`.
- **Esforço estimado:** **Médio**

---

### F-02 — Notificações de Lembrete de Tarefas com Prazo

- **Descrição funcional:** O sistema envia um email automático quando uma tarefa está a 1 dia do prazo (`prazo`) ou quando há tarefas PENDENTE há mais de 3 dias sem interação.
- **Viabilidade técnica:** Aproveita `Tarefa` (campo `prazo`), `TarefaRepository`, `EmailService` (infraestrutura já configurada) e `@Scheduled` do Spring. Um job `@Scheduled(cron = "0 8 * * *")` consulta tarefas pendentes próximas do prazo e dispara emails.
- **Esforço estimado:** **Pequeno**

---

### F-03 — Dashboard de Progresso do Plano de Estudos

- **Descrição funcional:** Visualização de quantas semanas do plano foram concluídas, percentual de tarefas CONCLUIDA vs PENDENTE por semana, e evolução da taxa de acerto por matéria desde o início do plano.
- **Viabilidade técnica:** Aproveita `PlanoEstudo.conteudoJson` (JSON com semanas/tarefas), `Tarefa` (status), `ResultadoSessao` (taxaAcerto por matéria) e `PerformanceAnalyzerService`. Requer novo DTO `DadosProgressoPlano` e endpoint `GET /plano-estudo/progresso`.
- **Esforço estimado:** **Médio**

---

### F-04 — Revisão Espaçada (Spaced Repetition)

- **Descrição funcional:** Com base nos `Resultado` com status INCORRETO, o sistema agenda automaticamente tarefas de revisão para os próximos dias usando uma versão simplificada do algoritmo SM-2. Questões erradas reaparecem no dia seguinte; questões acertadas têm intervalo crescente.
- **Viabilidade técnica:** Aproveita `Resultado`, `Questao`, `Tarefa` (tipo REVISAO). Novo serviço `SpacedRepetitionService` consulta erros recentes e cria tarefas de revisão com `prazo` calculado. A entidade `Tarefa` já tem `topico_id` (nullable), suportando o vínculo.
- **Esforço estimado:** **Grande**

---

### F-05 — Exportação do Plano para PDF ou Calendário (`.ics`)

- **Descrição funcional:** O aluno pode baixar o plano de estudos atual como PDF formatado (com todas as semanas, tarefas e metas) ou exportar como arquivo `.ics` para importar no Google Calendar ou Outlook, com alertas automáticos.
- **Viabilidade técnica:** Aproveita `PlanoEstudo.conteudoJson` e `Tarefa` (campo `prazo`, `descricao`). Para `.ics` basta gerar texto padrão iCalendar — sem nova dependência. Para PDF, requer dependência (iText ou Apache PDFBox).
- **Esforço estimado:** **Médio** (`.ics` pequeno; PDF médio)

---

### F-06 — Modo Flashcards com IA

- **Descrição funcional:** Para cada tópico, a IA gera pares pergunta/resposta curtos (flashcards). O aluno os revisa e marca como "lembrou" ou "não lembrou". O sistema prioriza flashcards não lembrados nas sessões seguintes e registra o progresso.
- **Viabilidade técnica:** Aproveita `AIClient`, `Topico`, `ResultadoSessao`. Requer nova entidade `Flashcard` (topicoId, pergunta, resposta, criadoEm) e novo prompt no `AulaService` para geração de pares. O fluxo de resposta é similar ao de questões.
- **Esforço estimado:** **Médio**

---

### F-07 — Relatório Semanal por Email

- **Descrição funcional:** Toda segunda-feira de manhã, o aluno recebe um email com um resumo da semana anterior: questões respondidas, taxa de acerto, tarefas concluídas e uma mensagem motivacional personalizada gerada pela IA com base no desempenho.
- **Viabilidade técnica:** Aproveita `ResultadoSessao`, `Tarefa`, `EmailService`, `AIClient` e `RecommendationService`. Apenas um job `@Scheduled(cron = "0 8 * * MON")` que orquestra os dados já existentes e dispara o email.
- **Esforço estimado:** **Pequeno**

---

### F-08 — Comparativo de Desempenho por Período

- **Descrição funcional:** O aluno visualiza a evolução da sua taxa de acerto ao longo do tempo: gráfico por semana/mês em cada matéria, comparando o desempenho no início do plano vs. o período mais recente.
- **Viabilidade técnica:** Aproveita `ResultadoSessao` (campo `respondidoEm`) e `PerformanceAnalyzerService`. Requer uma query agrupada por período (`GROUP BY DATE_TRUNC('week', respondido_em), topico_nome`) e novo endpoint `GET /dashboard/evolucao?periodo=semanas`.
- **Esforço estimado:** **Pequeno**

---

### F-09 — Desafio Diário com Sistema de Streak

- **Descrição funcional:** Todo dia, o sistema seleciona automaticamente 5 questões dos tópicos mais fracos do aluno e as apresenta como "Desafio do Dia". Ao concluir o desafio, o aluno mantém seu streak (dias consecutivos). Quebrar o streak por um dia aparece com alerta motivacional.
- **Viabilidade técnica:** Aproveita `PerformanceAnalyzerService` (tópicos mais fracos), `Questao`, `Resultado` (para evitar repetição de questões já acertadas). Requer nova entidade `StreakUsuario` (usuarioId, ultimoDesafio, diasConsecutivos).
- **Esforço estimado:** **Médio**

---

### F-10 — Onboarding Assistido por Boletim do ENEM

- **Descrição funcional:** O aluno informa suas notas do ENEM anterior (por área de conhecimento). O sistema pré-preenche as matérias e níveis de dificuldade automaticamente com base nas notas, pulando as perguntas de onboarding sobre esse aspecto e acelerando a criação do plano.
- **Viabilidade técnica:** Aproveita o fluxo `OnboardingService`, `Materia`, `Topico` e `PlanoEstudoParser`. Requer apenas um novo endpoint `POST /onboarding/importar-notas-enem` que transforma as notas informadas em JSON compatível com o `PlanoEstudoParser`.
- **Esforço estimado:** **Médio** (sem integração externa; apenas parsing das notas informadas pelo usuário)

---

## 6. Prioridades Recomendadas

| # | Item | Tipo | Impacto | Esforço | Prioridade |
|---|------|------|---------|---------|------------|
| 1 | **BUG-03** — `send()` fora do try-catch em `EmailService` | Bug | Alto | Baixo | 🔴 Crítica |
| 2 | **BUG-01** — NPE em `DadosListagemTarefa` | Bug | Alto | Baixo | 🔴 Crítica |
| 3 | **BUG-04** — Sem read timeout no `AnthropicClient` | Bug | Alto | Baixo | 🔴 Crítica |
| 4 | **MEL-01** — `ResultadoSessao` sem FK para tópico/matéria | Melhoria | Alto | Médio | 🟠 Alta |
| 5 | **MEL-02** — Sem rate limiting em endpoints de IA e auth | Melhoria | Alto | Médio | 🟠 Alta |
| 6 | **BUG-02** — `ex.printStackTrace()` em produção | Bug | Médio | Baixo | 🟠 Alta |
| 7 | **BUG-05** — Lookup de tópico sem filtro por usuário | Bug | Médio | Baixo | 🟠 Alta |
| 8 | **MEL-08** — CORS wildcard + credentials | Melhoria | Médio | Baixo | 🟠 Alta |
| 9 | **MEL-07** — Sem logs estruturados (SLF4J/@Slf4j) | Melhoria | Médio | Médio | 🟡 Média |
| 10 | **MEL-03** — Sem token de refresh JWT | Melhoria | Médio | Médio | 🟡 Média |
| 11 | **DT-01** — Prompts de IA hardcoded no código | Dívida | Médio | Médio | 🟡 Média |
| 12 | **MEL-05** — Sem cache para aulas geradas pela IA | Melhoria | Médio | Médio | 🟡 Média |
| 13 | **MEL-04** — Histórico de chat ilimitado em memória | Melhoria | Médio | Baixo | 🟡 Média |
| 14 | **DT-05** — API da Anthropic sem mensagens estruturadas | Dívida | Médio | Grande | 🟡 Média |
| 15 | **BUG-08** — `ObjectMapper` instanciado a cada chamada | Bug | Baixo | Baixo | 🟢 Baixa |
| 16 | **F-07** — Relatório semanal por email | Feature | Alto (UX) | Pequeno | 🟠 Alta |
| 17 | **F-02** — Notificação de tarefas com prazo | Feature | Alto (UX) | Pequeno | 🟠 Alta |
| 18 | **F-08** — Comparativo de desempenho por período | Feature | Médio | Pequeno | 🟡 Média |
| 19 | **F-03** — Dashboard de progresso do plano | Feature | Alto (UX) | Médio | 🟡 Média |
| 20 | **F-01** — Simulado cronometrado | Feature | Alto (core) | Médio | 🟡 Média |
| 21 | **F-09** — Desafio diário com streak | Feature | Alto (engajamento) | Médio | 🟡 Média |
| 22 | **F-04** — Revisão espaçada (spaced repetition) | Feature | Alto (diferencial) | Grande | 🟢 Futura |

### Resumo executivo

**Correções urgentes — fazer antes de qualquer release em produção:**

1. Mover `enviadorEmail.send(message)` para dentro do bloco try-catch em `EmailService.java` (linha 49)
2. Adicionar checagem `tarefa.getTopico() != null` em `DadosListagemTarefa.java` (linhas 28–29), idêntica à já presente em `DadosDetalhamentoTarefa`
3. Configurar `requestTimeout` ou `readTimeout` no `HttpClient` do `AnthropicClient.java` (ex: `Duration.ofSeconds(60)`)

**Melhorias de alta prioridade — próximo sprint:**

4. Normalizar `ResultadoSessao` adicionando `@ManyToOne topico` e `@ManyToOne materia` (migration V16)
5. Adicionar rate limiting com Bucket4j ou Spring Cloud Gateway nos endpoints `/auth/**`, `/onboarding/**` e `/aula/**`
6. Substituir `ex.printStackTrace()` por `log.error("Erro inesperado", ex)` no `GlobalExceptionHandler`

**Features de maior ROI — menor esforço, maior impacto de UX:**

7. Relatório semanal por email (aproveita 100% da infraestrutura existente, apenas orquestração)
8. Notificação de tarefas com prazo (`@Scheduled` + `EmailService` já prontos)
9. Endpoint de comparativo de desempenho por período (nova query + DTO)

---

*Este relatório foi gerado por análise estática completa do código-fonte presente no repositório. Nenhum arquivo de código-fonte foi modificado.*