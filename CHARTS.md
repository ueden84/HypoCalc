# Chart Specification

This document defines the data model and structure required to generate a combined financial chart with yearly bars and multiple balance trend lines.

---

## 1. Chart Axes

- **Horizontal axis (X):** Years (0..N) - starts at Year 0 (initial values)
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

**Note:** With "reduceTerm" offset mode:
- Standard balance continues for full loan term
- Offset balance shortens to show earlier payoff

---

## 4. Required Input Data

### Mortgage
- principal  
- annualRatePercent  
- years  
- offsetAmount
- offsetRatePercent  
- offsetMode ("reduceAmount" | "reduceTerm")

### Savings
- initialAmount  
- monthlyContribution  
- annualInterestRatePercent  
- taxRatePercent  
- periodicity ("monthly" | "yearly")
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
```

---

# Offset vs Savings Comparison Chart Specification

This chart visualizes how the financial benefit of an offset account compares to a savings account over time, assuming the same deposit amount and the same number of years.

---

## 1. Chart Axes

### X‑Axis (horizontal)
- Years (0..N) - starts at Year 0

### Y‑Axis (vertical)
- Cumulative Benefit (CZK)

---

## 2. Chart Series

### 2.1 Line: Offset Cumulative Benefit
Represents the total interest avoided due to the offset account up to each year.

\[
\text{OffsetBenefit}(year) = \sum_{m=1..year} (\text{InterestWithoutOffset}_m - \text{InterestWithOffset}_m)
\]

Starts at 0 for Year 0.



### 2.2 Line: Savings Cumulative Benefit
Represents the total net gain from the savings account up to each year.

\[
\text{SavingsBenefit}(year) = \text{SavingsBalance}(year) - \text{InitialAmount} - \text{TotalContributions}(year)
\]

Starts at 0 for Year 0 (excludes initial principal to make it comparable to offset benefit).



### 2.3 Line: Difference
Shows which strategy is better at each year.

\[
\text{Difference}(year) = \text{OffsetBenefit}(year) - \text{SavingsBenefit}(year)
\]

Positive → offset wins  
Negative → savings wins

---

## 3. API Integration

### Request
POST `/api/chart/compare`

```json
{
  "mortgage": {
    "principal": 4000000,
    "annualRatePercent": 4.79,
    "years": 25,
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
  },
  "offsetAmount": 1000000
}
```

### Response
```json
{
  "years": [0, 1, 2, ..., 25],
  "offsetBenefit": [0, 47437.43, ...],
  "savingsBenefit": [0, 2295.00, ...],
  "difference": [0, 45142.43, ...]
}
```

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

---
# Offset vs Savings Comparison Chart Specification

This chart visualizes how the financial benefit of an offset account compares to a savings account over time, assuming the same deposit amount and the same number of years.

---

## 1. Chart Axes

### X‑Axis (horizontal)
- Years (1..N)

### Left Y‑Axis (vertical)
- Balance (CZK)

### Right Y‑Axis (vertical)
- Annual payment (CZK)  
  (Used only if needed for alignment with other charts)

---

## 2. Chart Series

### 2.1 Line: Offset Cumulative Benefit
Represents the total interest avoided due to the offset account up to each year.



\[
\text{OffsetBenefit}(year) = \sum_{m=1..year} (\text{InterestWithoutOffset}_m - \text{InterestWithOffset}_m)
\]



### 2.2 Line: Savings Cumulative Benefit
Represents the total net gain from the savings account up to each year.



\[
\text{SavingsBenefit}(year) = \text{SavingsBalance}(year) - \text{TotalContributions}(year)
\]



### 2.3 Line: Difference (optional)
Shows which strategy is better at each year.



\[
\text{Difference}(year) = \text{OffsetBenefit}(year) - \text{SavingsBenefit}(year)
\]



Positive → offset wins  
Negative → savings wins

---