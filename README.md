# Finance API

> 🇧🇷 Português | 🇺🇸 [English](#english-version)

API REST para gerenciamento de finanças pessoais com autenticação JWT via cookie httpOnly, rate limiting, isolamento de dados por usuário e acesso restrito por convite.

**Frontend:** [rajo-finance.vercel.app](https://rajo-finance.vercel.app) | **Repositório frontend:** [finance-frontend](https://github.com/Rangeldev73/finance-frontend)

---

## Tecnologias

- Java 21
- Spring Boot 4.1.0
- Spring Security + JWT (JJWT 0.12.6)
- Spring Data JPA + Hibernate
- PostgreSQL 18
- Bucket4j (rate limiting)
- Docker (multi-stage build)
- Lombok
- Maven

---

## Arquitetura

O projeto segue a arquitetura em camadas:

```
controller/   → recebe requisições HTTP
service/      → lógica de negócio
repository/   → acesso ao banco de dados
model/        → entidades JPA
dto/          → objetos de transferência de dados
config/       → configurações de segurança e filtros
```

---

## Endpoints

### Autenticação
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | /auth/register | Cadastrar usuário (requer invite code) |
| POST | /auth/login | Login — define cookie JWT httpOnly |
| GET | /auth/me | Verificar sessão ativa |
| POST | /auth/logout | Logout — invalida o cookie |

### Transações (requer autenticação)
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | /transactions | Criar transação |
| GET | /transactions | Listar transações do usuário |
| GET | /transactions/paged?page=0&size=5 | Listar com paginação |
| GET | /transactions/filter?startDate=&endDate=&categoryId= | Filtrar por período e categoria |
| GET | /transactions/summary | Resumo financeiro (saldo, receitas, despesas) |
| PUT | /transactions/{id} | Editar transação |
| DELETE | /transactions/{id} | Excluir transação |

### Categorias (requer autenticação)
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | /categories | Criar categoria |
| GET | /categories | Listar categorias do usuário |
| PUT | /categories/{id} | Editar categoria |
| DELETE | /categories/{id} | Excluir categoria |

### Metas (requer autenticação)
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | /goals | Criar meta |
| GET | /goals | Listar metas do usuário |
| PUT | /goals/{id} | Editar meta |
| DELETE | /goals/{id} | Excluir meta |

---

## Decisões de Design

**Acesso restrito por invite code**
Não há cadastro público. Novos usuários precisam de um invite code definido como variável de ambiente no servidor. Isso garante que apenas pessoas autorizadas acessem o sistema.

**JWT via cookie httpOnly**
O token JWT é entregue em um cookie httpOnly com `SameSite=None; Secure`, em vez de retornado no body para armazenamento no frontend. Isso protege contra ataques XSS — o token não é acessível por JavaScript.

**Isolamento de dados por usuário**
Todas as queries filtram pelo usuário autenticado. Um usuário nunca consegue acessar ou modificar dados de outro usuário, mesmo conhecendo o ID do recurso.

**Rate limiting com Bucket4j**
- `/auth/**` (login e registro): 10 requisições por minuto
- Demais rotas: 60 requisições por minuto

**Docker multi-stage build**
A imagem de produção usa `eclipse-temurin:21-jre-alpine` como runtime — apenas o JRE, sem o JDK completo. Isso reduz o tamanho da imagem final.

---

## Como rodar localmente

### Pré-requisitos
- Java 21
- PostgreSQL
- Maven

### Configuração

1. Clone o repositório:
```bash
git clone https://github.com/Rangeldev73/finance-api.git
cd finance-api
```

2. Crie o banco de dados:
```sql
CREATE DATABASE finance_db;
```

3. Configure as variáveis de ambiente:
```
DB_USER=seu_usuario
DB_PASSWORD=sua_senha
JWT_SECRET=sua_chave_secreta_minimo_32_caracteres
INVITE_CODE=seu_codigo_de_convite
```

4. Rode a aplicação:
```bash
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`

---

## Como rodar com Docker

1. Configure o arquivo `.env` na raiz do projeto:
```
DB_URL=jdbc:postgresql://localhost:5432/finance_db
DB_USER=seu_usuario
DB_PASSWORD=sua_senha
JWT_SECRET=sua_chave_secreta_minimo_32_caracteres
INVITE_CODE=seu_codigo_de_convite
```

2. Suba os containers:
```bash
docker-compose up --build
```

A API estará disponível em `http://localhost:8080`

---

## Deploy

| Serviço | Plataforma        |
|---------|-------------------|
| Backend | Railway           |
| Banco de dados | Supabase (PostgreSQL) |
| Frontend | Vercel            |

### Variáveis de ambiente em produção

```
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:postgresql://<host>.neon.tech/<db>?sslmode=require
DB_USER=neondb_owner
DB_PASSWORD=sua_senha
JWT_SECRET=sua_chave_secreta
INVITE_CODE=seu_codigo_de_convite
```

---

## English Version

REST API for personal finance management with httpOnly cookie JWT authentication, rate limiting, per-user data isolation, and invite-only access.

**Frontend:** [rajo-finance.vercel.app](https://rajo-finance.vercel.app) | **Frontend repo:** [finance-frontend](https://github.com/Rangeldev73/finance-frontend)

---

## Technologies

- Java 21
- Spring Boot 4.1.0
- Spring Security + JWT (JJWT 0.12.6)
- Spring Data JPA + Hibernate
- PostgreSQL 18
- Bucket4j (rate limiting)
- Docker (multi-stage build)
- Lombok
- Maven

---

## Architecture

The project follows a layered architecture:

```
controller/   → handles HTTP requests
service/      → business logic
repository/   → database access
model/        → JPA entities
dto/          → data transfer objects
config/       → security configuration and filters
```

---

## Endpoints

### Authentication
| Method | Route | Description |
|--------|-------|-------------|
| POST | /auth/register | Register user (requires invite code) |
| POST | /auth/login | Login — sets httpOnly JWT cookie |
| GET | /auth/me | Check active session |
| POST | /auth/logout | Logout — clears the cookie |

### Transactions (requires authentication)
| Method | Route | Description |
|--------|-------|-------------|
| POST | /transactions | Create transaction |
| GET | /transactions | List user transactions |
| GET | /transactions/paged?page=0&size=5 | List with pagination |
| GET | /transactions/filter?startDate=&endDate=&categoryId= | Filter by period and category |
| GET | /transactions/summary | Financial summary (balance, income, expenses) |
| PUT | /transactions/{id} | Update transaction |
| DELETE | /transactions/{id} | Delete transaction |

### Categories (requires authentication)
| Method | Route | Description |
|--------|-------|-------------|
| POST | /categories | Create category |
| GET | /categories | List user categories |
| PUT | /categories/{id} | Update category |
| DELETE | /categories/{id} | Delete category |

### Goals (requires authentication)
| Method | Route | Description |
|--------|-------|-------------|
| POST | /goals | Create goal |
| GET | /goals | List user goals |
| PUT | /goals/{id} | Update goal |
| DELETE | /goals/{id} | Delete goal |

---

## Design Decisions

**Invite-only access**
There is no public registration. New users need an invite code defined as a server environment variable. This ensures only authorized people can access the system.

**JWT via httpOnly cookie**
The JWT token is delivered in an httpOnly cookie with `SameSite=None; Secure`, instead of being returned in the response body for frontend storage. This protects against XSS attacks — the token is not accessible by JavaScript.

**Per-user data isolation**
All queries filter by the authenticated user. A user can never access or modify another user's data, even knowing the resource ID.

**Rate limiting with Bucket4j**
- `/auth/**` (login and register): 10 requests per minute
- All other routes: 60 requests per minute

**Docker multi-stage build**
The production image uses `eclipse-temurin:21-jre-alpine` as runtime — only the JRE, not the full JDK. This reduces the final image size.

---

## Running locally

### Prerequisites
- Java 21
- PostgreSQL
- Maven

### Setup

1. Clone the repository:
```bash
git clone https://github.com/Rangeldev73/finance-api.git
cd finance-api
```

2. Create the database:
```sql
CREATE DATABASE finance_db;
```

3. Set environment variables:
```
DB_USER=your_user
DB_PASSWORD=your_password
JWT_SECRET=your_secret_key_minimum_32_characters
INVITE_CODE=your_invite_code
```

4. Run the application:
```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`

---

## Running with Docker

1. Create a `.env` file in the project root:
```
DB_URL=jdbc:postgresql://localhost:5432/finance_db
DB_USER=your_user
DB_PASSWORD=your_password
JWT_SECRET=your_secret_key_minimum_32_characters
INVITE_CODE=your_invite_code
```

2. Start the containers:
```bash
docker-compose up --build
```

The API will be available at `http://localhost:8080`

---

## Deploy

| Service | Platform              |
|---------|-----------------------|
| Backend | Railway               |
| Database | Supabase (PostgreSQL) |
| Frontend | Vercel                |

### Production environment variables

```
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:postgresql://<host>.neon.tech/<db>?sslmode=require
DB_USER=neondb_owner
DB_PASSWORD=your_password
JWT_SECRET=your_secret_key
INVITE_CODE=your_invite_code
```
