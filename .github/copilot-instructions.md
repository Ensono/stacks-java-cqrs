# Ensono Stacks Java CQRS - AI Coding Agent Instructions

## Important: Security and Compliance Requirements

**⚠️ MANDATORY**: Before making ANY code changes, read and follow the security guidelines in [`copilot-security-instructions.md`](copilot-security-instructions.md). These are non-negotiable security controls that MUST be maintained at all times.

## Project Overview

This is a Java Spring Boot REST API implementing **CQRS (Command Query Responsibility Segregation)** pattern for a restaurant menu management system. Built on the Ensono Stacks framework, targeting Azure/AWS cloud deployment with CosmosDB or DynamoDB persistence.

**Key Domain**: Restaurant → Menu → Category → Item hierarchy

## Architecture & Structure

### CQRS Implementation

- **Commands**: Write operations in `java/src/main/java/com/amido/stacks/workloads/menu/commands/`
  - All commands extend `MenuCommand` with a `correlationId` and operation code
  - Example: `CreateMenuCommand`, `UpdateMenuCommand`, `DeleteMenuCommand`
- **Handlers**: Command execution in `handlers/`
  - Implement `CommandHandler<T>` interface from `stacks.core.cqrs`
  - Pattern: `MenuBaseCommandHandler` provides common functionality
  - Each handler orchestrates service layer calls
- **Queries**: Read operations via `MenuQueryService` in `service/`
  - Separate read path from write path
  - Direct repository access for queries

### Layered Architecture

```
Controllers (api/v1/, api/v2/)
    ↓
Handlers (command execution)
    ↓
Services (business logic)
    ↓
MenuRepository (persistence - provided by Stacks framework, NOT in source)
    ↓
CosmosDB/DynamoDB
```

**Important**: `MenuRepository` interface is provided by the Stacks framework at runtime - you won't find it in `src/`. Services inject `MenuRepository` which provides Spring Data methods like `save()`, `findById()`, `findAllByRestaurantIdAndName()`.

### Key Packages

- `com.amido.stacks.workloads.menu.api.v1/v2`: REST controllers (v1 = write, v2 = read-only)
- `commands/`: CQRS command objects
- `handlers/`: Command handlers
- `service/`: Business logic (`MenuService`, `CategoryService`, `ItemService`)
- `domain/`: Domain models (`Menu`, `Category`, `Item`)
- `mappers/`: MapStruct interfaces for DTO ↔ Domain ↔ Command conversions
- `exception/`: Custom exceptions (e.g., `MenuNotFoundException`, `MenuAlreadyExistsException`)

## Build & Run Workflows

### Local Development

```bash
cd java/
./mvnw spring-boot:run  # Unix
mvnw.cmd spring-boot:run  # Windows
```

**Required environment variables**:

- `AZURE_COSMOSDB_KEY` - CosmosDB connection key
- `AZURE_APPLICATION_INSIGHTS_INSTRUMENTATION_KEY` - Optional logging (set `enabled: false` in application.yml if unavailable)

**API access**:

- REST API: http://localhost:9000/v1/menu
- Swagger UI: http://localhost:9000/swagger/index.html
- Health: http://localhost:9000/health

### Maven Profiles (Critical)

The project uses **Maven profile-based configuration** for different backends:

- Default: `no-cosmosdb`, `no-dynamodb`, `no-aws`, `no-azure`
- Activate profiles via `-P` flag: `./mvnw spring-boot:run -Pcosmosdb` or `-Pdynamodb`
- Profiles control which Stacks dependencies are included (see `pom.xml` lines 486-520)

### Testing

**Unit tests**: `./mvnw test` (in `java/`)

**API tests** (Serenity BDD with Cucumber):

```bash
# Start the application first (in java/)
./mvnw spring-boot:run

# In separate terminal (from project root)
export BASE_URL=http://localhost:9000
mvn -f api-tests/pom.xml clean verify
open api-tests/target/site/serenity/index.html  # View HTML report
```

