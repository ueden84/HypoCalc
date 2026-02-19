package com.example.mortgage.infrastructure;

public record MortgageResponse(
    double monthlyPayment,
    double totalPaid,
    double totalInterest,
    double effectivePrincipal,
    int effectiveYears
) {
    // Constructor for backward compatibility
    public MortgageResponse(double monthlyPayment, double totalPaid, double totalInterest) {
        this(monthlyPayment, totalPaid, totalInterest, 0.0, 0);
    }
}
