# Finance API

> 🇧🇷 Português | 🇺🇸 [English](#english-version)

API REST para gerenciamento de finanças pessoais com autenticação JWT, categorias e relatórios financeiros.

## Tecnologias

- Java 21
- Spring Boot 4.1
- Spring Security + JWT (JJWT 0.12.6)
- Spring Data JPA + Hibernate
- PostgreSQL 18
- Docker + Docker Compose
- Lombok
- Maven

## Arquitetura

O projeto segue a arquitetura em camadas:

```
controller/   → recebe requisições HTTP
service/      → lógica de negócio
repository/   → acesso ao banco de dados
model/        → entidades JPA
dto/          → objetos de transferência de dados
config/       → configurações de segurança
```

## Endpoints

### Autenticação
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | /auth/register | Cadastrar usuário |
| POST | /auth/login | Login e geração de token JWT |

### Transações (requer token)
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | /transactions | Criar transação |
| GET | /transactions | Listar transações do usuário |
| GET | /transactions/summary | Resumo financeiro |
| GET | /transactions/by-category?categoryId={id} | Filtrar por categoria |

### Categorias (requer token)
| Método | Rota | Descrição |
|--------|------|-----------|
| POST | /categories | Criar categoria |
| GET | /categories | Listar categorias do usuário |

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
```

4. Rode a aplicação:
```bash
./mvnw spring-boot:run
```

## Como rodar com Docker

1. Configure o arquivo `.env` na raiz do projeto:
```
DB_PASSWORD=sua_senha
JWT_SECRET=sua_chave_secreta_minimo_32_caracteres
```

2. Suba os containers:
```bash
docker-compose up --build
```

A API estará disponível em `http://localhost:8080`

---

## English Version

REST API for personal finance management with JWT authentication, categories and financial reports.

## Technologies

- Java 21
- Spring Boot 4.1
- Spring Security + JWT (JJWT 0.12.6)
- Spring Data JPA + Hibernate
- PostgreSQL 18
- Docker + Docker Compose
- Lombok
- Maven

## Architecture

The project follows a layered architecture:

```
controller/   → handles HTTP requests
service/      → business logic
repository/   → database access
model/        → JPA entities
dto/          → data transfer objects
config/       → security configuration
```

## Endpoints

### Authentication
| Method | Route | Description |
|--------|-------|-------------|
| POST | /auth/register | Register user |
| POST | /auth/login | Login and JWT token generation |

### Transactions (requires token)
| Method | Route | Description |
|--------|-------|-------------|
| POST | /transactions | Create transaction |
| GET | /transactions | List user transactions |
| GET | /transactions/summary | Financial summary |
| GET | /transactions/by-category?categoryId={id} | Filter by category |

### Categories (requires token)
| Method | Route | Description |
|--------|-------|-------------|
| POST | /categories | Create category |
| GET | /categories | List user categories |

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
```

4. Run the application:
```bash
./mvnw spring-boot:run
```

## Running with Docker

1. Create a `.env` file in the project root:
```
DB_PASSWORD=your_password
JWT_SECRET=your_secret_key_minimum_32_characters
```

2. Start the containers:
```bash
docker-compose up --build
```

The API will be available at `http://localhost:8080`