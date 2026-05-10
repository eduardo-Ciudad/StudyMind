# Relatório Técnico — StudyMind API
**Versão:** 2.0 | **Data:** 2026-05-10 | **Revisor:** Análise Estática Automatizada

---

## 1. Visão Geral do Projeto

**Objetivo:** API REST para uma plataforma de estudos voltada ao vestibular brasileiro. O sistema gerencia matérias, tópicos, questões, resultados de alunos, tarefas de estudo e gera recomendações personalizadas com IA.

**Stack tecnológica:**

| Camada | Tecnologia |
|--------|-----------|
| Framework | Spring Boot 3.5.14 |
| Linguagem | Java 17 |
| Banco de dados | PostgreSQL |
| Migrations | Flyway |
| Segurança | Spring Security + JWT (auth0 java-jwt 4.4.0) |
| IA | Anthropic Claude Haiku via HTTP (RestClient) |
| Documentação | SpringDoc OpenAPI 2.8.8 (Swagger UI) |
| Redução de boilerplate | Lombok |

**Estrutura de pacotes:**
```
com.eduardo.studymind
├── controller/          (11 controllers)
├── domain/              (entidades + repositories por agregado)
│   ├── materia/
│   ├── questao/
│   ├── resultado/
│   ├── tarefa/
│   ├── topico/
│   └── usuario/
├── dto/
│   ├── input/           (dados de entrada, com validação Bean Validation)
│   └── output/          (dados de saída em Records)
├── exception/           (exceções de domínio + handler global)
├── infra/
│   ├── ia/              (interface AIClient + AnthropicClient)
│   └── security/        (AuthFilter, JwtService, SecurityConfig, SecurityUtils)
└── service/             (8 services de negócio)
```

---

## 2. Arquitetura

### Organização em camadas

O projeto segue a arquitetura **Controller → Service → Repository** de forma consistente e sem desvios. Cada camada tem responsabilidade clara.

### Separação Controller / Service / Repository

- Controllers recebem e validam entrada, delegam para services e retornam DTOs de saída.
- Services contêm toda a lógica de negócio, conversão de entidade para DTO e controle transacional.
- Repositories estendem `JpaRepository` e expõem apenas queries derivadas e JPQL explícita quando necessário.

### DTOs como Records Java

Todos os DTOs de entrada e saída são `record`, aproveitando a imutabilidade e a concisão do Java 16+. A separação entre pacotes `input/` e `output/` é excelente — evita que o mesmo objeto carregue campos ora obrigatórios, ora opcionais conforme o contexto.

### Tratamento de exceções centralizado

`GlobalExceptionHandler` com `@RestControllerAdvice` trata quatro tipos:
- `RecursoNaoEncontradoException` → 404
- `RegrasDeNegocioException` → 422
- `MethodArgumentNotValidException` → 400
- `Exception` genérica → 500

### Padrões observados

- **Repository Pattern** via Spring Data JPA
- **DTO Pattern** (input/output separation)
- **Strategy / DIP** na camada de IA via interface `AIClient`
- **Soft Delete** (campo `ativo/ativa`) em todas as entidades principais
- **Stateless** — sem sessão HTTP (SessionCreationPolicy.STATELESS)

---

## 3. Qualidade do Código

### Pontos positivos

- Nomenclatura **muito consistente**: `DadosCadastro*`, `DadosAtualizacao*`, `DadosDetalhamento*`, `DadosListagem*` — padrão mantido em todo o projeto.
- `var` utilizado com moderação e de forma clara.
- Lombok reduz boilerplate sem ocultar comportamento importante.
- `@EqualsAndHashCode(of = "id")` em todas as entidades — prática correta para JPA.
- `@Column(updatable = false)` em `criadoEm` / `respondidoEm` — boa proteção de dados imutáveis.

### Problemas de qualidade

**Inconsistência nas mensagens de erro:**
```java
// UsuarioService
"Usuario nao encontrado"  // sem acento, inconsistente
"Usuario não Encontrado"  // letra maiúscula inesperada
```
Há pelo menos 6 variações de capitalização e acentuação nas mensagens de `RecursoNaoEncontradoException`.

**`ex.printStackTrace()` em produção:**
```java
// GlobalExceptionHandler.java:39
ex.printStackTrace();  // deve ser substituído por log estruturado (SLF4J)
```

