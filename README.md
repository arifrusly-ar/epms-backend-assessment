# EPMS Backend Assessment

## Project Overview

EPMS (Engineering Project Management System) is a RESTful backend application developed using Java and Spring Boot.

The project demonstrates REST API development, database transaction management, pagination, JWT authentication, request and response logging, third-party API integration, and event-driven communication using Apache Kafka.

## Technology Stack

- Java 17
- Spring Boot
- Spring Data JPA / Hibernate
- Spring Security with JWT
- Microsoft SQL Server
- Apache Kafka
- Docker / Docker Compose (Kafka infrastructure)
- Spring RestClient
- Maven
- Postman

## Features

### 1. Project Management
- Create and update projects
- Retrieve project information
- Manage project status

### 2. Document Management
- Create and update documents
- Submit documents for approval
- Approve and reject documents
- Manage document revisions
- Optimistic locking
- Pagination and status filtering

### 3. Authentication and Authorization
- User registration and login
- JWT-based authentication
- Role-based access control

### 4. Database Transaction Management
- `@Transactional` for database operations
- `@Transactional(readOnly = true)` for retrieval operations

### 5. Pagination
- Document listing with pagination
- Maximum 10 records per page
- Optional document status filtering

### 6. Request and Response Logging
- HTTP request and response logging using a servlet filter
- Application logs stored in `logs/epms-application.log`

### 7. Third-Party API Integration
- External API integration using Spring RestClient
- Endpoint: `GET /api/integrations/users/1`
- External service: JSONPlaceholder

### 8. Apache Kafka Integration

Apache Kafka is used to support asynchronous, event-driven communication between Spring Boot services.

The EPMS application includes two services:

**EPMS Main Service (Producer)**
- Handles project and document management
- Publishes document-related events to Kafka
- Uses the `document-events` topic

**EPMS Notification Service (Consumer)**
- Runs as a separate Spring Boot application
- Consumes document events from Kafka
- Processes received events for notification-related workflows

Example event:

```json
{
  "eventType": "DOCUMENT_SUBMITTED",
  "documentId": 1,
  "projectId": 1
}
```

The event above is an illustrative example; the actual event payload depends on the producer implementation.

#### Kafka Architecture

```text
EPMS Main Service
       |
       | Publish Document Event
       v
   Apache Kafka
   document-events
       |
       | Consume Event
       v
EPMS Notification Service
```

This architecture demonstrates asynchronous communication and separation of responsibilities between services.

## Database Configuration

The application uses Microsoft SQL Server.

Database name: `TESTDB`

Configure the database credentials using environment variables:

- `DB_USERNAME`
- `DB_PASSWORD`

Ensure SQL Server is running and the database exists before starting the application.

## Running the Application

### 1. Prerequisites

- Java 17
- Maven
- Microsoft SQL Server
- Docker Desktop
- Apache Kafka
- Postman

### 2. Start Kafka

Start the Kafka infrastructure using the Docker Compose configuration supplied with the project.

For example, if a `docker-compose.yml` file is provided:

```bash
docker compose up -d
```

Ensure the Kafka broker is running and the `document-events` topic is available.

### 3. Start EPMS Main Service

Configure the required database and Kafka connection settings.

Run:

```bash
mvn spring-boot:run
```

Application base URL:

`http://localhost:8080`

### 4. Start EPMS Notification Service

Start the separate Notification Service Spring Boot application with the appropriate Kafka consumer configuration.

The Notification Service listens for events published to the `document-events` topic.

## API Examples

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/projects` | Create project |
| POST | `/api/projects/{projectId}/documents` | Create document |
| GET | `/api/projects/{projectId}/documents?page=0&size=10` | Paginated document list |
| POST | `/api/projects/{projectId}/documents/{documentId}/submit` | Submit document |
| GET | `/api/integrations/users/1` | Retrieve external user |

Additional endpoints are available in the Postman Collection.

## Postman Collection

Import the Postman Collection from the `postman` folder.

1. Open Postman.
2. Import the collection JSON file.
3. Configure collection variables.
4. Login to obtain a JWT token.
5. Execute protected APIs using Bearer Token authorization.

## Notes

This project was developed as part of a backend technical assessment to demonstrate Java Spring Boot development, RESTful APIs, database operations, and event-driven microservice communication using Apache Kafka.