**Test structure**: `api-tests/src/test/`

- Feature files: `resources/cucumber/features/`
- Step definitions: `java/com/amido/stacks/tests/api/stepdefinitions/`
- Serenity screenplay pattern for readable API tests

### Docker

```bash
cd java/
docker build --tag stacks:1.0 .
docker run -p 9000:9000 -e AZURE_COSMOSDB_KEY -e AZURE_APPLICATION_INSIGHTS_INSTRUMENTATION_KEY stacks:1.0
```

## Project Conventions

### Naming Patterns

- **Commands**: `{Action}{Entity}Command` (e.g., `CreateMenuCommand`)
- **Handlers**: `{Action}{Entity}Handler` implements `CommandHandler<T>`
- **Services**: `{Entity}Service` with business logic
- **Controllers**: `{Entity}Controller` with REST mappings
- **Mappers**: MapStruct interfaces named `{Entity}Mapper` or `{Action}{Entity}Mapper`

### Stacks Framework Dependencies

This project heavily relies on **Ensono Stacks libraries** (see `pom.xml`):

- `stacks.core.commons` - Common utilities
- `stacks.core.api` - API annotations (`@CreateAPIResponses`, `@UpdateAPIResponses`)
- `stacks.core.cqrs` - CQRS interfaces (`CommandHandler`)
- `stacks.azure.cosmos` / `stacks.aws.dynamodb` - Persistence implementations

**Repository abstraction**: The framework provides repository implementations at runtime based on active profile.

### Code Style

- **Google Java Style** enforced (see `tools/formatter/intellij-java-google-style.xml`)
- **Lombok**: Heavy use of `@RequiredArgsConstructor`, `@Getter`, `@Setter`, `@Data`
- **MapStruct**: All DTO/Domain/Command mapping via compile-time mappers
- **Validation**: `@Validated` on controller request bodies, Jakarta Bean Validation

### Multi-Profile Support

Config files in `java/src/main/resources/`:

- `application.yml` - Base config with profile includes
- `application-cosmosdb.yml` - CosmosDB-specific settings
- `application-dynamodb.yml` - DynamoDB-specific settings
- `application-azure.yml` / `application-aws.yml` - Cloud provider settings

Profile activation: `spring.profiles.include: ["@cosmosdb.profile.name@"]` uses Maven filtering.

## Common Patterns

### Adding a New Command

1. Create command class extending `MenuCommand` in `commands/`
2. Implement handler implementing `CommandHandler<YourCommand>` in `handlers/`
3. Wire handler into appropriate controller
4. Add request/response DTOs in `api/v1/dto/`
5. Create MapStruct mapper in `mappers/commands/`

### Exception Handling

- Custom exceptions in `exception/` package
- Commands carry `correlationId` for tracing
- Framework provides global exception handling

### Auth Configuration

Optional Auth0 integration via `auth.properties`:

- `auth.isEnabled=true/false` - Toggle authentication
- When enabled, all endpoints require Bearer token in Authorization header

## CI/CD

Pipeline definition: `build/azDevOps/azure/azure-pipelines-javaspring-k8s.yml`

- Multi-stage pipeline (build, test, deploy)
- Kubernetes deployment configs: `deploy/k8s/app/`
- Terraform IaC: `deploy/azure/app/kube/`

## Key Files Reference

- Main entry: `java/src/main/java/com/amido/stacks/workloads/Application.java`
- POM config: `java/pom.xml` (profiles at line 457+)
- API contract: Check Swagger at `/swagger/index.html` when running
- Test scenarios: `api-tests/src/test/resources/cucumber/features/*.feature`

## Debugging Tips

- CosmosDB Emulator: Use locally on Windows with cert import (see `Dockerfile` line 29)
- ApplicationInsights optional: Set `enabled: false` in `application.yml` to disable
- Test profile: `ApplicationNoSecurity` config disables auth for testing
- Serenity reports show detailed API interaction logs with request/response bodies
