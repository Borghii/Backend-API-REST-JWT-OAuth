
# ✅ API REST con Spring Boot – Proyecto de Autenticación y Gestión de Usuarios

API REST desarrollada con Spring Boot que implementa autenticación y autorización mediante JWT y OAuth2. Incluye manejo de roles, pruebas unitarias e integración, documentación Swagger, y despliegue en AWS con Docker.


## 📌 Características

- 🔐 Autenticación y autorización con JWT + OAuth2
- 👥 Manejo de usuarios, roles y permisos (Admin/Dev/Invited)
- 📦 CRUD completo con control de acceso
- 📄 Documentación Swagger UI
- 🐳 Contenedores con Docker para backend y base de datos
- 🧪 Pruebas unitarias e integración (JUnit, Mockito, TestContainers)
- ☁️ Despliegue en AWS con ALB, Auto Scaling y EC2
- ⚠️ Manejo global de errores con `@ControllerAdvice`
- 🪵 Logging centralizado con SLF4J + Logback
- 🧬 Migraciones de base de datos automáticas con Flyway



## 🛠️ Tecnologías utilizadas

| Tecnología        | Uso                              |
|------------------|----------------------------------|
| Spring Boot       | Framework principal               |
| Spring Security   | Seguridad, JWT y OAuth2           |
| Spring Data JPA   | Persistencia con Hibernate        |
| MySQL             | Base de datos relacional          |
| Flyway            | Migraciones de esquema SQL        |
| Swagger/OpenAPI   | Documentación automática de la API|
| Docker + Compose  | Contenedores para despliegue      |
| JUnit + Mockito   | Pruebas unitarias                 |
| TestContainers    | Pruebas de integración reales     |
| AWS EC2 + ALB     | Despliegue y escalabilidad        |



## 🚀 Cómo ejecutar el proyecto

### 🔧 Requisitos

- Java 17+
- Maven 3.8+
- Docker y Docker Compose
- Crear .env file con las variables de entorno necesarias (ver sección de Docker)
- Crear claves .pem para authentication con JWT y OAuth2 (ver sección de seguridad)

### ▶️ Levantar aplicación con Docker Compose

```bash
docker-compose up --build
```

Esto levantará el backend y una instancia de MySQL en Docker.



## 🔐 Endpoints de Autenticación

| Método | Endpoint              | Descripción                  |
|--------|------------------------|------------------------------|
| POST   | `/api/v1/auth/sign-up`| Registro de nuevos usuarios |
| POST   | `/api/v1/auth/login`  | Login y obtención de JWT    |



## 👤 Endpoints de Usuarios

| Método | Endpoint                  | Descripción            |
|--------|----------------------------|------------------------|
| GET    | `/api/v1/users`            | Listar todos los usuarios |
| GET    | `/api/v1/users/{id}`       | Obtener usuario por ID |
| POST   | `/api/v1/users`            | Crear nuevo usuario    |
| PUT    | `/api/v1/users/{userId}`   | Actualizar usuario     |
| DELETE | `/api/v1/users/{id}`       | Eliminar usuario       |



## 🤖 Endpoint extra – Gemini API

| Método | Endpoint                            | Descripción                     |
|--------|--------------------------------------|---------------------------------|
| GET    | `/api/v1/gemini/nickname/{name}`     | Genera un apodo argentino usando IA |



## ❤️ Health Check

```http
GET /health
```



## 🧪 Pruebas

El proyecto cuenta con cobertura de pruebas en:

- Lógica de negocio (`service`)
- Repositorios (`repository`)
- Controladores (`controller`)
- Seguridad (`JWT`, OAuth2)
- Pruebas de integración con bases reales (`TestContainers`)

Ejecutar:

```bash
mvn test
```



## 🐳 Docker

### `Dockerfile`

```dockerfile
FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/api.rest-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app_api-rest-springboot.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar","app_api-rest-springboot.jar"]
```

### `docker-compose.yml`

```yaml
services:
  api.rest:
    build: Project-API-REST-JWT-OAuth
    mem_limit: 512m
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: ${SPRING_DATASOURCE_URL}
      SPRING_DATASOURCE_USERNAME: ${SPRING_DATASOURCE_USERNAME}
      SPRING_DATASOURCE_PASSWORD: ${SPRING_DATASOURCE_PASSWORD}
      SPRING_AI_OPENAI_API_KEY: ${SPRING_AI_OPENAI_API_KEY}
    depends_on:
      - user_db

  user_db:
    image: mysql:8.4
    ports:
      - "3307:3306"
    environment:
      MYSQL_ROOT_PASSWORD: test
      MYSQL_DATABASE: project-api-rest
    restart: always
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-u", "root", "-ptest"]
      timeout: 20s
      retries: 15
      interval: 5s
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:


```



## ☁️ Despliegue en AWS

Desplegado con:

- EC2 (Dockerizado)
- Load Balancer (ALB)
- Auto Scaling Groups
- Imagen Docker personalizada para el backend



![Diagram-Api-REST-JWT drawio (4)](https://github.com/user-attachments/assets/bc200f4e-f4e7-463c-bb18-e1a87be93fd9)




## 📂 Estructura del proyecto (resumen)

```bash
src/
 ├── main/
 │    ├── java/project/api/rest/
 │    │    ├── controller/
 │    │    ├── entity/
 │    │    ├── service/
 │    │    ├── security/
 │    │    ├── dto/
 │    │    ├── mapper/
 │    │    ├── exceptions/
 │    │    ├── config/
 │    ├── resources/
 │         ├── db/migration/  # Scripts de Flyway
 │         └── application.yml
 └── test/
      ├── integration/
      ├── controller/
      └── service/
```

