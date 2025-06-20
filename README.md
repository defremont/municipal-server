# Municipal Servidor API

Sistema de gestão de servidores públicos municipais desenvolvido com Spring Boot e MongoDB.

## 📋 Sobre o Projeto

API REST para gerenciamento de servidores públicos municipais e suas respectivas secretarias. Permite operações CRUD completas com validações robustas e tratamento de erros.

## ✨ Funcionalidades

### 📦 Secretarias
- ✅ Listar todas as secretarias
- ✅ Criar nova secretaria
- ✅ Atualizar secretaria existente
- ✅ Excluir secretaria (com validação de dependências)

### 👥 Servidores
- ✅ Listar todos os servidores
- ✅ Criar novo servidor
- ✅ Atualizar servidor existente
- ✅ Excluir servidor

## 🛠 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data MongoDB**
- **Spring Boot Validation**
- **MongoDB**
- **Maven**
- **SLF4J + Logback**

## 📊 Modelo de Dados

### Secretaria
```json
{
  "id": "string",
  "nome": "string (2-100 caracteres)",
  "sigla": "string (2-10 caracteres, maiúsculo, único)",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

### Servidor
```json
{
  "id": "string",
  "nome": "string (2-100 caracteres)",
  "email": "string (formato email válido, único)",
  "dataNascimento": "date (passado, idade entre 18-75 anos)",
  "secretaria": {
    "id": "string"
  },
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

## ⚙️ Configuração

### Pré-requisitos
- Java 17+
- Maven 3.6+
- MongoDB 4.4+

### Configuração do MongoDB
A aplicação está configurada para conectar ao MongoDB com as seguintes configurações:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://admin:password@localhost:27017/municipal-db?authSource=admin
```

### Instalação e Execução

1. **Clone o repositório**
```bash
git clone <repository-url>
cd servidor-api
```

2. **Configure o MongoDB**
Assegure-se de que o MongoDB está rodando com as credenciais configuradas no `application.yml`

3. **Execute a aplicação**
```bash
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

## 📚 Endpoints da API

### Secretarias

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/secretarias` | Lista todas as secretarias |
| POST | `/api/secretarias` | Cria nova secretaria |
| PUT | `/api/secretarias` | Atualiza secretaria (ID no body) |
| DELETE | `/api/secretarias/{id}` | Remove secretaria |

### Servidores

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/servidores` | Lista todos os servidores |
| POST | `/api/servidores` | Cria novo servidor |
| PUT | `/api/servidores` | Atualiza servidor (ID no body) |
| DELETE | `/api/servidores/{id}` | Remove servidor |

## 📝 Exemplos de Uso

### Criar Secretaria
```bash
curl -X POST http://localhost:8080/api/secretarias \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Secretaria de Educação",
    "sigla": "SEDUC"
  }'
```

### Atualizar Secretaria
```bash
curl -X PUT http://localhost:8080/api/secretarias \
  -H "Content-Type: application/json" \
  -d '{
    "id": "secretaria-id-aqui",
    "nome": "Secretaria Municipal de Educação",
    "sigla": "SEDUC"
  }'
```

### Criar Servidor
```bash
curl -X POST http://localhost:8080/api/servidores \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao.silva@municipal.gov.br",
    "dataNascimento": "1985-03-15",
    "secretaria": {
      "id": "secretaria-id-aqui"
    }
  }'
```

### Atualizar Servidor
```bash
curl -X PUT http://localhost:8080/api/servidores \
  -H "Content-Type: application/json" \
  -d '{
    "id": "servidor-id-aqui",
    "nome": "João da Silva Santos",
    "email": "joao.santos@municipal.gov.br",
    "dataNascimento": "1985-03-15",
    "secretaria": {
      "id": "secretaria-id-aqui"
    }
  }'
```

## 🔒 Validações

### Secretaria
- **Nome**: Obrigatório, 2-100 caracteres
- **Sigla**: Obrigatória, 2-10 caracteres, única, convertida para maiúsculo

### Servidor
- **Nome**: Obrigatório, 2-100 caracteres
- **Email**: Obrigatório, formato válido, único
- **Data de Nascimento**: Obrigatória, data passada, idade entre 18-75 anos
- **Secretaria**: Obrigatória, deve existir no sistema (informar apenas o ID)

## 🚨 Tratamento de Erros

A API retorna respostas de erro estruturadas:

```json
{
  "timestamp": "2023-12-01T10:15:30",
  "status": 400,
  "error": "Erro de validação",
  "message": "Nome é obrigatório",
  "path": "/api/servidores"
}
```

### Códigos de Status
- **200 OK**: Operação realizada com sucesso
- **201 Created**: Recurso criado com sucesso
- **204 No Content**: Recurso deletado com sucesso
- **400 Bad Request**: Erro de validação ou regra de negócio
- **404 Not Found**: Recurso não encontrado
- **500 Internal Server Error**: Erro interno do servidor

## 🧪 Testes

```bash
# Executar testes
mvn test

# Executar com cobertura
mvn test jacoco:report
```

## 📁 Estrutura do Projeto

```
src/main/java/com/municipal/
├── config/          # Configurações (CORS, etc.)
├── controller/      # Controllers REST
├── exception/       # Tratamento de exceções
├── model/          # Entidades/Modelos
├── repository/     # Repositórios MongoDB
├── service/        # Camada de serviços
├── validation/     # Validações customizadas
└── ServidorApiApplication.java
```

## 🏗 Arquitetura

O projeto segue uma arquitetura em camadas:

- **Controller**: Recebe requisições HTTP e retorna respostas
- **Service**: Contém lógica de negócio e validações
- **Repository**: Acesso aos dados no MongoDB
- **Model**: Entidades do domínio
- **Exception**: Tratamento centralizado de erros

## 🔐 Segurança

- Validação de dados de entrada
- Sanitização de campos (sigla em maiúsculo)
- Validações de integridade referencial
- Logs de auditoria para operações críticas