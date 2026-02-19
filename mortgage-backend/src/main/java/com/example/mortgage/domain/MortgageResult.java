package com.example.mortgage.domain;

public record MortgageResult(
    double monthlyPayment,
    double totalPaid,
    double totalInterest
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
}
