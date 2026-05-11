# Microserviço - Gerenciamento de Usuários e Pets

Serviço de domínio responsável pela lógica de negócio de Usuários e Pets, com isolamento de dados por usuário via headers HTTP.

## 🎯 Responsabilidades

- ✅ Gerenciamento de Usuários (CRUD)
- ✅ Gerenciamento de Pets (CRUD)
- ✅ Isolamento de dados por usuário (X-User-Id header)
- ✅ Persistência em MySQL
- ✅ Logging estruturado com Correlation ID
- ✅ Health check com indicadores customizados
- ❌ NÃO faz autenticação (delegada ao Gateway)
- ❌ NÃO valida JWT (confia nos headers do Gateway)

## 📡 Endpoints

### Usuários

```
GET    /api/v1/users              Listar usuários (requer autenticação)
GET    /api/v1/users/{id}         Obter usuário por ID
POST   /api/v1/users              Criar novo usuário
PUT    /api/v1/users/{id}         Atualizar usuário
DELETE /api/v1/users/{id}         Deletar usuário
```

### Pets

```
GET    /api/v1/pets               Listar pets do usuário (filtrado por X-User-Id)
GET    /api/v1/pets/{id}          Obter pet por ID (com validação de posse)
POST   /api/v1/pets               Criar novo pet
PUT    /api/v1/pets/{id}          Atualizar pet (com validação de posse)
DELETE /api/v1/pets/{id}          Deletar pet (com validação de posse)
```

### Health & Métricas

```
GET    /actuator/health           Status geral e database health
GET    /actuator/info             Informações da aplicação
GET    /actuator/metrics          Métricas de performance
```

## 🔐 Isolamento de Dados

### Fluxo Seguro

1. **Cliente** envia requisição com JWT:
   ```
   GET /api/v1/pets
   Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
   ```

2. **Gateway** valida JWT e adiciona headers:
   ```
   GET /api/v1/pets
   X-User-Id: 550e8400-e29b-41d4-a716-446655440000
   X-User-Email: user@example.com
   X-Request-Id: f47ac10b-58cc-4372-a567-0e02b2c3d479
   ```

3. **Microserviço** recebe requisição:
   - `RequestContextFilter` lê headers e adiciona ao MDC
   - `PetController` lê `X-User-Id` do header
   - `PetService` filtra pets por userId
   - `PetRepository` executa: `findByUserIdAndId(userId, petId)`

4. **Resultado** - Apenas pets do usuário são retornados

### Validação em Múltiplos Níveis

```java
// Nível 1: Header
String userId = request.getHeader("X-User-Id");
if (userId == null) {
    throw new UnauthorizedAccessException("X-User-Id header missing");
}

// Nível 2: Repository
Optional<Pet> pet = petRepository.findByIdAndUserId(id, userId);
if (pet.isEmpty()) {
    throw new PetNotFoundException("Pet not found or access denied");
}

// Nível 3: Service
petService.validateOwnership(pet, userId);
```

## ⚙️ Configuração

### Variáveis de Ambiente (`.env`)

```env
# Aplicação
SPRING_APPLICATION_NAME=help-pet-service
SERVER_PORT=8081

# Database
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/helppet?createDatabaseIfNotExist=true&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=

# JPA/Hibernate
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false
SPRING_JPA_PROPERTIES_HIBERNATE_FORMAT_SQL=true

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:4200

# Logging
LOGGING_LEVEL_COM_HELPPET=DEBUG
LOGGING_LEVEL_ROOT=INFO
```

**Notas:**
- Microserviço NÃO precisa de `JWT_SECRET` (confia no Gateway)
- Database é criada automaticamente se não existir
- `ddl-auto=update`: Altera schema automaticamente (use `validate` em produção)

### application.properties

```properties
# Datasource
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA
spring.jpa.hibernate.ddl-auto=${SPRING_JPA_HIBERNATE_DDL_AUTO}
spring.jpa.show-sql=${SPRING_JPA_SHOW_SQL}
spring.jpa.properties.hibernate.format_sql=${SPRING_JPA_PROPERTIES_HIBERNATE_FORMAT_SQL}
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Actuator
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always

# Logging
logging.level.com.helppet=${LOGGING_LEVEL_COM_HELPPET}
logging.level.root=${LOGGING_LEVEL_ROOT}
logging.file.name=logs/help-pet-service.log
logging.file.max-size=10MB
logging.file.max-history=30
```

## 🚀 Como Rodar

### Pré-requisitos

- Java 25+
- Maven 3.8+
- MySQL 8.0+ (local ou Docker)

### 1. Iniciar MySQL (com Docker)

```bash
docker run --name mysql-helppet \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=helppet \
  -p 3306:3306 \
  -d mysql:8.0
```

**Verificar:**
```bash
docker ps | grep mysql-helppet
```

### 2. Configurar Ambiente

