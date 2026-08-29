# Storix

**Storix** is a Spring Boot backend for secure file storage and metadata management. It provides REST APIs for uploading, downloading, updating, deleting, and retrieving file metadata while associating files with authenticated users.

The project is being built as a practical backend system to explore **Spring Boot, REST APIs, PostgreSQL, Spring Security, Redis caching, file storage, validation, and clean backend architecture**.

---

## Features

### File Management

* Upload files
* Download files
* Retrieve file metadata by ID
* Retrieve all files belonging to the authenticated user
* Update/replace existing files
* Delete files
* Generate unique stored filenames using UUIDs
* Store actual files on the local filesystem
* Store file metadata in PostgreSQL

### File Validation

Storix validates uploaded files for:

* Empty files
* Missing filenames
* Maximum file size
* Missing content types
* Unsupported file types

Currently supported content types:

* `application/pdf`
* `image/png`
* `image/jpeg`
* `text/plain`

### Authentication & Authorization

Storix uses **Spring Security** for authentication and authorization.

* HTTP Basic authentication
* User accounts stored in PostgreSQL
* Passwords securely encoded
* Role-based authorization
* Users can only access their own files

### Redis Caching

Storix uses **Redis with Spring Cache** to improve file metadata retrieval performance.

The metadata endpoint uses:

```java
@Cacheable(value = "files", key = "#id")
```

The caching flow is:

```text
Client
  ↓
FileController
  ↓
FileService
  ↓
Redis Cache
  ↓
Cache Hit ─────────→ Return cached metadata
  ↓
Cache Miss
  ↓
PostgreSQL
  ↓
Store result in Redis
  ↓
Return metadata
```

Redis is used as a cache rather than as the primary source of file metadata.

### Exception Handling

Storix includes centralized exception handling using `@ControllerAdvice`.

Handled errors include:

* File not found
* Invalid file
* Missing multipart file
* Multipart request errors
* Storage errors

API errors are returned through a consistent `ErrorResponse` structure.

---

## Architecture

Storix follows a layered backend architecture:

```text
                    ┌──────────────┐
                    │    Client    │
                    │ Postman/UI   │
                    └──────┬───────┘
                           │
                           ▼
                 ┌──────────────────┐
                 │  REST Controller  │
                 └────────┬─────────┘
                          │
                          ▼
                 ┌──────────────────┐
                 │   FileService    │
                 └───────┬─────┬────┘
                         │     │
              ┌──────────┘     └──────────┐
              ▼                           ▼
       ┌─────────────┐             ┌─────────────┐
       │    Redis    │             │ PostgreSQL  │
       │    Cache    │             │  Metadata   │
       └─────────────┘             └─────────────┘
                                             │
                                             ▼
                                    ┌────────────────┐
                                    │ Local Storage  │
                                    │ Actual Files   │
                                    └────────────────┘
```

A more detailed Redis architecture diagram is available in:

`docs/redis-architecture.png`

---

##  Tech Stack

| Technology      | Purpose                        |
| --------------- | ------------------------------ |
| Java 21         | Programming language           |
| Spring Boot     | Backend framework              |
| Spring Web      | REST APIs                      |
| Spring Data JPA | Database access                |
| PostgreSQL      | File metadata & user data      |
| Spring Security | Authentication & authorization |
| Redis           | Caching                        |
| Spring Cache    | Cache abstraction              |
| Maven           | Dependency management          |
| Lombok          | Boilerplate reduction          |
| Docker          | Running Redis locally          |

---

##  Project Structure

```text
src/main/java/com/storix
│
├── config
│   ├── CacheConfig.java
│   ├── RedisConfig.java
│   └── SecurityConfig.java
│
├── controller
│   ├── FileController.java
│   ├── RedisController.java
│   └── UserController.java
│
├── dto
│   ├── FileResponse.java
│   ├── UserRequest.java
│   └── UserResponse.java
│
├── exception
│   ├── ErrorResponse.java
│   ├── FileNotFoundException.java
│   ├── GlobalExceptionHandler.java
│   ├── InvalidFileException.java
│   └── StorageException.java
│
├── file
│   ├── FileMetadata.java
│   └── FileService.java
│
├── repository
│   ├── FileMetadataRepository.java
│   └── UserRepository.java
│
├── service
│   ├── CustomUserDetailsService.java
│   ├── UserService.java
│   └── UserServiceImpl.java
│
├── storage
│   ├── LocalStorageService.java
│   └── StorageService.java
│
└── user
    ├── Role.java
    └── User.java
```

---

##  API Endpoints

### User

