# Note0 - Note Sharing Application (Java Spring Boot)

`Note0` is a comprehensive note-sharing application designed for students to upload, share, and rate academic materials. This project is a complete conversion from an original Node.js backend to a modern, robust, and secure Java Spring Boot application.

**Current Status: In Development (Core User Authentication Implemented)**

This README documents the project's structure, setup, and current capabilities, which include a fully functional and secure user registration endpoint connected to a cloud PostgreSQL database.

## Table of Contents
1.  [Technology Stack](#technology-stack)
2.  [Features Implemented](#features-implemented)
3.  [Project Structure](#project-structure)
4.  [Getting Started](#getting-started)
    *   [Prerequisites](#prerequisites)
    *   [Database Setup (Aiven)](#database-setup-aiven)
    *   [Configuration](#configuration)
    *   [Running the Application](#running-the-application)
5.  [API Endpoints](#api-endpoints)
    *   [Authentication](#authentication)
6.  [How to Test with Postman](#how-to-test-with-postman)
7.  [Next Steps](#next-steps)

## Technology Stack

This project is built with a modern and powerful Java technology stack, chosen for its robustness, security, and maintainability.

| Component      | Technology                                                              | Purpose                                     |
|----------------|-------------------------------------------------------------------------|---------------------------------------------|
| **Runtime**    | Java 17                                                                 | Core programming language                   |
| **Framework**  | Spring Boot 3.3+                                                        | Application framework for rapid development |
| **Security**   | Spring Security 6+                                                      | Authentication and Access Control           |
| **Database**   | Spring Data JPA / Hibernate                                             | Object-Relational Mapping (ORM)             |
| **Database**   | PostgreSQL (Hosted on [Aiven](https://aiven.io/))                       | Relational database for data persistence      |
| **Web**        | Spring Web (MVC)                                                        | Building RESTful APIs                       |
| **Validation** | Jakarta Bean Validation                                                 | Data validation for API inputs              |
| **Build Tool** | Maven                                                                   | Dependency Management and Build Automation  |

## Features Implemented

As of the current version, the core foundation for user management is complete:

✅ **Secure User Registration:**
-   An API endpoint (`POST /api/auth/register`) for new user creation.
-   **Password Encryption:** User passwords are securely hashed using `BCryptPasswordEncoder` before being stored.
-   **Input Validation:** Incoming registration data (full name, email, password) is validated to ensure correctness (e.g., valid email format, non-blank fields).
-   **Duplicate Email Check:** The system prevents registration with an email that is already in use.

✅ **Cloud Database Integration:**
-   The application is fully connected to a PostgreSQL database hosted on Aiven.
-   JPA entities are successfully mapped to the database schema.
-   Automatic schema management (`ddl-auto=update`) is enabled for development.

✅ **Robust Security Foundation:**
-   Spring Security is configured to protect the application.
-   Cross-Site Request Forgery (CSRF) protection is disabled, which is standard practice for stateless REST APIs.
-   Specific endpoints (`/api/auth/**`) are whitelisted to allow public access for registration and login.

## Project Structure

The project follows the standard Maven and Spring Boot conventions, with a clear separation of concerns.

```
src/main/
├── java/com/note0/
│   ├── controller/          # REST API endpoints (e.g., AuthController)
│   ├── dto/                 # Data Transfer Objects (e.g., RegisterDto)
│   ├── entity/              # JPA database entities (e.g., User)
│   ├── repository/          # Spring Data JPA repositories (e.g., UserRepository)
│   ├── security/            # Spring Security configuration (e.g., SecurityConfig)
│   ├── service/             # Business logic layer (e.g., AuthService)
│   └── Note0Application.java  # Main application entry point
│
└── resources/
    ├── application.properties # Application configuration (database, etc.)
    └── static/                # For future CSS, JS files
    └── templates/             # For future Thymeleaf HTML files
```

## Getting Started

Follow these steps to get a local instance of the application running.

### Prerequisites

-   **Java Development Kit (JDK)**: Version 17 or newer.
-   **Apache Maven**: Version 3.6+ for building the project.
-   **Postman**: (Recommended) for testing the API endpoints.
-   An **Aiven Account**: A free account to host the PostgreSQL database.

### Database Setup (Aiven)

1.  Log in to your [Aiven](https://aiven.io/) account.
2.  Create a new **PostgreSQL** service on the **Free** plan.
3.  Once the service is running, navigate to its "Overview" page.
4.  Under the "Connection information" section, find and copy the **Host**, **Port**, **User**, **Password**, and **Database Name**. You will need these for the next step.

### Configuration

1.  Navigate to `src/main/resources/`.
2.  Open the `application.properties` file.
3.  Copy the following configuration and **replace the placeholders** with your actual Aiven database credentials.

```properties
# Aiven PostgreSQL Database Configuration
spring.datasource.url=jdbc:postgresql://<YOUR_AIVEN_HOST>:<YOUR_AIVEN_PORT>/<YOUR_DATABASE_NAME>?sslmode=require
spring.datasource.username=<YOUR_AIVEN_USER>
spring.datasource.password=<YOUR_AIVEN_PASSWORD>

# JPA / Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

### Running the Application

1.  Open a terminal or command prompt in the root directory of the project.
2.  Build and run the application using Maven:
    ```bash
    ./mvnw spring-boot:run
    ```
3.  The server will start on `http://localhost:8080`. You can see the logs in your terminal, including the final `Started Note0Application...` message.

## API Endpoints

### Authentication

#### Register a New User

-   **Endpoint**: `POST /api/auth/register`
-   **Description**: Creates a new user account.
-   **Request Body** (`application/json`):
    ```json
    {
      "fullName": "Your Name",
      "email": "your.email@example.com",
      "password": "yourStrongPassword"
    }
    ```
-   **Success Response** (`200 OK`):
    ```json
    {
        "id": 1,
        "fullName": "Your Name",
        "email": "your.email@example.com",
        "passwordHash": null,
        "collegeName": null,
        "branch": null,
        "semester": null,
        "role": "USER",
        "active": true,
        "verified": false,
        "createdAt": "2025-09-15T12:30:00.123456"
    }
    ```
-   **Error Response** (`400 Bad Request`):
    -   If email is already in use: `"Email already in use"`
    -   If validation fails (e.g., blank password): Standard Spring validation error response.

## How to Test with Postman

1.  **Start the application** locally.
2.  Open **Postman**.
3.  Create a new request with the method **`POST`**.
4.  Set the URL to `http://localhost:8080/api/auth/register`.
5.  Go to the **Body** tab, select **raw**, and choose **JSON** from the dropdown.
6.  Paste the request body JSON (as shown above) and click **Send**.
7.  Review the response from the server.

## Next Steps

The project foundation is now solid. The immediate next steps are:

-   [ ] **Implement User Login**: Create a `/api/auth/login` endpoint.
-   [ ] **JWT Integration**: Generate a JWT on successful login and return it to the user.
-   [ ] **Token-Based Authentication**: Update Spring Security to validate JWTs for protected endpoints.
-   [ ] **Create User Profile Endpoint**: Implement a protected `/api/users/me` endpoint that requires a valid JWT.