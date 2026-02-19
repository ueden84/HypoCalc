package com.example.mortgage.domain;

public record MortgageResult(
    double monthlyPayment,
    double totalPaid,
    double totalInterest,
    double effectivePrincipal,
    int effectiveYears
) {
    public MortgageResult {
        if (monthlyPayment < 0) {
            throw new IllegalArgumentException("Monthly payment cannot be negative");
        }
        if (totalPaid < 0) {
            throw new IllegalArgumentException("Total paid cannot be negative");
        }
        if (totalInterest < 0) {
            throw new IllegalArgumentException("Total interest cannot be negative");
        }
    }
    
    // Constructor for backward compatibility
    public MortgageResult(double monthlyPayment, double totalPaid, double totalInterest) {
        this(monthlyPayment, totalPaid, totalInterest, 0.0, 0);
    }
}
