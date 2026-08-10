# PromptVault

PromptVault is a production-ready Spring Boot backend application for storing, organizing, and managing AI prompts. It enables users to securely create, categorize, version, search, and manage prompts through a RESTful API with JWT-based authentication.

---

## Features

- Secure user registration and login using JWT authentication
- Create, update, retrieve, and delete prompts
- Organize prompts with collections and tags
- Mark frequently used prompts as favorites
- Automatic version history for prompt edits
- Search and filter prompts efficiently
- Interactive API documentation with Swagger UI

---

## Tech Stack

- Java 23
- Spring Boot
- Spring Security
- JWT Authentication
- Spring Data JPA (Hibernate)
- MySQL
- Maven
- Lombok
- MapStruct
- Swagger / OpenAPI

---

## Running the Project

### 1. Clone the repository

```bash
git clone https://github.com/<your-username>/PromptVault.git
cd PromptVault
```

### 2. Create the database

Create a MySQL database named:

```sql
CREATE DATABASE promptvault;
```

### 3. Configure the database

Update the database credentials inside:

```text
src/main/resources/application.yml
```

Example:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/promptvault
    username: your_username
    password: your_password
```

### 4. Run the application

Using Maven:

```bash
mvn spring-boot:run
```

Or run the main Spring Boot application class from your IDE.

---

## API Base URL

```text
http://localhost:8080
```

### Swagger UI

```text
http://localhost:8080/swagger-ui.html
```

---

## Project Structure

```text
src
└── main
    └── java
        └── com.promptvault
            ├── config
            ├── controller
            ├── dto
            ├── entity
            ├── exception
            ├── mapper
            ├── repository
            ├── security
            ├── service
            │   └── impl
            └── PromptVaultApplication.java
```

---
