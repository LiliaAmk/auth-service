# Auth Service for Document Management System

Authentication and authorization microservice for the Document Management System (DMS). This service handles user registration, login, and token management.

## Features

- User registration with email and password
- JWT token generation and validation
- Role-based authorization (USER, ADMIN)
- User data persistence with JPA
- Integration with Spring Security
- RESTful API for client interaction

## Tech Stack

- Java 17
- Spring Boot 3.x
- Spring Security with JWT
- Spring Data JPA
- H2 Database (development) / PostgreSQL (production)
- Maven

## API Endpoints

### Authentication

- **POST /auth/register**: Register a new user
  ```json
  {
    "email": "user@dms.com",
    "password": "password",
    "roles": ["ROLE_USER"]
  }
  ```

- **POST /auth/login**: Login and receive JWT token
  ```json
  {
    "email": "user@dms.com",
    "password": "password"
  }
  ```
  Response:
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "expiresIn": 86400
  }
  ```

### User Management

- **GET /auth/user/{email}**: Get user details by email (used for synchronization with other services)
- **GET /auth/users**: List all users (admin only)

## Security

- Passwords are hashed using BCrypt
- Authentication is stateless via JWT tokens
- User roles are embedded in the JWT token
- Token validity can be configured (default: 24 hours)

## Getting Started

### Prerequisites

- JDK 17+
- Maven 3.6+

### Running the Service

```bash
# Build the application
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

The service will be available at http://localhost:8081.

## Integration with API Gateway

The Auth Service is designed to work behind the API Gateway:

1. Clients register and login through the gateway at `/auth/register` and `/auth/login`
2. The gateway forwards the requests to the Auth Service
3. On successful authentication, the Auth Service returns a JWT token
4. Clients use this token in subsequent requests to other services
5. The API Gateway validates the token and forwards the user's identity via headers

## Configuration

Key settings in `application.properties`:

```properties
# Server
server.port=8081

# Database (H2 for development)
spring.datasource.url=jdbc:h2:mem:authdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=update

# JWT Configuration
jwt.secret=abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789
jwt.expiration=86400000
```