**Imports não utilizados:**
- `TopicoController.java`: importa `org.w3c.dom.stylesheets.LinkStyle` e `org.springframework.beans.factory.annotation.Autowired` sem usá-los.
- `MateriaController.java`: importa `MateriaRepository` sem usá-lo.
- `MateriaService.java`: importa `jakarta.persistence.EntityNotFoundException` sem usá-la.
- `UsuarioService.java`: importa `java.util.List` sem usá-lo.

**Typos em nomes de arquivo de migration:**
- `V3__crate-table-topicos.sql` (falta o "e" de "create")
- `V4__crate-table-questoes.sql` (idem)
- `application.porperties.teste` (grafia errada de "properties")

**Comentários pedagógicos no código de produção:**
```java
// TopicoService.java
// esse ? e : é umamaneira simples de escrever if e else
// (condição) ? seSim : seNao
```
Comentários que explicam sintaxe básica da linguagem não têm lugar no código de produção.

---

## 4. Princípios SOLID

### S — Single Responsibility Principle ✅ Bem aplicado

Cada service tem escopo bem definido. `PerformanceAnalyzerService` calcula desempenho; `RecommendationService` orquestra IA; `JwtService` gerencia tokens. Não há "god classes".

### O — Open/Closed Principle ✅ Respeitado onde aplicável

A interface `AIClient` permite adicionar novos provedores de IA (OpenAI, Gemini) sem modificar `RecommendationService`. A hierarquia de exceções (`RecursoNaoEncontradoException`, `RegrasDeNegocioException`) é extensível.

### L — Liskov Substitution Principle ✅ Correto

`AnthropicClient implements AIClient` — a implementação cumpre o contrato da interface sem surpresas. `Usuario implements UserDetails` — comportamentos como `isEnabled()` são derivados do estado real da entidade (`ativo`).

### I — Interface Segregation Principle ✅ Bem aplicado

`AIClient` tem apenas um método (`gerarResposta`), sem forçar implementações desnecessárias. Os Repositories usam apenas os métodos necessários de `JpaRepository`.

### D — Dependency Inversion Principle ✅ Destaque positivo

```java
// RecommendationService depende da abstração, não da implementação
private final AIClient aiClient;  // injetado via construtor
```
Trocar o provedor de IA exige apenas criar uma nova implementação de `AIClient` e ajustar a configuração — sem tocar nos services de negócio.

---

## 5. Segurança

### O que está bem implementado

- **BCrypt** para hash de senhas — correto.
- **JWT com HMAC256** — implementação sólida com expiração de 2 horas.
- **Sessão stateless** — sem risco de fixação de sessão.
- **OncePerRequestFilter** — `AuthFilter` garante execução única por request.
- **`SecurityUtils.verificarOwnership`** aplicado a rotas sensíveis (resultados, tarefas, recomendações, dashboard).
- **Bean Validation** com `@NotBlank`, `@Email`, `@Size` nos DTOs de entrada.

### Problemas encontrados

#### CRÍTICO — Senha do banco hardcoded no application.properties
```properties
# application.properties:5
spring.datasource.password=${DB_PASSWORD:Vanda1107.}
```
O valor padrão expõe a senha real no código-fonte. Qualquer desenvolvedor com acesso ao repositório tem acesso ao banco de produção. A senha deve existir apenas como variável de ambiente, sem fallback.

#### CRÍTICO — Bug no verificador de permissão de ADMIN
```java
// SecurityUtils.java:19
boolean isAdmin = usuarioAutenticado.getAuthorities().stream()
    .anyMatch(a -> a.getAuthority().equals("ADMIN"));
```
As authorities são geradas como `"ROLE_ADMIN"` (via `"ROLE_" + role.name()`), mas a comparação é feita contra `"ADMIN"` sem o prefixo. Resultado: **nenhum usuário jamais é reconhecido como ADMIN** por esse método. Administradores perdem todos os privilégios de acesso a dados de outros usuários.

#### ALTO — Ausência de verificação de ownership no POST e PUT/DELETE de Tarefas
```java
// TarefaController.java
@PostMapping  // qualquer usuário autenticado cria tarefa para qualquer usuário
@PutMapping("/{id}")  // qualquer usuário atualiza qualquer tarefa
@DeleteMapping("/{id}")  // qualquer usuário cancela qualquer tarefa
```
O `verificarOwnership` só é chamado no `GET /tarefas/usuario/{usuarioId}`. As mutações não verificam se o caller é o dono da tarefa.