```bash
# Copiar .env
cp .env.example .env

# Editar se necessário
vim .env
```

### 3. Iniciar o Microserviço

```bash
# Desenvolvimento
mvn clean spring-boot:run

# Ou, construir e rodar JAR
mvn clean package
java -jar target/help-pet-service-1.0.0.jar
```

**Saída esperada:**
```
2025-05-04 14:35:00.123 INFO  c.h.HelpPetApplication - Starting HelpPetApplication...
2025-05-04 14:35:05.456 INFO  c.h.HelpPetApplication - Started HelpPetApplication in 5.333 seconds
2025-05-04 14:35:05.789 INFO  c.h.config.WebConfig - CORS configured for origins: http://localhost:3000, http://localhost:4200
2025-05-04 14:35:05.890 INFO  c.h.HelpPetApplication - Embedded Tomcat started on port 8081
```

### 4. Verificar Saúde

```bash
curl http://localhost:8081/actuator/health
```

**Resposta esperada:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "MySQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {"status": "UP"},
    "livenessState": {"status": "UP"},
    "readinessState": {"status": "UP"}
  }
}
```

## 📊 Logging

### Estrutura de Logs

Todos os logs incluem Correlation ID e contexto de usuário:

```
2025-05-04 14:37:15.123 [http-nio-8081-exec-1] INFO  c.h.controller.PetController - Retrieving pets for user: 550e8400
2025-05-04 14:37:15.234 [http-nio-8081-exec-1] DEBUG c.h.service.PetService - Querying pets with userId: 550e8400
2025-05-04 14:37:15.345 [http-nio-8081-exec-1] INFO  c.h.repository.PetRepository - Found 3 pets
```

### Contexto MDC (Mapped Diagnostic Context)

```
X-Request-Id: f47ac10b-58cc-4372-a567-0e02b2c3d479
X-User-Id: 550e8400-e29b-41d4-a716-446655440000
X-User-Email: user@example.com
```

### Arquivo de Log

```
logs/help-pet-service.log
```

**Rotação de Logs:**
- Tamanho máximo: 10 MB
- Retenção: 30 dias
- Compressão: Automática

## 🏗️ Estrutura de Pacotes

```
com.helppet/
├── HelpPetApplication.java              Main class
│
├── config/
│   └── WebConfig.java                   Configuração CORS e Web
│
├── controller/
│   ├── UserController.java              REST endpoints para Users
│   └── PetController.java               REST endpoints para Pets
│
├── service/
│   ├── UserService.java                 Lógica de usuários
│   ├── PetService.java                  Lógica de pets
│   └── RequestContext.java              Contexto de requisição (X-User-Id)
│
├── repository/
│   ├── UserRepository.java              JPA repository para Users
│   └── PetRepository.java               JPA repository para Pets
│
├── entity/
│   ├── User.java                        Entidade JPA de Usuário
│   └── Pet.java                         Entidade JPA de Pet
│
├── dto/
│   ├── request/                         DTOs de requisição
│   │   ├── CreateUserRequest.java
│   │   └── CreatePetRequest.java
│   └── response/                        DTOs de resposta
│       ├── UserResponse.java
│       └── PetResponse.java
│
├── exception/
│   ├── GlobalExceptionHandler.java      Tratamento centralizado de exceções
│   ├── ResourceNotFoundException.java   Quando recurso não existe
│   └── UnauthorizedAccessException.java Quando acesso negado
│
├── filter/
│   └── RequestContextFilter.java        Adiciona headers ao MDC
│
├── enums/
│   ├── PetType.java                     Tipos de pet (DOG, CAT, etc)
│   └── PetSize.java                     Tamanhos (SMALL, MEDIUM, LARGE)
│
└── health/
    └── DatabaseHealthIndicator.java     Custom health check para BD
```

## 🗄️ Schema do Banco de Dados

### Tabela: users

```sql
CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  external_id VARCHAR(36) UNIQUE NOT NULL,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  cpf VARCHAR(14) UNIQUE NOT NULL,
  phone VARCHAR(20) UNIQUE NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_external_id (external_id),
  INDEX idx_email (email)
);
```

### Tabela: pets

```sql
CREATE TABLE pets (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  name VARCHAR(255) NOT NULL,
  type VARCHAR(50) NOT NULL,
  size VARCHAR(50) NOT NULL,
  breed VARCHAR(255),
  color VARCHAR(100),
  date_of_birth DATE,
  description TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  INDEX idx_user_id (user_id),
  INDEX idx_user_id_id (user_id, id)
);
```

## 🧪 Exemplos de Uso

### Pré-requisito: Obter Token do Gateway

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"admin123"}' | jq -r '.accessToken')

echo "Token: $TOKEN"
```

### 1. Criar Usuário

```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@example.com",
    "cpf": "12345678901",
    "phone": "11987654321"
  }' | jq
```

