# StudyMind

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.14-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-336791?style=for-the-badge&logo=postgresql&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-Migration-CC0200?style=for-the-badge&logo=flyway&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Status](https://img.shields.io/badge/Status-In_Development-yellow?style=for-the-badge)

> A smart study roadmap system designed to help students prepare for the vestibular. Built with Spring Boot, AI integration, and a focus on personalized learning paths.

---

## About

StudyMind is a backend REST API that allows students to organize their study roadmap across subjects and topics, track their performance through exercises, and receive AI-generated tasks based on their results.

The project is designed as both a real-world learning tool for vestibular students and a portfolio piece demonstrating backend development with Java and Spring Boot.

---

## Features

- Subject and topic management
- Exercise (questão) registration per topic
- Answer tracking and performance results per user
- AI-generated study tasks based on user performance
- JWT authentication and role-based access control
- Soft delete pattern for users, subjects, topics and questions

---

## Domain Model

```
Materia  →  has many  →  Topico
Topico   →  has many  →  Questao
Questao  →  generates →  Resultado  (user attempt)
Resultado → feeds     →  Tarefa     (via AI or business rule)
Usuario  →  has many  →  Tarefa
```

---

## Tech Stack

| Layer          | Technology                        |
|----------------|-----------------------------------|
| Language       | Java 17                           |
| Framework      | Spring Boot 3.5.14                |
| Database       | PostgreSQL 18                     |
| ORM            | Spring Data JPA / Hibernate 6     |
| Migrations     | Flyway 11                         |
| Security       | Spring Security + JWT             |
| Validation     | Bean Validation (Jakarta)         |
| Build          | Maven                             |
| Boilerplate    | Lombok                            |

---

## Project Structure

```
src/main/java/com/eduardo/studymind/
├── config/
├── controller/
├── domain/
│   ├── usuario/
│   ├── materia/
│   ├── topico/
│   ├── questao/
│   ├── resultado/
│   └── tarefa/
├── dto/
│   ├── input/
│   └── output/
├── exception/
├── infra/
│   ├── ai/
│   └── security/
└── service/
```

---

## Getting Started

### Prerequisites

- Java 17+
- PostgreSQL running locally
- Maven 3.9+

### Setup

1. Clone the repository:
```bash
git clone https://github.com/educiudad/studymind.git
cd studymind
```

2. Create the database:
```sql
CREATE DATABASE studymind;
```

3. Copy the configuration template and fill in your credentials:
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

4. Run the application:
```bash
./mvnw spring-boot:run
```

Flyway will automatically apply all migrations on startup.

---

## API Endpoints

> Documentation in progress. Endpoints will be listed here as each module is completed.

---

## Roadmap

- [x] Project setup and configuration
- [x] Core domain model (entities + migrations)
- [ ] DTOs (input and output)
- [ ] Service layer
- [ ] REST controllers
- [ ] JWT authentication
- [ ] AI integration for task generation
- [ ] Frontend (HTML/CSS/Vanilla JS)

---

## Author

**Eduardo** — Backend Java Developer
[GitHub](https://github.com/educiudad) · [LinkedIn](https://www.linkedin.com/in/seu-perfil)

---

## License

This project is for educational and portfolio purposes.