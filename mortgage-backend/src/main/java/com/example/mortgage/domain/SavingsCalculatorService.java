package com.example.mortgage.domain;

public class SavingsCalculatorService {
    
    public SavingsResult calculate(double initialAmount, double monthlyContribution, 
                                   double annualInterestRatePercent, double taxRatePercent,
                                   String periodicity, int years) {
        validateInputs(initialAmount, monthlyContribution, annualInterestRatePercent, taxRatePercent, years);
        
        int numberOfPeriods;
        double periodicRate;
        
        if ("monthly".equalsIgnoreCase(periodicity)) {
            numberOfPeriods = years * 12;
            periodicRate = annualInterestRatePercent / 100.0 / 12.0;
        } else {
            numberOfPeriods = years;
            periodicRate = annualInterestRatePercent / 100.0;
        }
        
        double totalContributions = monthlyContribution * numberOfPeriods;
        double afterTaxRate = periodicRate * (1 - taxRatePercent / 100.0);
        
        double totalSaved;
        double interestBeforeTax;
        
        if (annualInterestRatePercent == 0 || afterTaxRate == 0) {
            totalSaved = initialAmount + totalContributions;
            interestBeforeTax = 0;
        } else {
            double factor = Math.pow(1 + afterTaxRate, numberOfPeriods);
            
            double fvInitial = initialAmount * factor;
            
            double fvAnnuity;
            if (monthlyContribution > 0) {
                fvAnnuity = monthlyContribution * (factor - 1) / afterTaxRate;
            } else {
                fvAnnuity = 0;
            }
            
            totalSaved = fvInitial + fvAnnuity;
            
            double grossFactor = Math.pow(1 + periodicRate, numberOfPeriods);
            double grossFvInitial = initialAmount * grossFactor;
            double grossFvAnnuity;
            if (monthlyContribution > 0) {
                grossFvAnnuity = monthlyContribution * (grossFactor - 1) / periodicRate;
            } else {
                grossFvAnnuity = 0;
            }
            double grossTotal = grossFvInitial + grossFvAnnuity;
            interestBeforeTax = grossTotal - initialAmount - totalContributions;
        }
        
        double totalTaxPaid = interestBeforeTax * (taxRatePercent / 100.0);
        double totalInterestEarned = interestBeforeTax - totalTaxPaid;
        
        return new SavingsResult(initialAmount, monthlyContribution, totalContributions, 
                               totalInterestEarned, totalTaxPaid, totalSaved, years);
    }
    
    private void validateInputs(double initialAmount, double monthlyContribution, 
                               double annualInterestRatePercent, double taxRatePercent, int years) {
        if (initialAmount < 0) {
            throw new IllegalArgumentException("Initial amount cannot be negative");
        }
        if (monthlyContribution < 0) {
            throw new IllegalArgumentException("Monthly contribution cannot be negative");
        }
        if (annualInterestRatePercent < 0) {
            throw new IllegalArgumentException("Annual interest rate cannot be negative");
        }
        if (taxRatePercent < 0) {
            throw new IllegalArgumentException("Tax rate cannot be negative");
        }
        if (years <= 0) {
            throw new IllegalArgumentException("Years must be greater than 0");
        }
    }
}
