# Sazzler Order Service

## Overview
Manages customer orders, integrates with other microservices, and supports future Kafka event streaming.

## Features
- Create, update, and retrieve orders
- Database integration (MongoDB recommended)
- Eureka service registration
- Docker support
- (Planned) Kafka integration for event-driven architecture

## Setup
1. Java 21+
2. Gradle
3. Configure MongoDB URI in `application.yaml`
4. Set Eureka URL

## Build & Run
```bash
./gradlew build
./gradlew bootRun
```

## Configuration
- MongoDB: Set URI and database name
- Eureka: Set service registry URL
- Kafka: (future) Set broker address

## API Endpoints
- `/orders` - Manage orders

## Docker
- Build: `docker build -t sazzler-order-service .`
- Run: `docker run -p 8082:8082 sazzler-order-service`

## Troubleshooting
- Ensure MongoDB is running and accessible
- Check Eureka registration
- Validate API endpoints

