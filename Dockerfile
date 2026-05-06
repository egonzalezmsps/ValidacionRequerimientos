# Multi-stage Dockerfile optimizado para Java 25 con Spring Boot
# Stage 1: Build
FROM eclipse-temurin:25-jdk-alpine AS builder

# Instalar dependencias necesarias para la compilación
RUN apk add --no-cache bash

# Establecer directorio de trabajo
WORKDIR /workspace/app

# Copiar archivos de Maven
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Descargar dependencias (se cachea esta capa si pom.xml no cambia)
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente
COPY src src

# Compilar la aplicación
RUN ./mvnw clean package -DskipTests -B && \
    mkdir -p target/dependency && \
    (cd target/dependency; jar -xf ../*.jar)

# Stage 2: Runtime
FROM eclipse-temurin:25-jre-alpine

# Crear usuario no-root para ejecutar la aplicación
RUN addgroup -g 1000 spring && \
    adduser -u 1000 -G spring -s /bin/sh -D spring

# Instalar dumb-init para manejo correcto de señales
RUN apk add --no-cache dumb-init

# Variables de entorno para optimización de JVM con Java 25
ENV JAVA_OPTS="-XX:+UseZGC \
    -XX:+EnableDynamicAgentLoading \
    --enable-preview \
    -Xmx512m \
    -Xms256m \
    -XX:MaxMetaspaceSize=128m \
    -XX:+ExitOnOutOfMemoryError \
    -Djava.security.egd=file:/dev/./urandom \
    -Dspring.backgroundpreinitializer.ignore=true"

# Crear directorio para la aplicación
WORKDIR /app

# Copiar las capas de la aplicación desde el builder
COPY --from=builder /workspace/app/target/dependency/BOOT-INF/lib /app/lib
COPY --from=builder /workspace/app/target/dependency/META-INF /app/META-INF
COPY --from=builder /workspace/app/target/dependency/BOOT-INF/classes /app

# Cambiar al usuario no-root
USER spring:spring

# Exponer puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/api/v1/actuator/health || exit 1

# Usar dumb-init para ejecutar la aplicación
ENTRYPOINT ["dumb-init", "--"]

# Comando para ejecutar la aplicación con Virtual Threads habilitados
CMD ["sh", "-c", "java ${JAVA_OPTS} \
    -Dspring.threads.virtual.enabled=true \
    -cp app:app/lib/* \
    ${package}.MainApplication"]
