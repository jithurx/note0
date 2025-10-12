# Codebase Analysis Report

## Project Overview

This project is a **hybrid note-sharing application** that combines a Node.js backend API with a Java Spring Boot frontend. The application appears to be designed for academic material sharing, particularly for KTU (Kerala Technological University) students, with features for user authentication, file uploads, and material rating.

## Architecture Analysis

### 1. Backend (Node.js API)
- **Framework**: Express.js
- **Database**: PostgreSQL (configured via environment variables)
- **Authentication**: JWT-based with bcrypt password hashing
- **File Upload**: Multer middleware for handling file uploads
- **Port**: 3000 (configurable via environment)

### 2. Frontend (Java Spring Boot)
- **Framework**: Spring Boot 3.3.0
- **Java Version**: 17
- **Template Engine**: Thymeleaf
- **Database**: H2 (in-memory, for demo purposes)
- **Features**: Basic authentication with in-memory user storage

### 3. Database Schema
The project has two different database configurations:

#### MySQL Schema (from database/schema.sql)
```sql
- notes_app database
- notes table (id, title, content, created_at)
- files table (id, filename, filepath, uploaded_at)
```

#### PostgreSQL Schema (inferred from Node.js controllers)
Based on the controller code, the actual application uses:
- `users` table (id, full_name, email, password_hash, college_name, branch, semester, role, created_at)
- `materials` table (id, title, description, file_type, file_path, module_number, uploader_id, subject_id, avg_rating, upload_date, status)
- `subjects` table (id, name)
- `ratings` table (material_id, user_id, score)

## Component Analysis

### Node.js Backend Components

#### Controllers
1. **AuthController** (`src/api/controllers/authController.js`)
   - User registration with bcrypt password hashing
   - User login with JWT token generation
   - Input validation for required fields
   - Error handling for duplicate emails

2. **UserController** (`src/api/controllers/userController.js`)
   - Protected route for user profile retrieval
   - Excludes password hash from response

3. **MaterialController** (`src/api/controllers/materialController.js`)
   - File upload functionality with multer
   - Material listing with joins to subjects and users
   - Rating system with upsert logic
   - File type validation (images and documents)

4. **AdminController** (`src/api/controllers/adminController.js`)
   - User management for administrators
   - User verification functionality

#### Middleware
1. **AuthMiddleware** (`src/api/middleware/authMiddleware.js`)
   - JWT token verification
   - Role-based access control (admin vs regular users)
   - Bearer token format validation

2. **UploadMiddleware** (`src/api/middleware/uploadMiddleware.js`)
   - Multer configuration for file uploads
   - File size limit (10MB)
   - File type filtering (jpeg, jpg, png, gif, pdf, doc, docx, ppt, pptx)
   - Unique filename generation

#### Routes
- `/api/auth/*` - Authentication endpoints
- `/api/users/*` - User profile endpoints
- `/api/materials/*` - Material management endpoints
- `/api/admin/*` - Admin-only endpoints

### Java Spring Boot Frontend Components

#### Controllers
1. **HomeController** (`frontend/demo/src/main/java/com/example/demo/controller/HomeController.java`)
   - Basic home page routing
   - Returns Thymeleaf templates

2. **AuthController** (`frontend/demo/src/main/java/com/example/demo/controller/AuthController.java`)
   - REST endpoints for registration and login
   - Uses DTOs for data transfer
   - Basic validation with @Valid annotation

#### Models & DTOs
1. **User Model** (`frontend/demo/src/main/java/com/example/demo/model/User.java`)
   - Simple POJO with username and password
   - Basic getters and setters

2. **UserDTO** (`frontend/demo/src/main/java/com/example/demo/dto/UserDTO.java`)
   - Validation annotations (@NotBlank)
   - Data transfer object for API requests

#### Services
1. **AuthService** (`frontend/demo/src/main/java/com/example/demo/service/AuthService.java`)
   - In-memory user storage using HashMap
   - Basic registration and login logic
   - No password hashing (security concern)