### 2. Listar Usuários

```bash
curl http://localhost:8080/api/v1/users \
  -H "Authorization: Bearer $TOKEN" | jq
```

### 3. Criar Pet

```bash
curl -X POST http://localhost:8080/api/v1/pets \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Rex",
    "type": "DOG",
    "size": "LARGE",
    "breed": "Labrador",
    "color": "Amarelo",
    "dateOfBirth": "2020-03-15",
    "description": "Cachorro muito dócil"
  }' | jq
```

### 4. Listar Pets do Usuário

```bash
curl http://localhost:8080/api/v1/pets \
  -H "Authorization: Bearer $TOKEN" | jq
```

### 5. Obter Pet Específico

```bash
curl http://localhost:8080/api/v1/pets/1 \
  -H "Authorization: Bearer $TOKEN" | jq
```

### 6. Atualizar Pet

```bash
curl -X PUT http://localhost:8080/api/v1/pets/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Rex Updated",
    "color": "Amarelo Claro"
  }' | jq
```

### 7. Deletar Pet

```bash
curl -X DELETE http://localhost:8080/api/v1/pets/1 \
  -H "Authorization: Bearer $TOKEN" | jq
```

## 🔒 Segurança

### Validação de Acesso

O microserviço **confia COMPLETAMENTE** nos headers enviados pelo Gateway:

- **X-User-Id**: ID do usuário autenticado
- **X-User-Email**: Email do usuário autenticado
- **X-Request-Id**: ID único para correlação de logs

**Se alguém conseguir chamar o microserviço diretamente (sem passar pelo Gateway), pode simular qualquer usuário.**

### Proteção em Produção

1. **Rede Privada:** Microserviço deve estar em rede privada (VPC)
2. **Firewall:** Apenas Gateway pode acessar
3. **Validação:** Nunca remover validações de X-User-Id

```java
// OBRIGATÓRIO: Validar header em cada requisição
String userId = request.getHeader("X-User-Id");
if (userId == null || userId.isEmpty()) {
    throw new UnauthorizedAccessException("Invalid request: missing X-User-Id");
}
```

## 📈 Health Check

### Endpoint de Health

```bash
curl http://localhost:8081/actuator/health
```

**Resposta indicando banco OK:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "MySQL",
        "result": 1
      }
    }
  }
}
```

**Resposta indicando banco DOWN:**
```json
{
  "status": "DOWN",
  "components": {
    "db": {
      "status": "DOWN",
      "details": {
        "error": "java.sql.SQLException: Connection refused"
      }
    }
  }
}
```

## 🛠️ Desenvolvimento

### Build

```bash
mvn clean package
```

### Build com Testes

```bash
mvn clean verify
```

### Executar Testes

```bash
mvn test
```

### Executar Testes de Integração

```bash
mvn verify -DskipUnitTests
```

## 📦 Dependências Principais

| Dependência | Versão | Propósito |
|---|---|---|
| Spring Boot | 4.0.2 | Framework base |
| Spring Data JPA | 4.0.2 | ORM e persistência |
| Spring Web | 4.0.2 | REST controllers |
| MySQL Connector | 9.0.0 | Driver de database |
| Hibernate | 6.4.0 | ORM |
| Lombok | 1.18.30 | Reduzir boilerplate |
| Spring Boot Actuator | 4.0.2 | Health e métricas |
| Spring DotEnv | Latest | Variáveis de ambiente |

## 🚨 Troubleshooting

### Erro: "Cannot connect to database"

```
java.sql.SQLException: Connection refused
```

**Causas possíveis:**
- MySQL não está rodando
- `SPRING_DATASOURCE_URL` incorreto em `.env`
- Credenciais incorretas

**Solução:**
```bash
# Verificar MySQL
docker ps | grep mysql-helppet

# Editar .env
vim .env

# Testar conexão
mysql -h localhost -u root -p
```

### Erro: "X-User-Id header missing"

```
401 Unauthorized
```

**Causa:** Requisição não passou pelo Gateway

**Solução:**
```bash
# Chamar via Gateway
curl http://localhost:8080/api/v1/pets \
  -H "Authorization: Bearer $TOKEN"

# NÃO chamar diretamente
curl http://localhost:8081/api/v1/pets  # ❌ Não funciona
```

### Erro: "Pet not found or access denied"

```
404 Not Found
```

**Causa:** Pet pertence a outro usuário (validação de acesso)

**Solução:**
```bash
# Listar seus pets
curl http://localhost:8080/api/v1/pets \
  -H "Authorization: Bearer $TOKEN" | jq '.[] | .id'
```

## 📚 Referências

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Hibernate](https://hibernate.org/)
- [MySQL](https://www.mysql.com/)
- [REST API Best Practices](https://restfulapi.net/)

---

**Versão:** 1.0.0  
**Status:** ✅ Production Ready  
**Última atualização:** Maio 2025
