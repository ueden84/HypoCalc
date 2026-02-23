package com.example.mortgage.domain;

public record SavingsResult(
    double initialAmount,
    double monthlyContribution,
    double totalContributions,
    double totalInterestEarned,
    double totalTaxPaid,
    double totalSaved,
    int effectiveYears
) {
    public SavingsResult {
        if (initialAmount < 0) {
            throw new IllegalArgumentException("Initial amount cannot be negative");
        }
        if (monthlyContribution < 0) {
            throw new IllegalArgumentException("Monthly contribution cannot be negative");
        }
        if (totalContributions < 0) {
            throw new IllegalArgumentException("Total contributions cannot be negative");
        }
        if (totalInterestEarned < 0) {
            throw new IllegalArgumentException("Total interest earned cannot be negative");
        }
        if (totalTaxPaid < 0) {
            throw new IllegalArgumentException("Total tax paid cannot be negative");
        }
        if (totalSaved < 0) {
            throw new IllegalArgumentException("Total saved cannot be negative");
        }
    }
    
    public SavingsResult(double initialAmount, double monthlyContribution, double totalContributions, 
                        double totalInterestEarned, double totalTaxPaid, double totalSaved) {
        this(initialAmount, monthlyContribution, totalContributions, totalInterestEarned, 
             totalTaxPaid, totalSaved, 0);
    }
}
