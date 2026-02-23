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
# Compile and verify
mvn clean compile

# Run locally
mvn spring-boot:run

# Run all tests
mvn test

# Run single test class
mvn test -Dtest=MortgageCalculatorServiceTest

# Run single test class (Savings)
mvn test -Dtest=SavingsCalculatorServiceTest

# Run single test method
mvn test -Dtest=MortgageCalculatorServiceTest#shouldCalculateWithInterest

# Package (skip tests)
mvn clean package -DskipTests

# Run with coverage
mvn test -Dcoverage
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

# Run tests once (CI / headless)
ng test --watch=false --browsers=ChromeHeadless

# Run single test file
ng test --include='**/mortgage.service.spec.ts'

# Run tests with coverage
ng test --coverage
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

**Imports**: Use static imports for constants and test assertions. Organize imports in this order:
1. `java.*` packages
2. `javax.*` packages  
3. Third-party (`org.*`, `com.*`)
4. Project imports (`com.example.mortgage.*`)

```java
// Correct import order
import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.mortgage.domain.MortgageCalculation;
import com.example.mortgage.application.MortgageCalculationUseCase;
```

**Package Structure**: `com.example.mortgage.{domain|application|infrastructure}`
- `domain/` - Entities, value objects, domain services, repository interfaces (no Spring deps)
- `application/` - Use cases, DTOs, ports
- `infrastructure/` - Controllers, request/response DTOs, configuration

**Naming Conventions**:
- Classes/Records: `PascalCase` (e.g., `MortgageRequest`)
- Methods/Variables: `camelCase` (e.g., `calculateMortgage`)
- Constants: `UPPER_SNAKE_CASE` (e.g., `MAX_LOAN_AMOUNT`)
- Test Classes: `ClassNameTest` (e.g., `MortgageCalculatorServiceTest`)

**Records**: Use Java records for DTOs and immutable data:
```java
public record MortgageRequest(
    @NotNull @Positive BigDecimal propertyValue,
    @NotNull @Positive BigDecimal downPayment,
    @NotNull @Positive Integer termMonths,
    @NotNull @Positive BigDecimal interestRate
) {}
```

**Constructor Injection**: Always use constructor injection, never field injection:
```java
@Service
public class MortgageCalculationService {
    private final MortgageValidator validator;
    
    public MortgageCalculationService(MortgageValidator validator) {
        this.validator = validator;
    }
}
```

**Validation**:
- Use Jakarta Validation annotations on request DTOs (`@NotNull`, `@Positive`, `@Min`, `@Max`)
- Validate domain logic with explicit checks, throw `IllegalArgumentException`

**Error Handling**:
- Domain validation: Throw `IllegalArgumentException` with descriptive message
- Infrastructure: Use `@RestControllerAdvice` for global exception handling
- Return proper HTTP status codes (400 for validation, 404 for not found, 500 for errors)

**Tests**: JUnit 5 with descriptive method names:
```java
@Test
void shouldCalculateMonthlyPaymentWhenValidInputProvided() {}
@Test
void shouldThrowExceptionWhenInterestRateIsNegative() {}
```

### TypeScript/Angular (Frontend)

**Imports**: Organize imports in this order:
1. Angular core (`@angular/*`)
2. Third-party (`@/*`)
3. Relative paths (`./`, `../`)

```typescript
// Correct import order
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';

import { MortgageService } from './services/mortgage.service';
import { MortgageResult } from './models/mortgage-result.model';
```

**Strict Mode**: Always enabled in tsconfig.json - no implicit `any`

**Naming Conventions**:
- Files: `kebab-case` (e.g., `mortgage-calculator.component.ts`)
- Classes: `PascalCase` with `Component` suffix (e.g., `MortgageCalculatorComponent`)
- Interfaces: `PascalCase` (e.g., `MortgageRequest`, `MortgageResult`)
- Services: `PascalCase` with `Service` suffix (e.g., `MortgageService`)
- Constants: `UPPER_SNAKE_CASE`

**Standalone Components**: Use standalone components (no NgModules):
```typescript
@Component({
  selector: 'app-mortgage-calculator',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatCardModule],
  templateUrl: './mortgage-calculator.component.html',
  styleUrls: ['./mortgage-calculator.component.scss']
})
export class MortgageCalculatorComponent implements OnInit {}
```

**Services**: Use `providedIn: 'root'` for singleton services:
```typescript
@Injectable({ providedIn: 'root' })
export class MortgageService {
  constructor(private http: HttpClient) {}
}
```

**Reactive Forms**: Use reactive forms with validation:
```typescript
this.form = this.fb.group({
  propertyValue: ['', [Validators.required, Validators.min(1)]],
  downPayment: ['', [Validators.required, Validators.min(0)]],
  termMonths: [30, [Validators.required, Validators.min(1), Validators.max(600)]],
  interestRate: ['', [Validators.required, Validators.min(0), Validators.max(100)]]
});
```

**RxJS Observables**:
- Use `.subscribe()` with proper error handling
- Use `async` pipe in templates when possible
- Always unsubscribe in `ngOnDestroy` or use `takeUntilDestroyed()`
```typescript
private destroyRef = inject(DestroyRef);

ngOnInit() {
  this.mortgageService.calculate(this.request)
    .pipe(takeUntilDestroyed(this.destroyRef))
    .subscribe({
      next: (result) => this.result = result,
      error: (err) => this.errorMessage = err.message
    });
}
```