#### ALTO — POST /resultados sem verificação de ownership
```java
// ResultadoController.java
@PostMapping  // usuário A pode submeter resultado em nome do usuário B
```
Qualquer usuário autenticado pode registrar resultados em nome de outro usuário via `DadosCadastroResultado.usuarioId`.

#### MÉDIO — Rota de admin comentada, sem RBAC implementado
```java
// SecurityConfig.java:35
// .requestMatchers("/admin/**").hasRole("ADMIN")
```
O controle de acesso baseado em roles está comentado. Sem RBAC, qualquer aluno pode criar/editar matérias, tópicos e questões, que são operações tipicamente administrativas.

#### MÉDIO — Sem rate limiting na rota de IA
`GET /recomendacao/usuario/{id}` faz uma chamada externa à API da Anthropic a cada request. Sem limite, um usuário pode gerar custos ilimitados ou esgotar quota da API.

---

## 6. Banco de Dados

### Modelagem e migrations

7 migrations incrementais (V1–V7) com progressão lógica e correta:
```
V1: usuarios → V2: materias → V3: topicos → V4: questoes
V5: resultados → V6: tarefas → V7: indexes
```

### Pontos positivos

- FK constraints explícitas com nome (`fk_topico_materia`, etc.) — facilita manutenção.
- Soft delete (`ativo/ativa BOOLEAN NOT NULL DEFAULT TRUE`) em todas as tabelas principais.
- `DEFAULT NOW()` nos campos de auditoria.
- `BIGSERIAL` com `PRIMARY KEY` — sequência implícita, correto para PostgreSQL.

### Índices (V7)

Índices criados:
- `idx_usuarios_email` — essencial para autenticação.
- `idx_topicos_materia_id` — filtros por matéria.
- `idx_questoes_topico_id` — join questão→tópico.
- `idx_resultados_usuario_id`, `idx_resultados_questao_id` — queries de desempenho.
- `idx_tarefas_usuario_id`, `idx_tarefas_topico_id`, `idx_tarefas_status` — listagem e filtros.

### Problemas

**Ausência de unique constraint em `materias.nome`:** O service valida via `existsByNome`, mas sem a constraint no banco, inserções concorrentes podem violar a unicidade sem que o Java perceba.

**Ausência de unique constraint composta em `topicos(nome, materia_id)`:** O service valida via `existsByNomeAndMateriaId`, mas o banco não garante.

**Typos nos nomes de arquivo:** `V3__crate-table-topicos.sql` e `V4__crate-table-questoes.sql` — Flyway aceita, mas prejudica a legibilidade do histórico de migrations.

**Ausência de auditoria em `materias` e `topicos`:** Tabelas não têm campo `criado_em` / `atualizado_em`, dificultando troubleshooting e auditoria futura.

---

## 7. Camada de IA

### Arquitetura

```
RecommendationService
  └── AIClient (interface)
        └── AnthropicClient (implementação via RestClient)
  └── PerformanceAnalyzerService (dados de entrada para o prompt)
```

### Pontos positivos

- **Desacoplamento exemplar** via interface `AIClient` — segue DIP e facilita testes e troca de provedor.
- **Prompt engineering de qualidade**: o prompt inclui contexto estruturado (total de respostas, taxa de acerto, tópicos mais fracos com porcentagem) e instrui a IA a responder exclusivamente em JSON com schema definido.
- **Sanitização da resposta**: `parseResposta` remove blocos markdown ` ```json ``` ` caso a IA os inclua — prática defensiva real.
- **Uso de RestClient** — cliente HTTP moderno do Spring 6.1, substituindo `RestTemplate`.

### Problemas

#### ALTO — N+1 queries no PerformanceAnalyzerService
```java
// PerformanceAnalyzerService.java:62-66
// Para cada tópico, executa 2 queries no banco:
long total   = resultadoRepository.findAllByUsuarioIdAndQuestaoTopicoId(...).size();
long acertos = resultadoRepository.countByUsuarioIdAndQuestaoTopicoIdAndStatus(...);
```
Se houver 50 tópicos ativos, isso gera **101 queries** por chamada ao dashboard: 1 para buscar tópicos + 2×50 para calcular desempenho. Requer consolidação em uma query com `GROUP BY` e `COUNT`.

