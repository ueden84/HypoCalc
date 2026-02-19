# AGENTS.md - Coding Guidelines for This Repository

This document provides guidelines for agentic coding agents working on this mortgage calculator project.

## Project Overview

Full-stack mortgage calculator with:
- **Backend**: Spring Boot (Java 21, Clean Architecture)
- **Frontend**: Angular 17 with Angular Material
- **Infrastructure**: Docker Compose

## Build Commands

### Backend (mortgage-backend/)
```bash
# Build
mvn clean compile

# Run locally
mvn spring-boot:run

# Run all tests
mvn test

# Run single test class
mvn test -Dtest=MortgageCalculatorServiceTest

# Run single test method
mvn test -Dtest=MortgageCalculatorServiceTest#shouldCalculateWithInterest

# Package
mvn clean package -DskipTests
```

### Frontend (mortgage-frontend/)
```bash
# Install dependencies
npm install

# Start development server
ng serve

# Build for production
ng build --configuration production

# Run tests (watch mode)
ng test

# Run tests once (CI)
ng test --watch=false --browsers=ChromeHeadless

# Run single test file
ng test --include='**/mortgage.service.spec.ts'
```

### Docker (root)
```bash
# Start all services
docker compose up --build

# Start in background
docker compose up -d --build

# Stop services
docker compose down
```

## Code Style Guidelines

### Java (Backend)
- **Package structure**: `com.example.mortgage.{domain|application|infrastructure}`
- **Clean Architecture**: Domain has no Spring dependencies
- **Naming**: Classes=PascalCase, methods/variables=camelCase, constants=UPPER_SNAKE_CASE
- **Records**: Use Java records for DTOs and immutable data
- **Constructor injection**: Always use constructor injection, never field injection
- **Validation**: Use Jakarta Validation annotations on request DTOs
- **Error handling**: Throw `IllegalArgumentException` for domain validation; use `@RestControllerAdvice` for global exception handling
- **Tests**: JUnit 5 with descriptive method names (`shouldCalculateWithInterest`)

### TypeScript/Angular (Frontend)
- **Strict mode**: Enabled in tsconfig.json - no implicit any
- **Standalone components**: Use standalone components (no NgModules)
- **Naming**: Components=kebab-case files, PascalCase class names with `Component` suffix
- **Interfaces**: Use interfaces for data models (e.g., `MortgageRequest`, `MortgageResult`)
- **Services**: Injectable services with `providedIn: 'root'`
- **Reactive forms**: Use reactive forms with validation
- **Async**: Use RxJS Observables with `.subscribe()` and proper error handling
- **SCSS**: Component-scoped styles, use Material theming variables

### General
- **Formatting**: 4 spaces indentation (Java), 2 spaces (TypeScript/JSON)
- **Line length**: 120 characters max
- **Comments**: Only for complex business logic; prefer self-documenting code
- **Git commits**: Use conventional commits (feat:, fix:, test:, refactor:)

## Architecture Principles

### Clean Architecture (Backend)
1. **Domain Layer**: Pure business logic, no framework dependencies
2. **Application Layer**: Use cases orchestrate domain logic
3. **Infrastructure Layer**: REST controllers, DTOs, configuration, persistence

Dependency rule: Domain → Application → Infrastructure (dependencies point inward)

### Angular Best Practices
- Lazy load components when the app grows
- Use `async` pipe for Observables in templates when possible
- Unsubscribe from subscriptions in `ngOnDestroy`
- Keep components small and focused
- Services handle business logic and HTTP calls

## Testing

### Backend
- Unit tests for domain logic (calculations, validations)
- Mock infrastructure dependencies
- Aim for >80% domain layer coverage

### Frontend
- Unit tests for services (HTTP mocking with `HttpTestingController`)
- Component tests for complex UI logic
- Avoid testing framework code (Angular Material components)

## Common Tasks

### Adding a New API Endpoint
1. Create DTO in `infrastructure/` package
2. Add method to `MortgageCalculationUseCase`
3. Add endpoint to `MortgageController`
4. Add tests for domain logic

### Adding a New Component
1. Generate with `ng generate component components/component-name --standalone`
2. Add to imports of parent component
3. Create service if needed
4. Add Material modules to component imports

## Environment URLs
- Backend local: http://localhost:8080
- Frontend local: http://localhost:4200
- API base: `/api/mortgage`

## Dependencies to Avoid Adding
- **Backend**: No Lombok (use records), avoid Spring Data JPA unless persistence needed
- **Frontend**: Avoid additional UI libraries (Angular Material is sufficient)
