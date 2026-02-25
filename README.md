# Mortgage Calculator
![Build Status](https://github.com/ueden84/HypoCalc/actions/workflows/build.yml/badge.svg)

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

### Mortgage Calculator

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

### Savings Calculator

**POST** `/api/savings/calculate`

Request:
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

Response:
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

**Parameters:**
- `initialAmount` - Initial savings amount (CZK)
- `monthlyContribution` - Monthly contribution (CZK)
- `annualInterestRatePercent` - Annual interest rate (%)
- `taxRatePercent` - Tax rate on interest (%, default: 15)
- `periodicity` - Compounding period: "monthly" or "yearly"
- `years` - Investment period in years

### Chart API (Combined Mortgage + Savings Visualization)

**POST** `/api/chart/calculate`

Combines mortgage and savings calculations to generate visualization data for comparing:
- Standard mortgage balance over time
- Offset mortgage balance over time
- Savings balance over time
- Annual principal vs interest payments

Request:
```json
{
  "mortgage": {
    "principal": 4000000,
    "annualRatePercent": 4.79,
    "years": 25,
    "offsetAmount": 1000000,
    "offsetMode": "reduceAmount",
    "offsetRatePercent": 4.79
  },
  "savings": {
    "initialAmount": 1000000,
    "monthlyContribution": 5000,
    "annualInterestRatePercent": 4.5,
    "taxRatePercent": 15,
    "periodicity": "monthly",
    "years": 10
  }
}
```

Response:
```json
{
  "mortgage": {
    "monthlyPayment": 17172.61,
    "totalPaid": 5151783.98,
    "yearlyData": [
      {"year": 1, "principalPaid": 63759.05, "interestPaid": 142312.30},
      {"year": 2, "principalPaid": 66881.06, "interestPaid": 139190.30},
      ...
    ]
  },
  "savings": {
    "totalSaved": 2194548.89,
    "yearlyData": [
      {"year": 1, "balance": 1099990.88},
      {"year": 2, "balance": 1203874.18},
      ...
    ]
  },
  "chartData": {
    "years": [0, 1, 2, ..., 25],
    "standardBalance": [4000000, 3936240.95, ...],
    "offsetBalance": [3000000, 2936240.95, ...],
    "savingsBalance": [1000000, 1099990.88, ...],
    "yearlyPrincipal": [0, 63759.05, ...],
    "yearlyInterest": [0, 142312.30, ...]
  }
}
```

### Chart Compare API

**POST** `/api/chart/compare`

Request:
```json
{
  "mortgage": {"principal": 4000000, "annualRatePercent": 4.79, "years": 25, "offsetMode": "reduceAmount", "offsetRatePercent": 4.99},
  "savings": {"initialAmount": 1000000, "monthlyContribution": 0, "annualInterestRatePercent": 4, "taxRatePercent": 15, "periodicity": "monthly", "years": 25},
  "offsetAmount": 1000000
}
```

Response:
```json
{
  "years": [0, 1, ..., 25],
  "offsetBenefit": [0, 47437, ...],
  "savingsBenefit": [0, 29695, ...],
  "difference": [0, 17742, ...]
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

### Environment Configuration

Frontend uses environment files for API URLs:
- `src/environments/environment.ts` - Local development
- `src/environments/environment.prod.ts` - Production (AWS EC2)

Services use: `${environment.apiUrl}/api/...`

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
- Currency is CZK
- Values cannot be negative
- Use Czech format for numbers
- Sliders are present to set the value
- Two calculators: Mortgage and Savings
- Savings calculator supports:
  - Initial amount and monthly contributions
  - Configurable interest rate
  - Tax rate on interest (default 15%)
  - Monthly or yearly compounding periodicity
- Chart visualization automatically displays when both calculators are used
  - Shows balance comparison (standard, offset, savings)
  - Shows annual principal vs interest breakdown

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


# Savings Account Sample Calculation:
- initial amount: 1 000 000
- monthly amount: 0
- interest rate: 3%
- tax rate: 15%
- periodicity: monthly
- investment in years: 10

Result:
- total contributions: 0
- interest (before tax): 349 353,55
- tax paid: 52 403,03
- final balance: 1 290 112,53

# Savings Account with Monthly Contribution:
- initial amount: 1 000 000
- monthly contribution: 5 000
- interest rate: 4.5%
- tax rate: 15%
- periodicity: monthly
- years: 10

Result:
- total contributions: 600 000
- interest (before tax): 594 548,89
- tax paid: 89 182,33
- final balance: 2 194 548,89
