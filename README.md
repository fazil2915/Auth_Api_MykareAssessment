# Auth API - Mykare Assessment

## Overview
This project is a Spring Boot-based authentication API with JWT authentication and PostgreSQL as the database. It includes Swagger for API documentation.

## Features
- JWT-based authentication
- User registration and login
- Spring Security integration
- PostgreSQL database
- Docker support
- Swagger API documentation
- Unit test cases

## Prerequisites
Ensure you have the following installed:
- Java 17
- Maven
- PostgreSQL (if running locally)
- Docker & Docker Compose (if running with Docker)

## Environment Variables
Create a `.env` file in the root directory and set the following variables:

```
DB_URL=jdbc:postgresql://localhost:5432/your_db
DB_USER=postgres
DB_PASS=password
JWT_SECRET=your-very-secret-key-which-is-32-bytes-long!!
```

## Running the Application Locally

1. Clone the repository:
   ```sh
   git clone <repo-url>
   cd <repo-folder>
   ```
2. Set up PostgreSQL (if not using Docker):
   ```sh
   psql -U postgres -c "CREATE DATABASE your_db;"
   ```
3. Build the project:
   ```sh
   mvn clean package -DskipTests
   ```
4. Run the application:
   ```sh
   mvn spring-boot:run
   ```
5. Access Swagger UI at:
   ```
   http://localhost:8080/swagger-ui/index.html
   ```
6. Running Tests:
   ```
   mvn test
   ```   

## Running the Application with Docker

1. Build and start the containers:
   ```sh
   docker-compose up --build
   ```
2. The application will be accessible at:
   ```
   http://localhost:8080
   ```
3. Access Swagger UI at:
   ```
   http://localhost:8080/swagger-ui/index.html
   ```

## API Endpoints
| Method | Endpoint | Description |
|--------|---------|-------------|
| POST | /api/auth/register | Register a new user |
| POST | /api/auth/login | Login and receive JWT |
| GET | /api/users/get-users (With header jwt token) | Get all users (Admin can only access it) |
| DELETE | /api/users/delete-user (With header jwt token) | Delete a user with email (Admin can only access it) |

## Notes
- The `target/` directory is automatically created during the build and should be ignored in Git.
- Ensure `.env` is correctly set up for both local and Docker environments.



