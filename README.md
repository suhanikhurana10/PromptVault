PromptVault

PromptVault is a Spring Boot backend project that helps users save, organize, and manage AI prompts in one place. It provides secure user authentication, prompt management, version history, collections, tags, and search functionality.

Features

User registration and login with JWT authentication

Create, update, and delete prompts

Organize prompts using collections and tags

Mark prompts as favorites

Keep version history of every prompt

Search and filter prompts

Swagger UI for API testing

Tech Stack

Java 23

Spring Boot

Spring Security + JWT

Spring Data JPA (Hibernate)

MySQL

Maven

Lombok

MapStruct

Swagger/OpenAPI

Running the Project

Clone the repository.

Create a MySQL database named promptvault.

Update the MySQL username and password in application.yml.

Run the application.

The backend will start on:

http://localhost:8080

Swagger UI:

http://localhost:8080/swagger-ui.html

Project Structure

controller/
service/
service/impl/
repository/
entity/
dto/
mapper/
security/
config/
exception/