#### Templates
- `index.html` - Basic welcome page
- `login.html` - Login form with Thymeleaf
- `register.html` - Registration form
- `dashboard.html` - Post-login dashboard

## Key Features

### 1. Authentication System
- **Backend**: Secure JWT-based authentication with bcrypt password hashing
- **Frontend**: Basic in-memory authentication (demo purposes)
- **Roles**: Admin and regular user roles with different access levels

### 2. File Upload System
- Support for multiple file types (documents and images)
- File size limitations (10MB)
- Unique filename generation to prevent conflicts
- File metadata storage in database

### 3. Material Management
- Upload academic materials with metadata
- Subject-based organization
- Module number tracking
- Rating system (1-5 scale)
- Average rating calculation

### 4. User Management
- User registration with academic details (college, branch, semester)
- Admin user verification system
- Profile management

## Dependencies

### Node.js Backend
```json
{
  "bcryptjs": "^3.0.2",      // Password hashing
  "dotenv": "^16.5.0",       // Environment variables
  "express": "^5.1.0",       // Web framework
  "jsonwebtoken": "^9.0.2",  // JWT authentication
  "multer": "^2.0.1",        // File upload handling
  "pg": "^8.16.0"            // PostgreSQL client
}
```

### Java Spring Boot Frontend
```xml
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-validation
- spring-boot-starter-thymeleaf
- h2database (runtime)
```

## Configuration

### Environment Variables (Node.js)
- `DB_USER` - Database username
- `DB_HOST` - Database host
- `DB_DATABASE` - Database name
- `DB_PASSWORD` - Database password
- `DB_PORT` - Database port
- `JWT_SECRET` - JWT signing secret
- `PORT` - Server port (default: 3000)

### Application Properties (Java)
- Database auto-configuration disabled
- H2 database for demo purposes

## API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login

### User Management
- `GET /api/users/me` - Get user profile (protected)

### Materials
- `POST /api/materials/upload` - Upload material (protected)
- `GET /api/materials` - Get all materials (public)
- `POST /api/materials/:id/rate` - Rate material (protected)

### Admin
- `GET /api/admin/users` - Get all users (admin only)
- `PUT /api/admin/users/:id/verify` - Verify user (admin only)

## Security Analysis

### Strengths
- JWT-based authentication in backend
- Password hashing with bcrypt
- Role-based access control
- File type validation
- Input validation and sanitization

### Concerns
- Frontend uses in-memory storage without password hashing
- No HTTPS configuration visible
- File upload directory not secured
- No rate limiting implemented
- Missing CORS configuration

## Database Inconsistencies

The project has conflicting database configurations:
1. **MySQL schema** in `database/schema.sql` with basic notes/files tables
2. **PostgreSQL usage** in Node.js controllers with complex user/material schema
3. **H2 database** in Java frontend for demo purposes

This suggests the project may be in transition or has multiple development phases.

## Testing

- HTTP test file provided (`test.http`) with comprehensive API testing examples
- No automated test suites found
- Manual testing endpoints documented

## Recommendations

1. **Database Consistency**: Resolve the MySQL/PostgreSQL discrepancy
2. **Security**: Implement password hashing in Java frontend
3. **Configuration**: Add CORS and HTTPS configuration
4. **Testing**: Implement automated test suites
5. **Documentation**: Add API documentation (Swagger/OpenAPI)
6. **Error Handling**: Standardize error responses across all endpoints
7. **File Security**: Implement file access controls and virus scanning
8. **Rate Limiting**: Add rate limiting to prevent abuse

## Conclusion

This is a well-structured academic material sharing application with a solid Node.js backend and a basic Java Spring Boot frontend. The backend demonstrates good security practices with JWT authentication and proper password hashing, while the frontend appears to be a demo/prototype implementation. The project shows potential but needs consistency improvements and additional security measures for production use.
