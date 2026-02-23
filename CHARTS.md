# Chart Specification

This document defines the data model and structure required to generate a combined financial chart with yearly bars and multiple balance trend lines.

---

## 1. Chart Axes

- **Horizontal axis (X):** Years (1..N)
- **Left vertical axis (Y1):** Balance (CZK)
- **Right vertical axis (Y2):** Annual payment (CZK)

---

## 2. Bars (per year)

Each year must display a stacked or grouped bar with:

- `yearlyPrincipal: number`  
- `yearlyInterest: number`

These values come from aggregating monthly amortization data.

---

## 3. Lines

Three independent line series:

- `standardBalance[]` — remaining mortgage balance without offset  
- `offsetBalance[]` — remaining balance after subtracting offset account growth  
- `savingsBalance[]` — savings account balance over time  

All arrays must be aligned by year index.

---

## 4. Required Input Data

### Mortgage
- principal  
- annualRatePercent  
- years  
- offset  
- offsetRatePercent  
- offsetMode ("LOWER_PAYMENT" | "SHORTEN_DURATION")

### Savings
- initialAmount  
- monthlyContribution  
- annualRatePercent  
- taxRatePercent  
- periodicity ("MONTHLY")  
- years  

---

## 5. Derived Data Structures

### Monthly amortization
```ts
interface AmortizationMonth {
  month: number;
  principalPaid: number;
  interestPaid: number;
  remainingBalance: number;
}
