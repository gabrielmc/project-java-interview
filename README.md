# 🚀 Mini Mundo - Sistema de Gerenciamento de Projetos

Sistema completo de gerenciamento de projetos e tarefas desenvolvido com **Spring Boot**, **JWT Authentication**, e **PostgreSQL**.

## 📋 Índice

- [Tecnologias](#-tecnologias)
- [Instalação](#-instalação)
- [Rodando o Projeto](#-rodando-o-projeto)
- [Endpoints da API](#-endpoints-da-api)
- [Testes com Postman/Insomnia](#-testes-com-postmaninsomnia)
- [CI/CD](#-cicd)
- [Estrutura do Projeto](#-estrutura-do-projeto)

---

## 🛠️ Tecnologias

- **Java 8**
- **Spring Boot 2.7.18**
- **Spring Security + JWT**
- **Spring Data JPA**
- **PostgreSQL 13**
- **Lombok**
- **Maven**
- **Docker & Docker Compose**

---

## 📥 Instalação

### 1. Clone o repositório

### 2. Configure o projeto no Spring Initializr (se for do zero)

Ou use os arquivos já criados neste repositório.

---

## 🐳 Rodando o Projeto

### **Opção 1: Com Docker (Recomendado)**

```bash
# Build e start de todos os containers
docker-compose up --build -d

# Ver logs
docker-compose logs -f app

# Parar
docker-compose down
```

**Acessos:**
- API: http://localhost:8080/api
- Banco: localhost:5432

### **Opção 2: Localmente (Maven)**

```bash
# 1. Subir apenas o PostgreSQL
docker-compose up postgres -d

# 2. Rodar a aplicação
mvn spring-boot:run

# Ou compilar e executar o JAR
mvn clean package -DskipTests
java -jar target/minimundo-0.0.1-SNAPSHOT.war
```

---

## 🌐 Endpoints da API

### **🔐 Autenticação**

#### Cadastro
```http
POST /api/auth/register
Content-Type: application/json

{
  "nome": "João Silva",
  "email": "joao@email.com",
  "senha": "123456"
}
```

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "tipo": "Bearer",
  "usuarioId": 1,
  "nome": "João Silva",
  "email": "joao@email.com"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "joao@email.com",
  "senha": "123456"
}
```

---

### **📁 Projetos**

> ⚠️ **Todos os endpoints abaixo requerem autenticação!**  
> Header: `Authorization: Bearer {seu-token-jwt}`

#### Criar Projeto
```http
POST /api/projetos
Authorization: Bearer {token}
Content-Type: application/json

{
  "nome": "Sistema de Gestão",
  "descricao": "Sistema completo de gestão de Gestão",
  "status": "ATIVO",
  "orcamentoDisponivel": 50000.00
}
```

#### Listar Projetos
```http
GET /api/projetos
Authorization: Bearer {token}

# Com filtros (opcionais)
GET /api/projetos?status=ATIVO
GET /api/projetos?nome=Sistema
```

#### Buscar Projeto por ID
```http
GET /api/projetos/1
Authorization: Bearer {token}
```

#### Atualizar Projeto
```http
PUT /api/projetos/1
Authorization: Bearer {token}
Content-Type: application/json

{
  "nome": "Sistema de Gestão v2",
  "descricao": "Sistema atualizado",
  "status": "ATIVO",
  "orcamentoDisponivel": 75000.00
}
```

#### Deletar Projeto
```http
DELETE /api/projetos/1
Authorization: Bearer {token}
```

---

### **✅ Tarefas**

#### Criar Tarefa
```http
POST /api/tarefas
Authorization: Bearer {token}
Content-Type: application/json

{
  "descricao": "Implementar login",
  "projetoId": 1,
  "dataInicio": "2024-01-15",
  "dataFim": "2024-01-20",
  "tarefaPredecessoraId": null,
  "status": "NAO_CONCLUIDA"
}
```

#### Listar Tarefas de um Projeto
```http
GET /api/tarefas?projetoId=1
Authorization: Bearer {token}

# Com filtros (opcionais)
GET /api/tarefas?projetoId=1&status=CONCLUIDA
GET /api/tarefas?projetoId=1&descricao=login
```

#### Buscar Tarefa por ID
```http
GET /api/tarefas/1
Authorization: Bearer {token}
```

#### Atualizar Tarefa
```http
PUT /api/tarefas/1
Authorization: Bearer {token}
Content-Type: application/json

{
  "descricao": "Implementar login com JWT",
  "projetoId": 1,
  "dataInicio": "2024-01-15",
  "dataFim": "2024-01-22",
  "status": "CONCLUIDA"
}
```

#### Deletar Tarefa
```http
DELETE /api/tarefas/1
Authorization: Bearer {token}
```

---

## 🧪 Testes com Postman/Insomnia

### 1. **Cadastre um usuário**
```
POST http://localhost:8080/api/auth/register
```

### 2. **Copie o token JWT da resposta**

### 3. **Configure o Header Authorization**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

### 4. **Teste os outros endpoints!**

---

## 🔄 CI/CD

O projeto está configurado para **deploy automático** via GitHub Actions.

### Setup:

1. **Configure os Secrets no GitHub:**
   - Vá em: `Settings → Secrets → Actions`
   - Adicione:
     - `DOCKER_USERNAME` = seu usuário do Docker Hub
     - `DOCKER_PASSWORD` = seu token do Docker Hub

2. **Edite o arquivo `.github/workflows/ci-cd.yml`:**
   - Altere `DOCKER_IMAGE` para `seu-usuario/minimundo`

3. **Faça o deploy:**

```bash
# Commit suas alterações
git add .
git commit -m "feat: implementação completa do sistema"
git push origin master

# Crie uma tag para deploy
git tag v1.0.0
git push origin v1.0.0
```

4. **Aguarde o build!** A imagem será publicada no Docker Hub automaticamente.

### Rodando a imagem do Docker Hub:

```bash
docker pull seu-usuario/minimundo:latest
docker run -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e DB_PORT=5432 \
  -e DB_NAME=minimundo \
  -e DB_USER=postgres \
  -e DB_PASSWORD=postgres \
  seu-usuario/minimundo:latest
```

---

## 📂 Estrutura do Projeto

```
minimundo/
├── src/main/java/com/minimundo/
│   ├── MiniMundoApplication.java       # Classe principal
│   ├── config/                          # Configurações
│   │   ├── SecurityConfig.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── CorsConfig.java
│   ├── controller/                      # Controllers REST
│   │   ├── AuthController.java
│   │   ├── ProjetoController.java
│   │   └── TarefaController.java
│   ├── dto/                             # DTOs
│   │   ├── request/
│   │   └── response/
│   ├── exception/                       # Exceções
│   ├── model/                           # Entidades JPA
│   │   ├── Usuario.java
│   │   ├── Projeto.java
│   │   └── Tarefa.java
│   ├── repository/                      # Repositories
│   ├── service/                         # Services
│   └── util/                            # Utilitários
│       └── JwtUtil.java
├── src/main/resources/
│   └── application.properties
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

## 🎯 Regras de Negócio Implementadas

### **Projetos:**
- ✅ Nome único por usuário
- ✅ Status: ATIVO ou INATIVO
- ✅ Não pode excluir projeto com tarefas
- ✅ Apenas o dono pode editar/excluir

### **Tarefas:**
- ✅ Obrigatoriamente vinculada a um projeto
- ✅ Data fim não pode ser antes da data início
- ✅ Tarefa predecessora deve ser do mesmo projeto
- ✅ Não pode excluir tarefa que é predecessora
- ✅ Status: CONCLUIDA ou NAO_CONCLUIDA

### **Segurança:**
- ✅ Senha criptografada com BCrypt
- ✅ Autenticação via JWT
- ✅ Validação de permissões em todas as operações
- ✅ Token expira em 24h

---

## 📝 Convenções do Projeto

### **Git Flow:**
- `master` - branch principal (produção)
- `develop` - branch de desenvolvimento
- `feature/*` - novas funcionalidades
- `hotfix/*` - correções urgentes

### **Commits (Conventional Commits):**
```bash
feat: adiciona autenticação JWT
fix: corrige validação de datas
docs: atualiza README
refactor: melhora estrutura de services
```

---

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch: `git checkout -b feature/minha-feature`
3. Commit: `git commit -m 'feat: minha nova feature'`
4. Push: `git push origin feature/minha-feature`
5. Abra um Pull Request

---

## 📄 Licença

Este projeto está sob a licença MIT.

---

## 👨‍💻 Autor

Desenvolvido por **[Seu Nome]**

- GitHub: [@seu-usuario](https://github.com/seu-usuario)
- LinkedIn: [Seu Nome](https://linkedin.com/in/seu-perfil)

---

## 🙏 Agradecimentos

Projeto desenvolvido como parte de avaliação técnica.

**Enjoy coding! 🚀**