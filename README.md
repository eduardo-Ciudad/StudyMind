# StudyMind 🧠

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-336791?style=for-the-badge&logo=postgresql&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-V1--V12-CC0200?style=for-the-badge&logo=flyway&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-JWT-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![Anthropic](https://img.shields.io/badge/Anthropic-Claude_Haiku-purple?style=for-the-badge)
![JUnit](https://img.shields.io/badge/Tests-37_Passing-success?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Active_Development-yellow?style=for-the-badge)

> AI-powered study planning platform that creates personalized 12-week study roadmaps through a conversational onboarding experience.

---

## 📌 Overview

StudyMind is a REST API built with Java 17 and Spring Boot that helps students prepare for vestibular and other exams.

The main differentiator is an **AI-driven conversational onboarding flow**. Instead of manually registering subjects and tasks, the student chats with an AI assistant that gathers information such as:

- Target exam (ENEM, FUVEST, UNICAMP, etc.)
- Exam date
- Subjects to prioritize
- Current level in each subject
- Daily study hours available
- Strengths and weaknesses

Based on this conversation, the system generates a **personalized 12-week study plan**, parses it automatically, and populates the database with subjects, topics, and weekly tasks — no manual setup required.

---

## ✨ Key Features

### 🤖 AI Conversational Onboarding
- Persistent chat history across sessions
- Automatic information extraction through natural dialogue
- Detection of onboarding completion via `ONBOARDING_COMPLETO` marker
- Structured JSON study plan generation via Claude Haiku
- **Auto-population** of subjects, topics and tasks from the generated plan

### 🔄 Study Plan Review Cycle
- User can request a review at any time
- AI automatically prompts a review every 3–4 weeks
- New versioned plan generated without repeating previous content
- Full plan history preserved in the database

### 📚 Study Management
- Subject (`Materia`) and Topic (`Topico`) management per user
- Question (`Questao`) registration with multiple types
- Result (`Resultado`) tracking per student
- Task (`Tarefa`) generation with priorities and deadlines

### 📈 Performance Analytics (AI-powered)
- Accuracy rate analysis by subject and topic
- Identification of weakest topics
- Personalized study recommendations
- Motivational insights and practical tips

### 🔐 Security
- JWT authentication (stateless, 2h expiry)
- BCrypt password hashing
- Ownership verification — users can only access their own data
- CORS configured for frontend integration
- Role-based authorization (ADMIN / ALUNO) — roles defined, endpoint-level restrictions planned (see Roadmap)
### 🧪 Testing
- 37 automated test scenarios
- Unit tests with Mockito and ArgumentCaptor
- Integration tests with `@SpringBootTest` and MockMvc

---

## 🏗️ Architecture

```text
Controller → Service → Repository
```

**Key architectural decisions:**

- DTOs as Java Records (separate `input/` and `output/` packages)
- Global exception handling (`@RestControllerAdvice`)
- Soft delete pattern across all entities
- Pagination on high-volume endpoints
- Dependency inversion for AI provider (swap Claude for GPT without changing business logic)
- Per-user data isolation — subjects and topics are scoped to each user

---

## 🧠 AI Integration

StudyMind uses an abstraction layer for AI:

```text
AIClient (interface)
   └── AnthropicClient (RestClient → Anthropic API)
```

**AI-powered services:**

| Service | Responsibility |
|---|---|
| `OnboardingService` | Conversational data collection + roadmap generation |
| `PlanoEstudoParser` | Parses AI-generated JSON and populates the database |
| `PerformanceAnalyzerService` | Accuracy analysis by topic and subject |
| `RecommendationService` | Personalized study recommendations |

---

## 💬 Conversational Onboarding Flow

```text
User Registration
      ↓
Chat with AI (persistent history)
      ↓
AI collects: exam, date, subjects, level, hours, strengths/weaknesses
      ↓
AI emits: ONBOARDING_COMPLETO + structured JSON roadmap (12 weeks)
      ↓
System saves study plan in plano_estudo (versao=1, ativo=true)
      ↓
PlanoEstudoParser auto-creates subjects, topics and weekly tasks
      ↓
[Review cycle] User triggers review or AI prompts every 3-4 weeks
      ↓
AI generates new versioned plan (versao++) without repeating content
      ↓
Previous plan preserved in history for AI context
```

---

## 🗂️ Domain Model

```text
Usuario
 ├── ChatMensagem       ← onboarding/review conversation history
 ├── PlanoEstudo        ← AI-generated study plan (versioned JSON)
 ├── Resultado          ← question answers and scores
 ├── Tarefa             ← study tasks (auto-generated from plan)
 ├── Materia            ← subjects (per user, auto-created from plan)
 └── Topico             ← topics (per user, auto-created from plan)
      └── Questao       ← questions linked to topics
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5 |
| Database | PostgreSQL 17 |
| ORM | Spring Data JPA / Hibernate |
| Migrations | Flyway (V1–V12) |
| Security | Spring Security + Auth0 JWT |
| AI Integration | Anthropic Claude Haiku via RestClient |
| Validation | Jakarta Bean Validation |
| Testing | JUnit 5, Mockito, MockMvc |
| Build | Maven |
| Utilities | Lombok |

---

## 📁 Project Structure

```text
src/main/java/com/eduardo/studymind/
├── controller/          ← HTTP layer (12 controllers)
├── domain/              ← JPA entities + repositories (per aggregate)
│   ├── chat/
│   ├── materia/
│   ├── plano/
│   ├── questao/
│   ├── resultado/
│   ├── tarefa/
│   ├── topico/
│   └── usuario/
├── dto/
│   ├── input/           ← Incoming data (Bean Validation)
│   └── output/          ← Response data (Java Records)
├── exception/           ← Custom exceptions + global handler
├── infra/
│   ├── ia/              ← AIClient interface + AnthropicClient
│   └── security/        ← AuthFilter, JwtService, SecurityConfig, SecurityUtils
└── service/             ← Business logic (9 services)
    └── parser/          ← PlanoEstudoParser

src/main/resources/
├── db/migration/        ← Flyway scripts V1–V12
├── application.properties
├── application-dev.properties
└── application-prod.properties
```

---

## 🔌 API Endpoints

### 🔐 Authentication
| Method | Endpoint | Description |
|---|---|---|
| POST | `/auth/registro` | Register new user |
| POST | `/auth/login` | Login and receive JWT |

### 💬 Onboarding
| Method | Endpoint | Description |
|---|---|---|
| GET | `/onboarding/status/{usuarioId}` | Check onboarding completion |
| POST | `/onboarding/mensagem/{usuarioId}` | Send message to AI onboarding chat |
| POST | `/onboarding/review/{usuarioId}` | Send message to AI review chat |

### 📋 Study Plan
| Method | Endpoint | Description |
|---|---|---|
| GET | `/plano-estudo/usuario/{usuarioId}` | Get current active study plan |

### 📊 AI Insights
| Method | Endpoint | Description |
|---|---|---|
| GET | `/dashboard/usuario/{usuarioId}` | Overall performance dashboard |
| GET | `/diagnostico/usuario/{usuarioId}` | Detailed topic-level diagnosis |
| GET | `/recomendacao/usuario/{usuarioId}` | AI-powered study recommendations |

### 📚 Study Resources
| Method | Endpoint | Description |
|---|---|---|
| GET/POST/PUT/DELETE | `/materias` | Subject management |
| GET/POST/PUT/DELETE | `/topicos` | Topic management |
| GET/POST/PUT/DELETE | `/questoes` | Question management |
| GET/POST | `/resultados` | Answer tracking |
| GET/POST/PUT/DELETE | `/tarefas` | Task management |

> All endpoints except `/auth/**` require a valid JWT token in the `Authorization: Bearer <token>` header.

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- PostgreSQL 17+
- Maven 3.9+
- Anthropic API key ([console.anthropic.com](https://console.anthropic.com))

### Clone the Repository

```bash
git clone https://github.com/educiudad/studymind.git
cd studymind
```

### Create the Database

```sql
CREATE DATABASE studymind;
```

### Configure Environment Variables

Copy `.env.example` to `.env` and fill in your values:

```bash
DB_URL=jdbc:postgresql://localhost:5432/studymind
DB_USERNAME=postgres
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret_key_min_32_chars
ANTHROPIC_API_KEY=your_anthropic_api_key
```

If using IntelliJ IDEA, configure these under `Run > Edit Configurations > Environment Variables`.

### Run the Application

```bash
./mvnw spring-boot:run
```

Flyway will automatically apply all 12 migrations on startup.

### Run Tests

```bash
./mvnw test
```

---

## 🗺️ Roadmap

### ✅ Completed
- [x] Core domain model (8 entities)
- [x] Full CRUD REST APIs
- [x] JWT authentication + BCrypt
- [x] Global exception handling
- [x] Database indexing (V7)
- [x] Per-user data isolation for subjects and topics (V12)
- [x] AI integration with Anthropic Claude Haiku
- [x] Performance analysis engine
- [x] Personalized AI recommendations
- [x] Conversational onboarding (12-week plan)
- [x] Auto-population of subjects, topics and tasks from AI plan
- [x] Study plan versioning and review cycle
- [x] 37 automated tests
- [x] Swagger/OpenAPI documentation

### 🔄 In Progress
- [ ] Tests for `OnboardingService` and `PlanoEstudoParser`
- [ ] Frontend prototype connected to the real API

### 🔮 Future
- [ ] Docker + docker-compose
- [ ] CI/CD pipeline
- [ ] Refresh token support
- [ ] Notifications and reminders
- [ ] Spaced repetition scheduler
- [ ] Multi-provider AI support (OpenAI, Gemini)

---

## 🎯 Project Goal

The ultimate goal of StudyMind is for a student to simply answer a few questions in a natural chat and receive a **complete personalized study structure generated automatically by AI** — subjects, topics, and 12 weeks of tasks created instantly, with a review cycle that adapts the plan as the student progresses.

---

## 👨‍💻 Author

**Eduardo Ciudad Figueredo** — Backend Java Developer

[![GitHub](https://img.shields.io/badge/GitHub-eduardo-Ciudad-181717?style=flat&logo=github)](https://github.com/eduardo-Ciudad)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Eduardo_Ciudad-0A66C2?style=flat&logo=linkedin)](https://www.linkedin.com/in/eduardo-ciudad-figueredo/)

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

