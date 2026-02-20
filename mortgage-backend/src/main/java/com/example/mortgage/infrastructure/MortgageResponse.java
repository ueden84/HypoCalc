package com.example.mortgage.infrastructure;

public record MortgageResponse(
    double monthlyPayment,
    double totalPaid,
    double totalInterest,
    double effectivePrincipal,
    int effectiveYears,
    double totalOffsetInterestEarned
) {
    public MortgageResponse(double monthlyPayment, double totalPaid, double totalInterest) {
        this(monthlyPayment, totalPaid, totalInterest, 0.0, 0, 0.0);
    }
    
    public MortgageResponse(double monthlyPayment, double totalPaid, double totalInterest, double effectivePrincipal, int effectiveYears) {
        this(monthlyPayment, totalPaid, totalInterest, effectivePrincipal, effectiveYears, 0.0);
    }
}
