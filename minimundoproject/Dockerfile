# ========================================
# STAGE 1: Build da aplicação
# ========================================
FROM maven:3.8-openjdk-8 AS builder

WORKDIR /app

# Cache de dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Build
COPY src ./src
RUN mvn clean package -DskipTests

# ========================================
# STAGE 2: Imagem de produção
# ========================================
FROM openjdk:8-jre-alpine

WORKDIR /app

# Copia WAR buildado
COPY --from=builder /app/target/*.war app.war

# Variáveis de ambiente
ENV DB_HOST=postgres \
    DB_PORT=5432 \
    DB_NAME=minimundo \
    DB_USER=postgres \
    DB_PASSWORD=postgres \
    JAVA_OPTS="-Xmx512m -Xms256m"

# Porta da aplicação
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/api/actuator/health || exit 1

# Comando de inicialização
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.war"]