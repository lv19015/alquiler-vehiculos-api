# ============================================================
# Etapa 1: Compilacion (build)
# Usa un JDK completo para compilar el proyecto con Gradle
# ============================================================
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Se copian primero los archivos de Gradle para aprovechar la cache de capas:
# si no cambian, Docker no vuelve a descargar las dependencias.
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./

# Por si gradlew llego con saltos de linea de Windows (CRLF)
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# Descarga las dependencias (si falla aqui, se reintenta en el paso siguiente)
RUN ./gradlew --no-daemon dependencies > /dev/null 2>&1 || true

# Se copia el codigo fuente y se genera el JAR ejecutable (sin correr las pruebas)
COPY src src
RUN ./gradlew --no-daemon bootJar -x test

# ============================================================
# Etapa 2: Ejecucion (runtime)
# Usa solo un JRE, mucho mas liviano que el JDK de la etapa 1
# ============================================================
FROM eclipse-temurin:21-jre
WORKDIR /app

# Usuario sin privilegios: buena practica de seguridad
RUN useradd --system --uid 1001 spring

# Solo se copia el JAR generado en la etapa anterior
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar
USER spring

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
