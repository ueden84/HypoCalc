package com.example.mortgage.domain;

public class MortgageCalculatorService {
    
    public MortgageResult calculate(double principal, double annualRatePercent, int years) {
        validateInputs(principal, annualRatePercent, years);
        
        int numberOfPayments = years * 12;
        double monthlyPayment;
        
        if (annualRatePercent == 0) {
            monthlyPayment = principal / numberOfPayments;
        } else {
            double monthlyRate = annualRatePercent / 100.0 / 12.0;
            double factor = Math.pow(1 + monthlyRate, numberOfPayments);
            monthlyPayment = principal * (monthlyRate * factor) / (factor - 1);
        }
        
        double totalPaid = monthlyPayment * numberOfPayments;
        double totalInterest = totalPaid - principal;
        
        return new MortgageResult(monthlyPayment, totalPaid, totalInterest);
    }
    
    private void validateInputs(double principal, double annualRatePercent, int years) {
        if (principal <= 0) {
            throw new IllegalArgumentException("Principal must be greater than 0");
        }
        if (annualRatePercent < 0) {
            throw new IllegalArgumentException("Annual rate cannot be negative");
        }
        if (years <= 0) {
            throw new IllegalArgumentException("Years must be greater than 0");
        }
    }
}