#### ALTO — `ObjectMapper` instanciado por request
```java
// RecommendationService.java:70
var mapper = new ObjectMapper();  // novo objeto a cada chamada
```
`ObjectMapper` é uma classe pesada e thread-safe — deve ser um `@Bean` singleton.

#### MÉDIO — `RestClient` não é um Bean gerenciado
```java
// AnthropicClient.java:22
private final RestClient restClient = RestClient.create();  // fora do contexto Spring
```
Criado como campo direto sem pool de conexões HTTP configurado. Deve ser injetado como `@Bean` para beneficiar de configurações globais (timeout, logging, conexão pooling).

#### MÉDIO — Sem timeout para chamadas externas
Nenhum timeout configurado para a chamada à API da Anthropic. Uma latência alta bloqueia a thread indefinidamente.

#### MÉDIO — Sem fallback para falha da IA
Se a API da Anthropic estiver indisponível ou retornar JSON inválido, o sistema lança `RuntimeException` e retorna 500. Recomenda-se uma resposta de fallback com `DadosRecomendacao` genérico ou mensagem de erro amigável.

---

## 8. Testes

### Cobertura geral

| Classe testada | Testes | Técnica |
|----------------|--------|---------|
| `TarefaService` | 6 | Mockito |
| `MateriaService` | 5 | Mockito |
| `TopicoService` | 5 | Mockito |
| `UsuarioService` | 5 | Mockito |
| `ResultadoService` | 4 | Mockito |
| `QuestaoService` | 4 | Mockito + ArgumentCaptor |
| `AuthController` | 4 | MockMvc (@SpringBootTest) |

**Total:** ~37 cenários de teste.

### Pontos positivos

- `@DisplayName` descritivo em todos os testes — excelente para leitura de relatórios de CI.
- Uso correto de `@ExtendWith(MockitoExtension.class)` — sem Spring context desnecessário nos testes de service.
- Padrão **given/when/then** implícito e bem organizado em `MateriaServiceTest`.
- `ArgumentCaptor` utilizado em `questaoServiceTest` para verificar os dados reais passados ao `save()` — técnica avançada e demonstra domínio do Mockito.
- `AuthControllerTest` com `MockMvc` e `@SpringBootTest` — teste de integração da camada HTTP incluindo segurança.
- Teste de validação de senha fraca no registro — cobre um caminho de Bean Validation.

### Bugs críticos nos testes

#### BUG — MateriaServiceTest espera exceção errada
```java
// MateriaServiceTest.java:112
assertThatThrownBy(() -> materiaService.buscarPorID(99L))
    .isInstanceOf(EntityNotFoundException.class)  // ERRADO
    .hasMessage("Materia nao encontrada");
```
O service lança `RecursoNaoEncontradoException`, não `EntityNotFoundException`. Este teste **passa em falso** ou falha silenciosamente dependendo da versão — indica que a exception foi trocada durante refatoração sem atualizar os testes.

#### BUG — TarefaServiceTest com mensagem de exceção errada
```java
// TarefaServiceTest.java:155
.hasMessage("Não é possivel cancelar uma tarefa já cadastrada");  // ERRADO
```
O service lança `"Não é possivel cancelar uma tarefa já concluída"`. A mensagem no teste não corresponde ao comportamento real.

### Lacunas de cobertura

- `RecommendationService` — sem nenhum teste.
- `PerformanceAnalyzerService` — sem nenhum teste (contém lógica crítica e N+1).
- Endpoints além de `/auth` — sem testes de controller para `UsuarioController`, `TarefaController`, etc.
- Cenários de segurança — não há testes para `SecurityUtils.verificarOwnership`.
- Filtro JWT (`AuthFilter`) — sem testes de integração para tokens inválidos/expirados.

---

## 9. APIs REST

### Organização dos endpoints

