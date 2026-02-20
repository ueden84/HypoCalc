package com.example.mortgage.domain;

public record MortgageResult(
    double monthlyPayment,
    double totalPaid,
    double totalInterest,
    double effectivePrincipal,
    int effectiveYears,
    double totalOffsetInterestEarned
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
        if (totalOffsetInterestEarned < 0) {
            throw new IllegalArgumentException("Total offset interest earned cannot be negative");
        }
    }
    
    public MortgageResult(double monthlyPayment, double totalPaid, double totalInterest) {
        this(monthlyPayment, totalPaid, totalInterest, 0.0, 0, 0.0);
    }
    
    public MortgageResult(double monthlyPayment, double totalPaid, double totalInterest, double effectivePrincipal, int effectiveYears) {
        this(monthlyPayment, totalPaid, totalInterest, effectivePrincipal, effectiveYears, 0.0);
    }
}
