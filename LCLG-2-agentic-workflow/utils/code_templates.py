"""
Code templates for different technology stacks and file types.
"""
from typing import Dict, Any

class CodeTemplates:
    """Manages code templates for different technologies and patterns."""
    
    @staticmethod
    def get_template(template_name: str, context: Dict[str, Any]) -> str:
        """Get a code template with context substitution."""
        
        templates = {
            # Java Templates
            'spring_boot_main': CodeTemplates._spring_boot_main_template,
            'jpa_entity': CodeTemplates._jpa_entity_template,
            'spring_repository': CodeTemplates._spring_repository_template,
            'spring_service': CodeTemplates._spring_service_template,
            'spring_controller': CodeTemplates._spring_controller_template,
            'maven_pom': CodeTemplates._maven_pom_template,
            
            # Go Templates
            'gin_main': CodeTemplates._gin_main_template,
            'go_mod': CodeTemplates._go_mod_template,
            # Note: gin_handler and go_model templates not implemented yet
            
            # Node.js Templates (not implemented yet)
            # 'express_app': CodeTemplates._express_app_template,
            # 'package_json': CodeTemplates._package_json_template,
            # 'express_controller': CodeTemplates._express_controller_template,

            # Python Templates (not implemented yet)
            # 'fastapi_main': CodeTemplates._fastapi_main_template,
            # 'django_model': CodeTemplates._django_model_template,
            # 'requirements_txt': CodeTemplates._requirements_txt_template,

            # .NET Templates (not implemented yet)
            # 'dotnet_program': CodeTemplates._dotnet_program_template,
            # 'dotnet_controller': CodeTemplates._dotnet_controller_template,
            # 'dotnet_solution': CodeTemplates._dotnet_solution_template,

            # Configuration Templates (not implemented yet)
            # 'application_yml': CodeTemplates._application_yml_template,
            # 'dockerfile': CodeTemplates._dockerfile_template,
            # 'docker_compose': CodeTemplates._docker_compose_template,
            # 'gitignore': CodeTemplates._gitignore_template,
            # 'readme': CodeTemplates._readme_template
        }
        
        template_func = templates.get(template_name)
        print(f"DEBUG: Looking for template '{template_name}', found: {template_func}")
        if template_func:
            try:
                return template_func(context)
            except Exception as e:
                print(f"DEBUG: Error calling template function: {e}")
                raise
        else:
            return f"// Template '{template_name}' not found\n"
    
    # Java Templates
    @staticmethod
    def _spring_boot_main_template(context: Dict[str, Any]) -> str:
        package = context.get('package', 'com.example')
        class_name = context.get('class_name', 'Application')
        
        return f"""package {package};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for {context.get('app_name', 'Application')}.
 * 
 * This class serves as the entry point for the Spring Boot application.
 * It enables JPA repositories and transaction management.
 */
@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
public class {class_name} {{

    /**
     * Main method to start the Spring Boot application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {{
        SpringApplication.run({class_name}.class, args);
    }}
}}
"""
    
    @staticmethod
    def _jpa_entity_template(context: Dict[str, Any]) -> str:
        package = context.get('package', 'com.example')
        entity_name = context.get('entity_name', 'Entity')
        table_name = context.get('table_name', entity_name.lower())
        
        return f"""package {package}.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * {entity_name} entity representing the {table_name} table.
 * 
 * This entity follows JPA best practices with proper validation,
 * auditing fields, and immutable design patterns.
 */
@Entity
@Table(name = "{table_name}")
public class {entity_name} {{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 1000)
    @Column(name = "description")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Default constructor for JPA
    protected {entity_name}() {{}}

    // Constructor for creating new instances
    public {entity_name}(String name, String description) {{
        this.name = name;
        this.description = description;
    }}

    // Getters
    public Long getId() {{ return id; }}
    public String getName() {{ return name; }}
    public String getDescription() {{ return description; }}
    public LocalDateTime getCreatedAt() {{ return createdAt; }}
    public LocalDateTime getUpdatedAt() {{ return updatedAt; }}

    // Setters (following immutability principles)
    public void setName(String name) {{ this.name = name; }}
    public void setDescription(String description) {{ this.description = description; }}

    @Override
    public boolean equals(Object o) {{
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        {entity_name} entity = ({entity_name}) o;
        return Objects.equals(id, entity.id);
    }}

    @Override
    public int hashCode() {{
        return Objects.hash(id);
    }}

    @Override
    public String toString() {{
        return "{entity_name}{{" +
                "id=" + id +
                ", name='" + name + '\\'' +
                ", description='" + description + '\\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}}';
    }}
}}
"""
    
    @staticmethod
    def _spring_repository_template(context: Dict[str, Any]) -> str:
        package = context.get('package', 'com.example')
        entity_name = context.get('entity_name', 'Entity')
        
        return f"""package {package}.repository;

import {package}.model.{entity_name};
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {entity_name} entity.
 * 
 * Provides CRUD operations and custom query methods for {entity_name}.
 * Follows Spring Data JPA conventions and best practices.
 */
@Repository
public interface {entity_name}Repository extends JpaRepository<{entity_name}, Long> {{

    /**
     * Find {entity_name} by name.
     * 
     * @param name the name to search for
     * @return Optional containing the {entity_name} if found
     */
    Optional<{entity_name}> findByName(String name);

    /**
     * Find all {entity_name}s containing the given name (case-insensitive).
     * 
     * @param name the name pattern to search for
     * @return List of matching {entity_name}s
     */
    @Query("SELECT e FROM {entity_name} e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<{entity_name}> findByNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Check if {entity_name} exists by name.
     * 
     * @param name the name to check
     * @return true if exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Count {entity_name}s by name pattern.
     * 
     * @param namePattern the name pattern to count
     * @return count of matching {entity_name}s
     */
    @Query("SELECT COUNT(e) FROM {entity_name} e WHERE e.name LIKE :namePattern")
    long countByNamePattern(@Param("namePattern") String namePattern);
}}
"""
    
    @staticmethod
    def _spring_service_template(context: Dict[str, Any]) -> str:
        package = context.get('package', 'com.example')
        entity_name = context.get('entity_name', 'Entity')
        
        return f"""package {package}.service;

import {package}.model.{entity_name};
import {package}.repository.{entity_name}Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for {entity_name} business logic.
 * 
 * Handles all business operations related to {entity_name} entities.
 * Includes proper logging, transaction management, and error handling.
 */
@Service
@Transactional
public class {entity_name}Service {{

    private static final Logger logger = LoggerFactory.getLogger({entity_name}Service.class);

    private final {entity_name}Repository {entity_name.lower()}Repository;

    @Autowired
    public {entity_name}Service({entity_name}Repository {entity_name.lower()}Repository) {{
        this.{entity_name.lower()}Repository = {entity_name.lower()}Repository;
    }}

    /**
     * Create a new {entity_name}.
     * 
     * @param {entity_name.lower()} the {entity_name} to create
     * @return the created {entity_name}
     * @throws IllegalArgumentException if {entity_name.lower()} is null or invalid
     */
    public {entity_name} create({entity_name} {entity_name.lower()}) {{
        logger.info("Creating new {entity_name}: {{}}", {entity_name.lower()}.getName());
        
        if ({entity_name.lower()} == null) {{
            throw new IllegalArgumentException("{entity_name} cannot be null");
        }}
        
        if ({entity_name.lower()}Repository.existsByName({entity_name.lower()}.getName())) {{
            throw new IllegalArgumentException("{entity_name} with name '" + {entity_name.lower()}.getName() + "' already exists");
        }}
        
        {entity_name} saved = {entity_name.lower()}Repository.save({entity_name.lower()});
        logger.info("Successfully created {entity_name} with ID: {{}}", saved.getId());
        
        return saved;
    }}

    /**
     * Find {entity_name} by ID.
     * 
     * @param id the ID to search for
     * @return Optional containing the {entity_name} if found
     */
    @Transactional(readOnly = true)
    public Optional<{entity_name}> findById(Long id) {{
        logger.debug("Finding {entity_name} by ID: {{}}", id);
        return {entity_name.lower()}Repository.findById(id);
    }}

    /**
     * Find all {entity_name}s.
     * 
     * @return List of all {entity_name}s
     */
    @Transactional(readOnly = true)
    public List<{entity_name}> findAll() {{
        logger.debug("Finding all {entity_name}s");
        return {entity_name.lower()}Repository.findAll();
    }}

    /**
     * Update an existing {entity_name}.
     * 
     * @param id the ID of the {entity_name} to update
     * @param updated the updated {entity_name} data
     * @return the updated {entity_name}
     * @throws IllegalArgumentException if {entity_name} not found
     */
    public {entity_name} update(Long id, {entity_name} updated) {{
        logger.info("Updating {entity_name} with ID: {{}}", id);
        
        {entity_name} existing = {entity_name.lower()}Repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("{entity_name} not found with ID: " + id));
        
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        
        {entity_name} saved = {entity_name.lower()}Repository.save(existing);
        logger.info("Successfully updated {entity_name} with ID: {{}}", id);
        
        return saved;
    }}

    /**
     * Delete {entity_name} by ID.
     * 
     * @param id the ID of the {entity_name} to delete
     * @throws IllegalArgumentException if {entity_name} not found
     */
    public void delete(Long id) {{
        logger.info("Deleting {entity_name} with ID: {{}}", id);
        
        if (!{entity_name.lower()}Repository.existsById(id)) {{
            throw new IllegalArgumentException("{entity_name} not found with ID: " + id);
        }}
        
        {entity_name.lower()}Repository.deleteById(id);
        logger.info("Successfully deleted {entity_name} with ID: {{}}", id);
    }}
}}
"""

    @staticmethod
    def _maven_pom_template(context: Dict[str, Any]) -> str:
        group_id = context.get('package', 'com.example')
        artifact_id = context.get('app_name', 'application')
        app_name = context.get('app_name', 'Application')

        return f"""<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.3</version>
        <relativePath/>
    </parent>

    <groupId>{group_id}</groupId>
    <artifactId>{artifact_id}</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <name>{app_name}</name>
    <description>{app_name} - Generated by Agentic Workflow</description>

    <properties>
        <java.version>21</java.version>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- Database -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Flyway for database migrations -->
        <dependency>
            <groupId>org.flywaydb</groupId>
            <artifactId>flyway-core</artifactId>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>postgresql</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>

            <plugin>
                <groupId>org.flywaydb</groupId>
                <artifactId>flyway-maven-plugin</artifactId>
            </plugin>

            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>0.8.8</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>prepare-agent</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>report</id>
                        <phase>test</phase>
                        <goals>
                            <goal>report</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
"""

    @staticmethod
    def _spring_controller_template(context: Dict[str, Any]) -> str:
        package = context.get('package', 'com.example')
        entity_name = context.get('entity_name', 'Entity')

        return f"""package {package}.controller;

import {package}.model.{entity_name};
import {package}.service.{entity_name}Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for {entity_name} operations.
 */
@RestController
@RequestMapping("/api/v1/{entity_name.lower()}s")
public class {entity_name}Controller {{

    private static final Logger logger = LoggerFactory.getLogger({entity_name}Controller.class);
    private final {entity_name}Service {entity_name.lower()}Service;

    @Autowired
    public {entity_name}Controller({entity_name}Service {entity_name.lower()}Service) {{
        this.{entity_name.lower()}Service = {entity_name.lower()}Service;
    }}

    @PostMapping
    public ResponseEntity<{entity_name}> create(@Valid @RequestBody {entity_name} {entity_name.lower()}) {{
        {entity_name} created = {entity_name.lower()}Service.create({entity_name.lower()});
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }}

    @GetMapping
    public ResponseEntity<List<{entity_name}>> getAll() {{
        List<{entity_name}> {entity_name.lower()}s = {entity_name.lower()}Service.findAll();
        return new ResponseEntity<>({entity_name.lower()}s, HttpStatus.OK);
    }}

    @GetMapping("/{{id}}")
    public ResponseEntity<{entity_name}> getById(@PathVariable Long id) {{
        Optional<{entity_name}> {entity_name.lower()} = {entity_name.lower()}Service.findById(id);
        return {entity_name.lower()}.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }}

    @PutMapping("/{{id}}")
    public ResponseEntity<{entity_name}> update(@PathVariable Long id, @Valid @RequestBody {entity_name} {entity_name.lower()}) {{
        {entity_name} updated = {entity_name.lower()}Service.update(id, {entity_name.lower()});
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }}

    @DeleteMapping("/{{id}}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {{
        {entity_name.lower()}Service.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }}
}}
"""

    # Go Templates
    @staticmethod
    def _gin_main_template(context: Dict[str, Any]) -> str:
        module_name = context.get('module_name', 'github.com/company/app')
        app_name = context.get('app_name', 'app')
        
        return f"""package main

import (
    "log"
    "net/http"
    "os"

    "github.com/gin-gonic/gin"
    "{module_name}/internal/api/handlers"
    "{module_name}/internal/api/routes"
    "{module_name}/internal/config"
    "{module_name}/pkg/database"
    "{module_name}/pkg/logger"
)

func main() {{
    // Initialize logger
    logger.Init()
    
    // Load configuration
    cfg, err := config.Load()
    if err != nil {{
        log.Fatal("Failed to load configuration:", err)
    }}
    
    // Initialize database
    db, err := database.Connect(cfg.DatabaseURL)
    if err != nil {{
        log.Fatal("Failed to connect to database:", err)
    }}
    defer db.Close()
    
    // Initialize Gin router
    router := gin.Default()
    
    // Setup middleware
    router.Use(gin.Logger())
    router.Use(gin.Recovery())
    
    // Initialize handlers
    h := handlers.New(db)
    
    // Setup routes
    routes.Setup(router, h)
    
    // Start server
    port := os.Getenv("PORT")
    if port == "" {{
        port = "8080"
    }}
    
    log.Printf("Starting {app_name} server on port %s", port)
    if err := http.ListenAndServe(":"+port, router); err != nil {{
        log.Fatal("Failed to start server:", err)
    }}
}}
"""
    
    @staticmethod
    def _go_mod_template(context: Dict[str, Any]) -> str:
        module_name = context.get('module_name', 'github.com/company/app')
        go_version = context.get('go_version', '1.21')
        
        return f"""module {module_name}

go {go_version}

require (
    github.com/gin-gonic/gin v1.9.1
    github.com/lib/pq v1.10.9
    gorm.io/driver/postgres v1.5.6
    gorm.io/gorm v1.25.7
)

require (
    github.com/bytedance/sonic v1.9.1 // indirect
    github.com/chenzhuoyu/base64x v0.0.0-20221115062448-fe3a3abad311 // indirect
    github.com/gabriel-vasile/mimetype v1.4.2 // indirect
    github.com/gin-contrib/sse v0.1.0 // indirect
    github.com/go-playground/locales v0.14.1 // indirect
    github.com/go-playground/universal-translator v0.18.1 // indirect
    github.com/go-playground/validator/v10 v10.14.0 // indirect
    github.com/goccy/go-json v0.10.2 // indirect
    github.com/jackc/pgpassfile v1.0.0 // indirect
    github.com/jackc/pgservicefile v0.0.0-20221227161230-091c0ba34f0a // indirect
    github.com/jackc/pgx/v5 v5.4.3 // indirect
    github.com/jinzhu/inflection v1.0.0 // indirect
    github.com/jinzhu/now v1.1.5 // indirect
    github.com/json-iterator/go v1.1.12 // indirect
    github.com/klauspost/cpuid/v2 v2.2.4 // indirect
    github.com/leodido/go-urn v1.2.4 // indirect
    github.com/mattn/go-isatty v0.0.19 // indirect
    github.com/modern-go/concurrent v0.0.0-20180306012644-bacd9c7ef1dd // indirect
    github.com/modern-go/reflect2 v1.0.2 // indirect
    github.com/pelletier/go-toml/v2 v2.0.8 // indirect
    github.com/twitchyliquid64/golang-asm v0.15.1 // indirect
    github.com/ugorji/go/codec v1.2.11 // indirect
    golang.org/x/arch v0.3.0 // indirect
    golang.org/x/crypto v0.14.0 // indirect
    golang.org/x/net v0.10.0 // indirect
    golang.org/x/sys v0.13.0 // indirect
    golang.org/x/text v0.13.0 // indirect
    google.golang.org/protobuf v1.30.0 // indirect
    gopkg.in/yaml.v3 v3.0.1 // indirect
)
"""
    
    # Configuration Templates
    @staticmethod
    def _application_yml_template(context: Dict[str, Any]) -> str:
        app_name = context.get('app_name', 'application')
        database = context.get('database', 'postgresql')
        
        return f"""# {app_name} Application Configuration

spring:
  application:
    name: {app_name}
  
  datasource:
    url: jdbc:{database}://localhost:5432/{app_name}
    username: ${{DB_USERNAME:{app_name}}}
    password: ${{DB_PASSWORD:password}}
    driver-class-name: org.{database}.Driver
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
  
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
  
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${{JWT_ISSUER_URI:}}

server:
  port: ${{SERVER_PORT:8080}}
  servlet:
    context-path: /api/v1

logging:
  level:
    com.{app_name}: INFO
    org.springframework.security: DEBUG
  pattern:
    console: "%d{{yyyy-MM-dd HH:mm:ss}} - %msg%n"
    file: "%d{{yyyy-MM-dd HH:mm:ss}} [%thread] %-5level %logger{{36}} - %msg%n"
  file:
    name: logs/{app_name}.log

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: when-authorized
"""
    
    @staticmethod
    def _dockerfile_template(context: Dict[str, Any]) -> str:
        language = context.get('language', 'java')
        app_name = context.get('app_name', 'app')
        
        if language.lower() == 'java':
            return f"""# Multi-stage build for Java application
FROM openjdk:21-jdk-slim as builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM openjdk:21-jre-slim

WORKDIR /app

# Create non-root user
RUN groupadd -r {app_name} && useradd -r -g {app_name} {app_name}

# Copy the built JAR
COPY --from=builder /app/target/{app_name}-*.jar app.jar

# Change ownership
RUN chown -R {app_name}:{app_name} /app

USER {app_name}

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \\
  CMD curl -f http://localhost:8080/api/v1/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
"""
        elif language.lower() == 'golang':
            return f"""# Multi-stage build for Go application
FROM golang:1.21-alpine AS builder

WORKDIR /app

# Copy go mod files
COPY go.mod go.sum ./
RUN go mod download

# Copy source code
COPY . .

# Build the application
RUN CGO_ENABLED=0 GOOS=linux go build -a -installsuffix cgo -o main ./cmd/{app_name}

# Runtime stage
FROM alpine:latest

RUN apk --no-cache add ca-certificates
WORKDIR /root/

# Copy the binary
COPY --from=builder /app/main .

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \\
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/health || exit 1

CMD ["./main"]
"""
        else:
            return f"""# Dockerfile for {language} application
FROM {language}:latest

WORKDIR /app

COPY . .

EXPOSE 8080

CMD ["./start.sh"]
"""
    
    @staticmethod
    def _readme_template(context: Dict[str, Any]) -> str:
        app_name = context.get('app_name', 'Application')
        language = context.get('language', 'Java')
        framework = context.get('framework', 'Spring Boot')
        database = context.get('database', 'PostgreSQL')
        
        return f"""# {app_name}

A {language} {framework} application for {context.get('domain', 'business operations')}.

## Technology Stack

- **Language**: {language}
- **Framework**: {framework}
- **Database**: {database}
- **Build Tool**: {context.get('build_tool', 'Maven')}

## Features

- RESTful API design
- Database integration with {database}
- Comprehensive error handling
- Security implementation
- Logging and monitoring
- Docker containerization
- Comprehensive testing

## Getting Started

### Prerequisites

- {language} {context.get('language_version', '21')}
- {database} {context.get('database_version', '15+')}
- Docker (optional)

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd {app_name.lower().replace(' ', '-')}
```

2. Configure the database:
```bash
# Update application.yml with your database credentials
cp src/main/resources/application.yml.example src/main/resources/application.yml
```

3. Build and run:
```bash
# Using {context.get('build_tool', 'Maven')}
./mvnw spring-boot:run

# Or using Docker
docker-compose up
```

## API Documentation

The API documentation is available at:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI Spec: http://localhost:8080/v3/api-docs

## Project Structure

```
{app_name.lower().replace(' ', '-')}/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
├── docs/
├── scripts/
└── docker/
```

## Configuration

Key configuration properties:

| Property | Description | Default |
|----------|-------------|---------|
| `server.port` | Server port | 8080 |
| `spring.datasource.url` | Database URL | jdbc:{database.lower()}://localhost:5432/{app_name.lower()} |

## Testing

```bash
# Run unit tests
./mvnw test

# Run integration tests
./mvnw verify

# Generate test coverage report
./mvnw jacoco:report
```

## Deployment

### Docker

```bash
# Build image
docker build -t {app_name.lower().replace(' ', '-')} .

# Run container
docker run -p 8080:8080 {app_name.lower().replace(' ', '-')}
```

### Production

1. Build production artifact
2. Configure environment variables
3. Deploy to your preferred platform

## Monitoring

- Health check: `/api/v1/health`
- Metrics: `/api/v1/metrics`
- Application logs: `logs/{app_name.lower()}.log`

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License.

## Generated by

Agentic Workflow System - {context.get('workflow_id', 'Unknown')}
Generated on: {context.get('generation_date', 'Unknown')}
"""