| Recurso | Rotas disponíveis |
|---------|-------------------|
| `/auth` | POST /login, POST /registro |
| `/usuarios` | POST, GET, GET /{id}, PUT /{id}, DELETE /{id} |
| `/materias` | POST, GET, GET /{id}, PUT /{id}, DELETE /{id} |
| `/topicos` | POST, GET, GET /{id}, PUT /{id}, DELETE /{id} |
| `/questoes` | POST, GET (paginado), PUT /{id}, DELETE /{id} |
| `/resultados` | POST, GET /usuario/{id}, GET /{id} |
| `/tarefas` | POST, GET /usuario/{id}, PUT /{id}, DELETE /{id} |
| `/dashboard` | GET /usuario/{id} |
| `/diagnostico` | GET /usuario/{id} |
| `/recomendacao` | GET /usuario/{id} |

### Padrões HTTP

- ✅ `201 Created` com `Location` header em todos os endpoints de criação.
- ✅ `204 No Content` em todos os endpoints de exclusão.
- ✅ `200 OK` para leituras e atualizações.
- ✅ Paginação com `@PageableDefault` nos endpoints de listagem de questões, resultados e usuários.
- ⚠️ Listagem de matérias e tópicos sem paginação — pode se tornar problema com crescimento.

### Rota duplicada de cadastro de usuário

`POST /usuarios` (UsuarioController) e `POST /auth/registro` (AuthController) chamam o mesmo service. A rota `/usuarios` é mais adequada para admins criarem usuários; `/auth/registro` é o auto-cadastro. Ambas não têm distinção de permissão atualmente.

### Documentação Swagger

SpringDoc configurado e liberado sem autenticação (`/v3/api-docs/**`, `/swagger-ui/**`). Todos os endpoints estarão documentados automaticamente via reflexão.

---

## 10. Performance

### Uso de FetchType.LAZY

Todos os relacionamentos `@ManyToOne` usam `FetchType.LAZY`:
- `Topico.materia`
- `Questao.topico`
- `Resultado.usuario`, `Resultado.questao`
- `Tarefa.usuario`, `Tarefa.topico`

Correto — evita carregamento desnecessário de entidades relacionadas.

### Consulta otimizada com JOIN FETCH

```java
// TopicoRepository.java:14
@Query("SELECT t FROM Topico t JOIN FETCH t.materia WHERE t.ativo = true")
List<Topico> findAllByAtivoTrueWithMateria();
```
Excelente — evita N+1 ao acessar `topico.getMateria()` no loop do `PerformanceAnalyzerService`.

### N+1 residual em PerformanceAnalyzerService

Apesar do JOIN FETCH acima, o cálculo de desempenho por tópico ainda executa 2 queries por tópico (ver seção 7). Para um aluno com 100 tópicos ativos, isso resulta em ~201 queries por acesso ao dashboard ou à geração de recomendação.

### Paginação

Questões, resultados por usuário e usuários usam `Pageable` corretamente. Matérias e tópicos retornam `List` completo — aceitável para dados de catálogo que crescem lentamente.

---

## 11. Pontos Positivos — Destaques

1. **Interface `AIClient` e injeção de dependência** — padrão profissional que desacopla a lógica de negócio do provedor externo.

2. **Records Java para DTOs** — uso moderno e idiomático de Java 17, com imutabilidade garantida.

3. **Soft delete consistente** — em vez de excluir registros, o sistema desativa entidades, preservando histórico e integridade referencial.

4. **`SecurityUtils.verificarOwnership`** — utilitário centralizado para controle de acesso por proprietário, aplicado corretamente na maioria das rotas sensíveis.

5. **Hierarquia de exceções clara** — `RecursoNaoEncontradoException` e `RegrasDeNegocioException` expressam intenção semântica e mapeiam para HTTP status codes específicos.

6. **Prompt de IA bem estruturado** — inclui dados contextuais reais (taxa de acerto, tópicos mais fracos) e instrui formato de resposta JSON explicitamente, com sanitização defensiva da resposta.

7. **V7 de migrations com índices** — o desenvolvedor demonstrou maturidade ao criar uma migration dedicada a otimizações de banco após estabelecer a estrutura base.

8. **Testes com `ArgumentCaptor`** — evidencia conhecimento de técnicas intermediárias/avançadas de Mockito.

9. **`@Transactional(readOnly = true)`** em queries de leitura — boa prática para otimizar conexões e evitar dirty checking desnecessário.

