# Demo: WildFly + Docker Compose + GitHub Actions

Proyecto de ejemplo para aprender a levantar un servidor **WildFly** dentro
de un contenedor Docker, orquestado con **Docker Compose**, y con un
workflow de **GitHub Actions** que compila y prueba la imagen automáticamente.

## Estructura del proyecto

```
wildfly-docker-demo/
├── pom.xml                          # Proyecto Maven que genera el WAR
├── Dockerfile                       # Build multi-etapa: Maven -> WildFly
├── docker-compose.yml               # Orquestación del contenedor
├── src/main/java/...HelloServlet.java   # Servlet Java de ejemplo (/hello)
├── src/main/webapp/index.html       # Página de inicio
├── src/main/webapp/WEB-INF/web.xml  # Configuración mínima de la app
└── .github/workflows/docker-build.yml   # CI: build + prueba del contenedor
```

## Cómo funciona

1. El `Dockerfile` tiene dos etapas:
   - **Etapa 1 (build):** usa una imagen de Maven para compilar el proyecto
     y generar un archivo `ROOT.war`.
   - **Etapa 2 (runtime):** parte de la imagen oficial `quay.io/wildfly/wildfly`
     y copia el `ROOT.war` a la carpeta de despliegues automáticos de WildFly
     (`/opt/jboss/wildfly/standalone/deployments/`). Al llamarse `ROOT.war`,
     la app queda publicada directamente en la raíz (`http://localhost:8080/`).
2. `docker-compose.yml` construye esa imagen y expone los puertos 8080 (app)
   y 9990 (consola de administración).
3. El workflow de GitHub Actions (`.github/workflows/docker-build.yml`) hace
   `docker compose build` y `docker compose up`, espera a que la app responda
   y luego apaga todo. Así te avisa si un cambio rompe el build o el arranque.

## Requisitos previos

- Tener [Docker](https://www.docker.com/) y Docker Compose instalados
  (Docker Desktop en Windows/Mac ya lo incluye).
- No necesitas Java ni Maven instalados localmente: todo se compila dentro
  del contenedor, gracias al build multi-etapa.

## Pasos para correr el contenedor en tu máquina

1. **Clona o descarga** el proyecto y entra a la carpeta:
   ```bash
   cd wildfly-docker-demo
   ```

2. **Construye la imagen** (compila el WAR y arma la imagen de WildFly):
   ```bash
   docker compose build
   ```

3. **Levanta el contenedor**:
   ```bash
   docker compose up
   ```
   Si prefieres que corra en segundo plano, usa `docker compose up -d`.

4. **Espera unos segundos** a que WildFly termine de arrancar. Verás en los
   logs una línea similar a `WildFly ... started in Xms`.

5. **Abre el navegador** en:
   - `http://localhost:8080/` → verás la página de inicio.
   - `http://localhost:8080/hello` → verás la respuesta del servlet Java.
   - `http://localhost:9990/` → consola de administración de WildFly
     (por defecto no tiene usuario creado; es opcional para esta demo).

6. **Para detener el contenedor**:
   ```bash
   docker compose down
   ```

## Comandos útiles

| Acción                                | Comando                              |
|----------------------------------------|---------------------------------------|
| Ver logs en vivo                        | `docker compose logs -f`             |
| Reconstruir sin usar caché              | `docker compose build --no-cache`    |
| Entrar al contenedor (debug)            | `docker exec -it wildfly-demo bash`  |
| Ver contenedores corriendo              | `docker ps`                          |

## Sobre el workflow de GitHub Actions

Cada vez que hagas `push` o abras un *pull request* contra `main`, GitHub
Actions va a:
1. Descargar tu código (`actions/checkout`).
2. Construir la imagen con `docker compose build`.
3. Levantarla con `docker compose up -d`.
4. Hacer *polling* a `http://localhost:8080/` hasta 30 veces (cada 5s) para
   confirmar que WildFly respondió correctamente.
5. Mostrar los logs del contenedor (útil para depurar si algo falla).
6. Apagar y limpiar los contenedores al final, pase lo que pase.

Puedes ver el resultado en la pestaña **Actions** de tu repositorio en GitHub.

### Cómo subir esto a GitHub

```bash
git init
git add .
git commit -m "Proyecto inicial: WildFly + Docker Compose + GitHub Actions"
git branch -M main
git remote add origin https://github.com/TU_USUARIO/TU_REPO.git
git push -u origin main
```

En cuanto hagas el `push`, el workflow se disparará automáticamente.

## Siguientes pasos sugeridos

- Agregar autenticación a la consola de administración (`add-user.sh` dentro
  de la imagen, o variables de entorno).
- Publicar la imagen en GitHub Container Registry (GHCR) o Docker Hub desde
  el mismo workflow, usando `docker/build-push-action`.
- Añadir una base de datos (por ejemplo PostgreSQL) como otro `service` en
  el `docker-compose.yml` y configurar un *datasource* en WildFly.