| Method | Endpoint | Description   |
| ------ | -------- | ------------- |
| `POST` | `/users` | Create a user |

### Files

| Method   | Endpoint               | Description                              |
| -------- | ---------------------- | ---------------------------------------- |
| `POST`   | `/files`               | Upload a file                            |
| `GET`    | `/files`               | Get all files for the authenticated user |
| `GET`    | `/files/{id}`          | Get file metadata                        |
| `GET`    | `/files/{id}/download` | Download a file                          |
| `PUT`    | `/files/{id}`          | Replace/update a file                    |
| `DELETE` | `/files/{id}`          | Delete a file                            |

All file endpoints require authentication.

---

##  Data Storage

Storix separates **file metadata** from the **actual file contents**.

### PostgreSQL

PostgreSQL stores metadata such as:

```text
id
originalFileName
storedFileName
contentType
size
user
```

### Local Filesystem

The actual uploaded file is stored separately using a generated unique filename.

For example:

```text
Original:
report.pdf

Stored:
550e8400-e29b-41d4-a716-446655440000.pdf
```

This prevents filename collisions and avoids directly exposing the original filename as the storage identifier.

---

## Redis Caching

File metadata retrieval is cached using Spring Cache backed by Redis.

### Cache Miss

```text
GET /files/1
      ↓
Redis
      ↓
MISS
      ↓
PostgreSQL
      ↓
FileResponse
      ↓
Redis
      ↓
Client
```

### Cache Hit

```text
GET /files/1
      ↓
Redis
      ↓
HIT
      ↓
FileResponse
      ↓
Client
```

The purpose is to avoid repeatedly querying PostgreSQL for frequently requested metadata.

---

##  Security Flow

```text
Client
   │
   │ Credentials
   ▼
Spring Security
   │
   ▼
CustomUserDetailsService
   │
   ▼
PostgreSQL
   │
   ▼
Authenticated User
   │
   ▼
Authorization
   │
   ▼
FileController
   │
   ▼
FileService
```

File queries use the authenticated user's email to ensure users only access their own files.

---

##  Running Locally

### Prerequisites

Make sure you have:

* Java 21
* Maven
* PostgreSQL
* Docker
* Redis

### Start Redis

Redis can be run locally through Docker.

```bash
docker run --name storix-redis -p 6379:6379 -d redis
```

Verify Redis is running:

```bash
docker ps
```

You can also connect using the Redis CLI:

```bash
redis-cli
```

### Configure the Application

Configure your PostgreSQL and Redis connection details in:

```text
src/main/resources/application.yaml
```

### Run the Application

Using Maven:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The application will run on:

```text
http://localhost:8080
```

---

## Testing

Testing is planned as the next major development stage.

The project will include:

* Unit tests
* Service-layer tests
* Controller tests
* Integration tests
* Database-related tests
* Redis/cache-related tests

---

##  Roadmap

### Completed

* [x] Spring Boot project setup
* [x] REST API structure
* [x] PostgreSQL integration
* [x] JPA entities and repositories
* [x] Local file storage
* [x] File upload
* [x] File download
* [x] File metadata retrieval
* [x] File listing
* [x] File update/replace
* [x] File deletion
* [x] File validation
* [x] User accounts
* [x] Spring Security
* [x] Authentication
* [x] Authorization
* [x] Global exception handling
* [x] Redis integration
* [x] Spring Cache
* [x] `@Cacheable` file metadata caching
* [x] Redis cache testing
* [x] Redis architecture documentation

### Upcoming

* [ ] Improve cache key design
* [ ] Add cache eviction/update handling
* [ ] Configure production-friendly cache TTL
* [ ] Unit testing
* [ ] Integration testing
* [ ] Spring Boot Actuator
* [ ] Application metrics
* [ ] Improved observability
* [ ] Complete Docker setup
* [ ] Production deployment
* [ ] Explore S3-style object storage architecture

---

##  Project Goal

Storix is being developed as a practical backend project to understand how real-world backend systems are designed and how different components work together.

The long-term goal is to evolve Storix from a simple local file-storage API into a more production-oriented **object storage backend**, while learning concepts such as:

* REST API design
* Authentication & authorization
* Database design
* Caching
* Distributed systems concepts
* Testing
* Observability
* Containerization
* Object storage architecture
* Scalability

---

##  Documentation

Architecture documentation and diagrams are available in:

```text
docs/
```

Current documentation includes:

```text
docs/redis-architecture.png
```

---

##  Status

Storix is an **actively developed learning project** focused on building practical backend engineering skills with Java and Spring Boot.

The project is currently focused on improving the backend architecture, testing, performance, and production readiness.
