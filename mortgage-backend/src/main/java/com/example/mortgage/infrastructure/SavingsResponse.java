package com.example.mortgage.infrastructure;

public record SavingsResponse(
    double initialAmount,
    double monthlyContribution,
    double totalContributions,
    double totalInterestEarned,
    double totalTaxPaid,
    double totalSaved,
    int effectiveYears
) {
    public SavingsResponse(double initialAmount, double monthlyContribution, double totalContributions, 
                          double totalInterestEarned, double totalTaxPaid, double totalSaved) {
        this(initialAmount, monthlyContribution, totalContributions, totalInterestEarned, 
             totalTaxPaid, totalSaved, 0);
    }
}