**Input Validation**: For number fields, use regex validation to reject partial matches:
```typescript
// Integer field (e.g., principal, years)
if (/^\d+$/.test(rawValue)) {
  this.form.get('field')?.setValue(parseInt(rawValue, 10), { emitEvent: true });
} else {
  this.form.get('field')?.setValue(rawValue, { emitEvent: true }); // Keep invalid for error display
}

// Decimal field (e.g., interest rate)
if (/^\d*\.?\d+$/.test(rawValue)) {
  this.form.get('field')?.setValue(parseFloat(rawValue), { emitEvent: true });
} else {
  this.form.get('field')?.setValue(rawValue, { emitEvent: true });
}
```

**Display Functions**: Return raw string value for invalid input to show in template:
```typescript
getFieldDisplay(): string {
  const value = this.form.get('field')?.value;
  if (value === null || value === undefined || value === '') {
    return '';
  }
  if (typeof value === 'string') {
    return value; // Return invalid string for display
  }
  return this.formatNumber(value);
}
```

**SCSS**: Component-scoped styles, use Material theming variables:
```scss
@use '@angular/material' as mat;

.container {
  padding: mat.get-typography-config($font-size-base);
}

.button-primary {
  background-color: mat.get-color-from-palette($primary);
}
```

### General

**Formatting**: 
- Java: 4 spaces indentation
- TypeScript/JSON: 2 spaces indentation

**Line Length**: 120 characters maximum

**Comments**: Only for complex business logic; prefer self-documenting code

**Git Commits**: Use conventional commits:
```
feat: add mortgage calculation endpoint
fix: validate negative interest rates
test: add unit tests for calculator service
refactor: separate domain from infrastructure
```

## Architecture Principles

### Clean Architecture (Backend)
1. **Domain Layer**: Pure business logic, no framework dependencies
2. **Application Layer**: Use cases orchestrate domain logic
3. **Infrastructure Layer**: REST controllers, DTOs, configuration, persistence

Dependency rule: Domain → Application → Infrastructure (dependencies point inward)

### Angular Best Practices
- Lazy load components when the app grows
- Use `async` pipe for Observables in templates when possible
- Unsubscribe from subscriptions using `takeUntilDestroyed()` or `DestroyRef`
- Keep components small and focused (single responsibility)
- Services handle business logic and HTTP calls
- Use `OnPush` change detection for performance

## Testing

### Backend
- Unit tests for domain logic (calculations, validations)
- Mock infrastructure dependencies
- Aim for >80% domain layer coverage
- Use `@SpringBootTest` for integration tests sparingly

### Frontend
- Unit tests for services (HTTP mocking with `HttpTestingController`)
- Component tests for complex UI logic
- Avoid testing framework code (Angular Material components)

## Common Tasks

### Adding a New API Endpoint
1. Create request/response DTOs in `infrastructure/dto/`
2. Add method to UseCase in `application/`
3. Add endpoint to Controller in `infrastructure/`
4. Add unit tests for domain logic

### Adding a New Component
1. Generate with `ng generate component components/component-name --standalone`
2. Add to imports of parent component
3. Create service if needed
4. Add Material modules to component imports

### Savings Calculator API Request Format
```json
{
  "initialAmount": 1000000,
  "monthlyContribution": 5000,
  "annualInterestRatePercent": 4.5,
  "taxRatePercent": 15,
  "periodicity": "monthly",
  "years": 10
}
```

### Savings Calculator API Response Format
```json
{
  "initialAmount": 1000000.0,
  "monthlyContribution": 5000.0,
  "totalContributions": 600000.0,
  "totalInterestEarned": 505366.56,
  "totalTaxPaid": 89182.33,
  "totalSaved": 2194548.89,
  "effectiveYears": 10
}
```

## Sample Calculations

### Without Offset
- principal: 4,000,000
- annualRate: 4.79%
- years: 25
- monthlyPayment: 22,896.82
- totalPaid: 6,869,045.30
- totalInterest: 2,869,045.30

### With Offset (reduceAmount mode)
- principal: 4,000,000
- annualRate: 4.79%
- years: 25
- offsetAmount: 1,000,000
- offsetRate: 4.79%
- offsetMode: reduceAmount
- effectivePrincipal: 3,000,000
- monthlyPayment: 17,172.61
- totalPaid: 5,151,783.98
- totalInterest: 2,151,783.98 (interest paid + offset interest earned)
- totalOffsetInterestEarned: 717,261.32 (savings vs no offset)

### With Offset (reduceTerm mode)
- Same inputs as reduceAmount
- monthlyPayment: 22,896.82 (same as without offset)
- Loan term reduced based on offset amount

### Savings Account (Initial Amount Only, Monthly Compounding)
- initialAmount: 1,000,000
- monthlyContribution: 0
- annualInterestRate: 3%
- taxRate: 15%
- periodicity: monthly
- years: 10
- totalContributions: 0
- totalInterestEarned (after tax): 296,950.52
- totalTaxPaid: 52,403.03
- totalSaved (final balance): 1,290,112.53

### Savings Account (With Monthly Contribution)
- initialAmount: 1,000,000
- monthlyContribution: 5,000
- annualInterestRate: 4.5%
- taxRate: 15%
- periodicity: monthly
- years: 10
- totalContributions: 600,000
- final balance: ~2,194,548.89
- totalTaxPaid: ~89,182.33

## Environment URLs
- Backend local: http://localhost:8080
- Frontend local: http://localhost:4200
- Mortgage API: `/api/mortgage/calculate`
- Savings API: `/api/savings/calculate`

## Dependencies to Avoid Adding
- **Backend**: No Lombok (use records), avoid Spring Data JPA unless persistence needed
- **Frontend**: Avoid additional UI libraries (Angular Material is sufficient)
