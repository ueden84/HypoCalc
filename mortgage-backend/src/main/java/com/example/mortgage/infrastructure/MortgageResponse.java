package com.example.mortgage.infrastructure;

public record MortgageResponse(
    double monthlyPayment,
    double totalPaid,
    double totalInterest
) {}
