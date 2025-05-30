# Fridge Tracker

A Spring Boot application for tracking items in your fridge, helping you reduce food waste and manage your groceries efficiently.

🔗 **Live Demo**: [https://fridge-tracker.brkovic.dev](https://fridge-tracker.brkovic.dev)

## Features

- Track items in your fridge
- Monitor expiration dates
- Multiple fridges support
- User authentication and authorization
- RESTful API with Swagger documentation

## Prerequisites

Before running the application, make sure you have the following installed:

- Java 21 or higher
- Maven
- PostgreSQL (for local setup)
- Docker and Docker Compose (for containerized setup)

## Local Development Setup

### Linux/macOS

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd fridge-tracker
   ```

2. Make the Maven wrapper executable:
   ```bash
   chmod +x mvnw
   ```

3. Build the application:
   ```bash
   ./mvnw clean install
   ```

4. Start the application:
   ```bash
   ./mvnw spring-boot:run
   ```

### Windows

1. Clone the repository:
   ```powershell
   git clone <repository-url>
   cd fridge-tracker
   ```

2. Build the application:
   ```powershell
   .\mvnw.cmd clean install
   ```

3. Start the application:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

The application will be available at:
- Application: `http://localhost:9000`
- API Documentation: `http://localhost:9000/swagger-ui/index.html`

## Docker Setup

### Using Docker Compose (Recommended)

1. Build and start the containers:
   ```bash
   docker-compose up -d
   ```

2. The application will be available at `http://localhost:9000`

To stop the containers:
```bash
docker-compose down
```

### Using Docker Directly

1. Build the image:
   ```bash
   docker build -t fridge-tracker .
   ```

2. Run the container:
   ```bash
   docker run -p 9000:9000 fridge-tracker
   ```

3. The application will be available at `http://localhost:9000`

To stop the container:
```bash
docker stop <container-id>
```