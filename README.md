# Storix

Storix is a lightweight object-storage backend built with Spring Boot.

The project is being developed incrementally to build a production-oriented backend for file storage, metadata management, authentication, asynchronous processing, caching, and scalable storage.

## Overview

Storix allows users to upload and manage files while separating physical file storage from file metadata.

The current implementation uses local filesystem storage and PostgreSQL. As development progresses, the system will introduce authentication, caching, asynchronous events, cloud-compatible object storage, containerization, testing, monitoring, and CI/CD.

## Current Features

- File upload
- File download
- File metadata management
- File replacement
- File deletion
- File size validation
- File name validation
- Content type validation
- UUID-based stored filenames
- Configurable storage location
- Configurable maximum file size
- Path traversal protection
- Custom application exceptions
- Global exception handling
- DTO-based API responses
- PostgreSQL metadata persistence
- Storage abstraction using `StorageService`

## Current Tech Stack

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Hibernate
- PostgreSQL
- Maven
- Lombok
- JUnit
- Spring Boot Test
- Postman
- Git
- GitHub

## Architecture

```text
                         Client
                           |
                           v
                      REST API
                           |
                           v
                     FileController
                           |
                           v
                      FileService
                     /          \
                    /            \
                   v              v
             Validation       Repository
                   |              |
                   v              v
            StorageService     PostgreSQL
                   |
                   v
          LocalStorageService
                   |
                   v
            Local File System
````

The storage layer is abstracted through `StorageService`, allowing the physical storage implementation to be changed without heavily modifying the business logic.

## Project Structure

```text
src/
└── main/
    └── java/
        └── com/
            └── storix/
                ├── controller/
                ├── dto/
                ├── exception/
                ├── file/
                ├── repository/
                └── storage/
```

## API Endpoints

| Method | Endpoint                   | Description       |
| ------ | -------------------------- | ----------------- |
| POST   | `/api/files`               | Upload a file     |
| GET    | `/api/files/{id}`          | Get file metadata |
| GET    | `/api/files/{id}/download` | Download a file   |
| PUT    | `/api/files/{id}`          | Replace a file    |
| DELETE | `/api/files/{id}`          | Delete a file     |

## File Storage

Files are currently stored on the local filesystem.

The original filename is stored as metadata, while the physical file receives a UUID-based name.

```text
Original filename:
report.pdf

Stored filename:
550e8400-e29b-41d4-a716-446655440000.pdf
```

This prevents filename collisions and separates user-facing metadata from the physical storage representation.

## Database

PostgreSQL stores file metadata such as:

* File ID
* Original filename
* Stored filename
* Content type
* File size

The actual file contents are currently stored separately on the local filesystem.

## Validation

Storix validates uploaded files before they reach the storage layer.

Current validations include:

* Empty file detection
* Missing filename detection
* Maximum file-size validation
* Missing content-type detection

Example configuration:

```yaml
storix:
  storage:
    location: storage
    max-file-size: 10KB
```

## Exception Handling

Storix uses custom exceptions and a centralized global exception handler.

Current custom exceptions include:

* `InvalidFileException`
* `FileNotFoundException`
* `StorageException`

The global exception handler converts application exceptions into consistent HTTP responses.

Example:

```json
{
  "status": 404,
  "code": "FILE_NOT_FOUND",
  "message": "File not found"
}
```

## Security

Current security-related features include:

* Path traversal protection
* UUID-based stored filenames
* File validation
* Centralized exception handling
* Separation between database entities and API DTOs

Authentication and authorization will be introduced in a later phase.

## Configuration

Example:

```yaml
spring:
  application:
    name: storix

  datasource:
    url: jdbc:postgresql://localhost:5432/storix
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

server:
  port: 8080

storix:
  storage:
    location: storage
    max-file-size: 10KB
