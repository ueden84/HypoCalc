package com.example.mortgage.infrastructure;

import java.util.List;

public record ChartResponse(
    MortgageData mortgage,
    SavingsData savings,
    ChartData chartData
) {
    public record MortgageData(
        double monthlyPayment,
        double totalPaid,
        List<YearlyData> yearlyData
    ) {}
    
    public record SavingsData(
        double totalSaved,
        List<YearlySavingsData> yearlyData
    ) {}
    
    public record YearlyData(
        int year,
        double principalPaid,
        double interestPaid
    ) {}
    
    public record YearlySavingsData(
        int year,
        double balance
    ) {}
    
    public record ChartData(
        List<Integer> years,
        List<Double> standardBalance,
        List<Double> offsetBalance,
        List<Double> savingsBalance,
        List<Double> yearlyPrincipal,
        List<Double> yearlyInterest
    ) {}
}
