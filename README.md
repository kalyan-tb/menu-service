# Menu Service

A Spring Boot microservice for managing restaurant menus and items.

## Tech Stack

- Java 17
- Spring Boot 3.4.8-SNAPSHOT
- MySQL 8.0
- Redis 7
- Docker & Docker Compose
- Maven

## Prerequisites

- Java 17 or higher
- Docker and Docker Compose
- Maven 3.6 or higher

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/kalyan-tb/menu-service.git
cd menu-service
```

### 2. Start Infrastructure Services

First, if you have any existing containers from previous runs, clean them up:

```bash
# Stop and remove containers, networks, and volumes specific to this project
docker compose down -v
```

The application requires MySQL and Redis. Start them using Docker Compose:

```bash
docker-compose up -d
```

This will start:
- MySQL on port 3306
- Redis on port 6379

### 3. Database Setup

The database will be automatically created and initialized by Docker Compose with:
- Database name: menu_service
- Username: root
- Password: root

The following operations will be performed automatically when the MySQL container starts:
1. Database creation using the provided credentials
Note: If you need to reinitialize the database:
```bash
# Stop containers and remove volumes
docker-compose down -v

# Start services again
docker-compose up -d
```

### 4. Build and Run

#### Option 1: Local Development with Docker Dependencies (Recommended for Development)

This option runs MySQL and Redis in Docker while running the application locally with Maven:

```bash
# Start only MySQL and Redis
docker compose -f docker-compose.yml up -d

# Wait for services to be healthy
docker compose -f docker-compose.yml ps

# Run the application locally
./mvnw spring-boot:run -Pdev
```


#### Option 3: Using Maven (Development)

```bash
./mvnw clean install
./mvnw spring-boot:run -Pdev
```


## Available Profiles

- `dev` - Development environment (default)
- `docker` - Docker environment
- `uat` - UAT environment
- `prod` - Production environment

To run with a specific profile:
```bash
./mvnw spring-boot:run -P <profile-name>
```

## Configuration

### Database Configuration

The application uses HikariCP connection pool with the following default settings:
- Connection Timeout: 30 seconds
- Idle Timeout: 10 minutes
- Max Lifetime: 30 minutes
- Minimum Idle: 10 connections
- Maximum Pool Size: 10 connections

### Redis Configuration

Redis is configured with:
- Host: localhost (configurable)
- Port: 6379
- Timeout: 60 seconds
- Database: 0

## API Documentation

OpenAPI documentation is available at:
- `http://localhost:8080/swagger-ui.html`

## Monitoring

The application exposes the following actuator endpoints under `/actuator`:
- Health check: `/actuator/health`
- Metrics: `/actuator/metrics`
- Info: `/actuator/info`

## Development

### Adding New Features

1. Create necessary entity classes in `com.swiggy.menu.entityl`
2. Add repository interfaces in `com.swiggy.menu.repository`
3. Implement service layer in `com.swiggy.menu.service`
4. Create DTOs in `com.swiggy.menu.dto`
5. Add controllers in `com.swiggy.menu.controller`

### Running Tests

```bash
./mvnw test
```
