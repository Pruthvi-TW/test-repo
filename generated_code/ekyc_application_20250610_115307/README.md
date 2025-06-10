# eKYC Application Project Structure

Generated on: 2025-06-10T11:53:07.997505

## Project Structure

ekyc-application/
├── pom.xml (parent)
├── README.md
├── docker-compose.yml
├── ekyc-service/ (main service)
│   ├── pom.xml
│   ├── src/main/java/com/ekyc/
│   └── src/main/resources/
└── mock-uidai-service/ (mock service)
    ├── pom.xml
    ├── src/main/java/com/mockuidai/
    └── src/main/resources/


## Description
Complete eKYC application with main service and mock UIDAI service

## Setup Instructions

1. Prerequisites: Java 21, Maven 3.6+, PostgreSQL 12+
2. Setup database: Create 'ekyc_db' database
3. Build: mvn clean install
4. Run main service: cd ekyc-service && mvn spring-boot:run
5. Run mock service: cd mock-uidai-service && mvn spring-boot:run
6. Access Swagger: http://localhost:8080/swagger-ui.html
7. Mock service: http://localhost:8082/swagger-ui.html


## Files Generated
14 files created in this project.