```

Sensitive credentials should be provided through environment variables and must not be committed to the repository.

## Running Locally

### Requirements

* Java 21+
* Maven
* PostgreSQL
* Git

### Clone

```bash
git clone https://github.com/abdulwalidal/Storix.git
cd Storix
```

### Database

Create a PostgreSQL database:

```sql
CREATE DATABASE storix;
```

Configure:

```text
DB_USERNAME=your_username
DB_PASSWORD=your_password
```

### Run

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Windows:

```bash
mvnw.cmd spring-boot:run
```

The application will run on:

```text
http://localhost:8080
```

## Development Roadmap

Storix will be developed in multiple phases.

### Phase 1 — Core File Storage

* [x] File upload
* [x] File download
* [x] File replacement
* [x] File deletion
* [x] Local filesystem storage
* [x] PostgreSQL metadata
* [x] Validation
* [x] Exception handling
* [x] DTO-based responses

### Phase 2 — API & Code Quality

* [ ] Complete DTO architecture
* [ ] Service-layer improvements
* [ ] Repository improvements
* [ ] Unit tests
* [ ] Integration tests
* [ ] OpenAPI / Swagger documentation

### Phase 3 — Users & Security

* [ ] User accounts
* [ ] Spring Security
* [ ] JWT authentication
* [ ] User registration/login
* [ ] Password hashing
* [ ] File ownership
* [ ] Authorization
* [ ] Per-user file access

### Phase 4 — Caching & Performance

* [ ] Redis
* [ ] Metadata caching
* [ ] Rate limiting
* [ ] Performance improvements
* [ ] Cache invalidation strategies

### Phase 5 — Asynchronous Processing

* [ ] Apache Kafka
* [ ] File upload events
* [ ] File deletion events
* [ ] Asynchronous background processing
* [ ] Event-driven architecture

### Phase 6 — Object Storage

* [ ] Storage provider abstraction improvements
* [ ] MinIO
* [ ] S3-compatible storage
* [ ] Configurable storage providers
* [ ] Multipart uploads for large files

### Phase 7 — Containerization & Deployment

* [ ] Docker
* [ ] Docker Compose
* [ ] Containerized PostgreSQL
* [ ] Containerized Redis
* [ ] Containerized Kafka
* [ ] Production configuration
* [ ] Deployment

### Phase 8 — Observability

* [ ] Spring Boot Actuator
* [ ] Application metrics
* [ ] Prometheus
* [ ] Grafana
* [ ] Structured logging
* [ ] Health checks

### Phase 9 — CI/CD

* [ ] GitHub Actions
* [ ] Automated tests
* [ ] Automated builds
* [ ] Docker image builds
* [ ] Deployment pipeline

## Planned Architecture

As the project evolves, the architecture will move toward:

```text
                         Client
                           |
                           v
                      REST API
                           |
                           v
                    Spring Security
                           |
                           v
                     Controllers
                           |
                           v
                      FileService
                     /    |     \
                    /     |      \
                   v      v       v
              PostgreSQL Redis   Storage
                         |
                         v
                       Cache

              File Events
                   |
                   v
                 Kafka
                   |
          +--------+--------+
          |        |        |
          v        v        v
      Processing  Audit   Workers
```

The exact architecture will evolve as each component is introduced.

## Testing

The project uses Spring Boot testing tools and JUnit for automated testing.

Postman is currently used for manual API testing.

Integration testing with containerized dependencies will be introduced later using Testcontainers.

## Future Goals

The long-term goal is to turn Storix from a simple local file-storage API into a more complete object-storage backend with:

* Multi-user support
* Authentication and authorization
* Scalable object storage
* Caching
* Event-driven processing
* Asynchronous workers
* Containerized infrastructure
* Monitoring
* Automated CI/CD
* Production-ready configuration

## Project Status

Storix is currently under active development.

Features are being implemented incrementally, with each phase introducing new backend concepts and infrastructure.

The project is intended as a practical exploration of backend engineering, distributed systems, storage architecture, security, performance, and deployment.

````
