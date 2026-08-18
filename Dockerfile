# ---------- Etapa 1: build del WAR con Maven ----------
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Descarga dependencias y empaqueta el WAR (queda como ROOT.war)
RUN mvn -B clean package -DskipTests

# ---------- Etapa 2: imagen final de WildFly ----------
FROM quay.io/wildfly/wildfly:33.0.2.Final-jdk17

# Copiamos el WAR generado al directorio de despliegues de WildFly
COPY --from=build /app/target/ROOT.war /opt/jboss/wildfly/standalone/deployments/ROOT.war

# Exponemos el puerto HTTP (8080) y el de administración (9990)
EXPOSE 8080 9990

# Arrancamos WildFly escuchando en todas las interfaces (necesario dentro de Docker)
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
