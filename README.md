# Note0 - Simple Desktop Note-Sharing Application

`Note0` is a desktop application built with Java Swing that allows users to register, log in, and share academic materials. This project serves as a foundational example of a database-driven desktop application, demonstrating core concepts of JDBC, secure password handling, file management, and role-based access control without the use of large frameworks like Spring Boot.

All user and material data is stored in a cloud-hosted PostgreSQL database managed by Aiven. Uploaded files are stored on Cloudinary and referenced by secure URLs in the database.

**Current Status:** Fully functional with core features implemented.

## Table of Contents
1.  [Features](#features)
2.  [Technology Stack](#technology-stack)
3.  [How It Works (Application Architecture)](#how-it-works-application-architecture)
4.  [Database Schema](#database-schema)
5.  [Getting Started (Setup Guide)](#getting-started-setup-guide)
    *   [Prerequisites](#prerequisites)
    *   [Database Setup](#database-setup)
    *   [Configuration](#configuration)
    *   [Build and Run](#build-and-run)
6.  [How to Use the Application](#how-to-use-the-application)

## Features

The application currently supports the following features:

✅ **User Authentication**
-   **Secure Registration:** New users can create an account. Passwords are securely hashed using **jBCrypt** before being stored.
-   **User Login:** Registered users can log in with their email and password. The system verifies credentials against the hashed passwords in the database.

✅ **Material Dashboard**
-   **View Materials:** After logging in, users are presented with a dashboard showing all uploaded materials in a sortable table.
-   **Upload New Materials:** Users can upload new files by providing a title, selecting a subject from a dynamic dropdown list, and choosing a file from their computer.
-   **Open Files:** Users can double-click any material in the table to open the associated file using their system's default application.

✅ **Interactive Features**
-   **Rating System:** Users can select a material from the table and give it a rating from 1 to 5. The average rating is calculated and displayed.
-   **Search and Filter:** The dashboard includes a search bar to filter materials by title and a dropdown menu to filter by subject, allowing for easy navigation.

✅ **Admin Functionality**
-   **Role-Based Access:** The application distinguishes between `USER` and `ADMIN` roles.
-   **Admin Panel:** A special "Admin Panel" button appears on the dashboard only for users logged in with the `ADMIN` role.
-   **Subject Management (CRUD):** From the admin panel, administrators can **Create**, **Read**, **Update**, and **Delete** subjects in the database, which dynamically updates the subject dropdowns for all users.

## Technology Stack

This project was built from the ground up to demonstrate fundamental Java principles.

| Component      | Technology                                                | Purpose                                           |
|----------------|-----------------------------------------------------------|---------------------------------------------------|
| **Language**   | Java 17                                                   | Core programming language                         |
| **User Interface**| Java Swing                                                | Native desktop GUI toolkit                        |
| **Database**   | PostgreSQL (Hosted on [Aiven](https://aiven.io/))         | Relational database for all data persistence      |
| **Build Tool** | Apache Maven                                              | Dependency management and project build automation|
| **DB Driver**  | PostgreSQL JDBC Driver                                    | Translator for communication between Java and PostgreSQL|
| **Security**   | jBCrypt                                                   | A trusted library for secure password hashing     |

## How It Works (Application Architecture)

The project follows a simple, layered architecture to ensure a clear separation of concerns:

-   **Presentation Layer (`RegistrationForm`, `LoginForm`, `DashboardForm`, `AdminForm`):** These are the Java Swing `JFrame` classes that the user sees and interacts with. Their only job is to display information and capture user input. They contain no business or database logic.
-   **Data Access Layer (DAO - `UserDAO`, `MaterialDAO`, `SubjectDAO`):** These "Data Access Object" classes are the dedicated workers that handle all communication with the database. They are the only classes that contain SQL code.
-   **Domain Objects (`User`, `Material`, `Subject`):** These are simple POJOs (Plain Old Java Objects) that act as data containers, holding information as it moves between the UI and the DAO layers.
-   **Database Manager (`DatabaseManager.java`):** A single utility class responsible for establishing the connection to the Aiven database using JDBC.

## Database Schema

The application relies on four main tables in the PostgreSQL database:
-   `users`: Stores user information, including `id`, `full_name`, `email`, hashed password, and `role`.
-   `subjects`: Stores a list of academic subjects, including `id` and `name`.
-   `materials`: The central table storing information about each uploaded file, including `title`, `file_path`, `average_rating`, and foreign keys linking to the `users` and `subjects` tables.
-   `ratings`: Stores each individual rating, linking a `user_id` and a `material_id` with a `score`.

## Getting Started (Setup Guide)

Follow these steps to get the application running on your local machine.

### Prerequisites
-   **Java Development Kit (JDK):** Version 17 or newer.
-   **Apache Maven:** Must be installed and configured on your system.
-   **Aiven Account:** A free account with a running PostgreSQL service.

### Database Setup
1.  Connect to your Aiven PostgreSQL database using a client like **DBeaver**.
2.  Execute the SQL scripts to create the `users`, `subjects`, `materials`, and `ratings` tables.
3.  Manually insert at least one user and change their `role` to `'ADMIN'` for testing purposes.
4.  Manually insert a few subjects into the `subjects` table so the dropdown menus will have options.

### Configuration
1.  Open the file `src/main/java/com/note0/simple/DatabaseManager.java`.
2.  Replace the placeholder values for `DB_URL`, `USER`, and `PASSWORD` with your actual Aiven credentials.
3.  Set the `CLOUDINARY_URL` environment variable before running the app. Format: `cloudinary://<api_key>:<api_secret>@<cloud_name>`
    - Windows PowerShell example:
      ```bash
      $env:CLOUDINARY_URL = "cloudinary://API_KEY:API_SECRET@CLOUD_NAME"
      mvn compile exec:java
      ```
    - Windows CMD example:
      ```bash
      set CLOUDINARY_URL=cloudinary://API_KEY:API_SECRET@CLOUD_NAME
      mvn compile exec:java
      ```
    - macOS/Linux example:
      ```bash
      export CLOUDINARY_URL="cloudinary://API_KEY:API_SECRET@CLOUD_NAME"
      mvn compile exec:java
      ```

### Build and Run
1.  Open a terminal or command prompt.
2.  Navigate to the root directory of the project (the folder containing `pom.xml`).
3.  Run the following Maven command:
    ```bash
    mvn compile exec:java
    ```
4.  Maven will compile all the files, and the application's login window will appear.

## How to Use the Application

1.  **Register:** Use the "Create New Account" button to register a new user.
2.  **Log In:** Log in with the credentials of a registered user.
3.  **Use the Dashboard:**
    *   View existing materials.
    *   Use the search and filter options to find specific materials.
    *   Double-click a material to open the file.
    *   Select a material and click "Rate Selected" to give it a rating.
    *   Fill out the form on the left and click "Upload" to add a new material.
4.  **Admin Functions:** If you log in as a user with the `ADMIN` role, an "Admin Panel" button will appear. Click it to open the subject management window where you can add, edit, or delete subjects.