10. **`spring.jpa.open-in-view=false`** em todos os profiles — elimina o anti-padrão "open session in view" que causa queries ocultas na camada de apresentação.

---

## 12. Problemas Encontrados por Severidade

### 🔴 Crítico

| # | Problema | Localização |
|---|----------|-------------|
| C1 | Senha do banco hardcoded com fallback no `application.properties` | `application.properties:5` |
| C2 | `SecurityUtils.verificarOwnership` compara `"ADMIN"` em vez de `"ROLE_ADMIN"` — admins nunca são reconhecidos | `SecurityUtils.java:19` |
| C3 | Testes com exceção e mensagem erradas passam silenciosamente (MateriaServiceTest, TarefaServiceTest) | `MateriaServiceTest.java:112`, `TarefaServiceTest.java:155` |

### 🟠 Alto

| # | Problema | Localização |
|---|----------|-------------|
| A1 | N+1 queries no `PerformanceAnalyzerService` (2 queries por tópico) | `PerformanceAnalyzerService.java:62-66` |
| A2 | POST/PUT/DELETE de tarefas sem verificação de ownership | `TarefaController.java` |
| A3 | POST /resultados sem verificação de ownership | `ResultadoController.java:28` |
| A4 | `ObjectMapper` instanciado por request (objeto pesado, deve ser singleton) | `RecommendationService.java:70` |
| A5 | Ausência total de testes para `RecommendationService` e `PerformanceAnalyzerService` | — |

### 🟡 Médio

| # | Problema | Localização |
|---|----------|-------------|
| M1 | RBAC comentado — qualquer aluno pode gerenciar matérias/tópicos/questões | `SecurityConfig.java:35` |
| M2 | Sem rate limiting na rota que chama a API da Anthropic | `RecomendacaoController.java` |
| M3 | `RestClient` não é Bean gerenciado — sem pool de conexões configurado | `AnthropicClient.java:22` |
| M4 | Sem timeout nas chamadas à API externa | `AnthropicClient.java` |
| M5 | Sem fallback para falha da IA | `RecommendationService.java:87` |
| M6 | `ex.printStackTrace()` em produção — deve usar logger estruturado | `GlobalExceptionHandler.java:39` |
| M7 | Ausência de unique constraints no banco para `materias.nome` e `topicos(nome, materia_id)` | V2, V3 migrations |
| M8 | Listagem de materias e tópicos sem paginação | `MateriaService.java`, `TopicoService.java` |

### 🟢 Baixo

| # | Problema | Localização |
|---|----------|-------------|
| B1 | Imports não utilizados em 4 arquivos | `TopicoController`, `MateriaController`, `MateriaService`, `UsuarioService` |
| B2 | Mensagens de erro inconsistentes (acentos, capitalização) | Múltiplos services |
| B3 | Typos nos nomes de migration (`crate` em vez de `create`) | V3, V4 |
| B4 | Comentários pedagógicos no código de produção | `TopicoService.java:52-53` |
| B5 | Rota duplicada: `POST /usuarios` e `POST /auth/registro` | `UsuarioController.java`, `AuthController.java` |
| B6 | `application.porperties.teste` com nome incorreto | `src/main/resources/` |
| B7 | Ausência de auditoria (`criado_em`, `atualizado_em`) em `materias` e `topicos` | V2, V3 migrations |

---

## 13. Prioridades de Melhoria

### Fase 1 — Correções imediatas (antes de qualquer deploy)

1. **C1:** Remover o fallback da senha do banco em `application.properties`. Deixar apenas `${DB_PASSWORD}` — sem valor padrão.
2. **C2:** Corrigir `SecurityUtils.verificarOwnership` para comparar `"ROLE_ADMIN"` em vez de `"ADMIN"`.
3. **C3:** Corrigir `MateriaServiceTest` para esperar `RecursoNaoEncontradoException` e `TarefaServiceTest` para usar a mensagem correta.
4. **A2/A3:** Adicionar `verificarOwnership` nos endpoints de mutação de tarefas e resultados.
5. **M6:** Substituir `ex.printStackTrace()` por `log.error("Erro interno", ex)` com `@Slf4j`.

### Fase 2 — Estabilidade e confiabilidade

