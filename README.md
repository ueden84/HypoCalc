# Mortgage Calculator

A full-stack mortgage calculator application with Spring Boot backend and Angular 17 frontend.

## Architecture

```
.
├── mortgage-backend/        # Spring Boot backend (Clean Architecture)
│   ├── src/main/java/...    # Java source code
│   ├── src/test/java/...    # Unit tests
│   ├── pom.xml             # Maven configuration
│   └── Dockerfile          # Backend container
│
├── mortgage-frontend/       # Angular 17 SPA
│   ├── src/app/            # Angular components
│   ├── angular.json        # Angular CLI config
│   ├── package.json        # Node dependencies
│   └── Dockerfile          # Frontend container
│
├── docker-compose.yml       # Orchestrates both services
└── README.md               # This file
```

## Quick Start (Docker Compose)

Run everything with one command:

```bash
docker compose up --build
```

- Backend: http://localhost:8080
- Frontend: http://localhost:4200

## Backend (Spring Boot)

### Local Development

```bash
cd mortgage-backend
mvn spring-boot:run
```

### Run Tests

```bash
mvn test                              # Run all tests
mvn test -Dtest=ClassName             # Run single test class
mvn test -Dtest=ClassName#methodName  # Run single test method
```

### API Contract

**POST** `/api/mortgage/calculate`

Request:
```json
{
  "principal": 300000,
  "annualRatePercent": 5.0,
  "years": 30
}
```

Response:
```json
{
  "monthlyPayment": 1610.46,
  "totalPaid": 579765.60,
  "totalInterest": 279765.60
}
```

## Frontend (Angular)

### Local Development

```bash
cd mortgage-frontend
npm install
ng serve
```

### Build for Production

```bash
ng build --configuration production
```

### Run Tests

```bash
ng test                    # Run tests in watch mode
ng test --watch=false      # Run tests once
```

## Technology Stack

- **Backend**: Java 21, Spring Boot 3.2, Maven, JUnit 5
- **Frontend**: Angular 17, TypeScript, Angular Material
- **Infrastructure**: Docker, Docker Compose

## Functional requirements
- currency is CZK
- values cannot be negative
- use czech format for numbers
- sliders are present to set the value

#  Sample calculation:
- principal amount: 4 000 000
- annual interest rate: 4,79
- loan term: 25
- total interest: 2 869 045,3

Using offset
- offset amount: 1 000 000
- offset interest rate: 4,79
- offset optionm: reduce principal amount
- effective principal: 3 000 000
- monthly payment: 17 172,61
- total paid with offset: 5 151 783,98
- total interest paid without offset: 2 869 045,3
- total interest with offset: 1 197 500 + 954 283,98 = 2 151 783,98
- offset interest earned: 2 869 045,3 - 2 151 783,98 = 717 261,32