6. **A1:** Substituir as 2 queries por tópico em `PerformanceAnalyzerService` por uma única query JPQL com `GROUP BY topico_id, status`.
7. **A4:** Transformar `ObjectMapper` em `@Bean` singleton injetado.
8. **M3:** Criar `@Bean RestClient` com timeout configurado e injetá-lo em `AnthropicClient`.
9. **M4:** Configurar timeout na `RestClient` (ex: 10s connect, 30s read).
10. **M5:** Implementar fallback: retornar `DadosRecomendacao` com mensagem padrão em caso de falha da IA.
11. **M7:** Criar migration V8 adicionando `UNIQUE(nome)` em materias e `UNIQUE(nome, materia_id)` em topicos.

### Fase 3 — Segurança e RBAC

12. **M1:** Ativar o controle de acesso por role: rotas de criação/edição de matérias, tópicos e questões devem exigir `ROLE_ADMIN`.
13. **M2:** Implementar rate limiting na rota `/recomendacao/**` (ex: 1 request por minuto por usuário via `Bucket4j` ou similar).

### Fase 4 — Qualidade e cobertura de testes

14. **A5:** Criar testes para `RecommendationService` (mockando `AIClient` e `PerformanceAnalyzerService`).
15. **A5:** Criar testes para `PerformanceAnalyzerService` com cenários de múltiplos tópicos e taxa de acerto.
16. Adicionar testes de controller para `TarefaController`, `ResultadoController` e `UsuarioController`.
17. Criar teste de integração para `AuthFilter` com token inválido/expirado.

### Fase 5 — Melhorias incrementais

18. **M8:** Adicionar paginação em `GET /materias` e `GET /topicos`.
19. **B1:** Remover imports não utilizados.
20. **B2:** Padronizar todas as mensagens de erro (sem acento vs. com acento, case consistente).
21. **B7:** Adicionar `criado_em` e `atualizado_em` em materias e topicos na próxima migration.

---

## 14. Avaliação Profissional

### Nível do desenvolvedor

**Júnior avançado / Pleno inicial.**

O projeto demonstra claramente que o desenvolvedor aprendeu e aplicou corretamente os fundamentos de desenvolvimento back-end com Spring Boot:
- Arquitetura em camadas sem desvios;
- Uso idiomático de Records, Lombok e a API moderna do Spring;
- Gerenciamento de migrations com Flyway;
- Implementação funcional de JWT + Spring Security;
- Conhecimento de Mockito em nível intermediário (ArgumentCaptor, mocking de interfaces).

O que ainda diferencia um sênior: identificação proativa dos bugs de segurança descritos acima, testes que verificam o comportamento real (não a exceção errada), e soluções de performance para N+1.

### Adequação para portfólio

**Muito adequado**, com ressalvas.

O projeto tem um diferencial concreto: **integração com IA para recomendações educacionais personalizadas**, o que é atual, relevante e demonstra curiosidade técnica. A estrutura de pacotes é limpa e profissional, os DTOs como Records mostram conhecimento de Java moderno, e a documentação Swagger está configurada.

Antes de compartilhar publicamente:
1. Remover a senha do banco do `application.properties`.
2. Corrigir os bugs de teste (C3) — um recrutador técnico que roda os testes e vê falhas leva a conclusões negativas.
3. Corrigir o bug de ADMIN (C2) — é facilmente detectável em code review.

### Impressão para recrutadores técnicos

**Positiva com pontos de atenção.**

Um revisor técnico vai perceber:
- ✅ Código limpo, organizado e com padrões bem definidos.
- ✅ Funcionalidade diferenciada (IA + vestibular).
- ✅ Cobertura de testes existente com técnicas não triviais.
- ✅ Flyway, separação de profiles (dev/prod), `open-in-view=false` — sinais de maturidade.
- ⚠️ Bugs nos testes e na verificação de role de admin — sinais de que o projeto não foi completamente revisado.
- ⚠️ Senha no código-fonte — sinal de alerta em segurança.

**Resumo:** projetos de portfólio são avaliados pelo que revelam sobre o raciocínio do desenvolvedor. Este projeto revela boa base técnica, curiosidade com IA e organização de código — atributos valiosos. As correções sugeridas nas fases 1 e 2 transformariam este em um portfólio de referência.

---

*Relatório gerado por análise estática de 80 arquivos — nenhuma linha de código foi modificada.*