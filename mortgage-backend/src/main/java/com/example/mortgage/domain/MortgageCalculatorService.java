package com.example.mortgage.domain;

public class MortgageCalculatorService {
    
    public MortgageResult calculate(double principal, double annualRatePercent, int years) {
        return calculate(principal, annualRatePercent, years, 0.0, "reduceAmount");
    }
    
    public MortgageResult calculate(double principal, double annualRatePercent, int years, 
                                    double offsetAmount, String offsetMode) {
        validateInputs(principal, annualRatePercent, years);
        
        if (offsetAmount < 0) {
            throw new IllegalArgumentException("Offset amount cannot be negative");
        }
        if (offsetAmount > principal) {
            throw new IllegalArgumentException("Offset amount cannot exceed principal");
        }
        
        double effectivePrincipal = principal - offsetAmount;
        int effectiveYears = years;
        
        int numberOfPayments = years * 12;
        double monthlyPayment;
        
        if (annualRatePercent == 0) {
            monthlyPayment = effectivePrincipal / numberOfPayments;
        } else {
            double monthlyRate = annualRatePercent / 100.0 / 12.0;
            double factor = Math.pow(1 + monthlyRate, numberOfPayments);
            monthlyPayment = effectivePrincipal * (monthlyRate * factor) / (factor - 1);
        }
        
        // If offset mode is reduceTerm, we keep original principal but calculate how much faster it pays off
        if ("reduceTerm".equals(offsetMode) && offsetAmount > 0) {
            // Calculate with full principal but same monthly payment as without offset
            double monthlyPaymentWithoutOffset;
            if (annualRatePercent == 0) {
                monthlyPaymentWithoutOffset = principal / numberOfPayments;
            } else {
                double monthlyRate = annualRatePercent / 100.0 / 12.0;
                double factor = Math.pow(1 + monthlyRate, numberOfPayments);
                monthlyPaymentWithoutOffset = principal * (monthlyRate * factor) / (factor - 1);
            }
            
            // Now calculate how many months to pay off effectivePrincipal with this payment
            if (annualRatePercent == 0) {
                numberOfPayments = (int) Math.ceil(effectivePrincipal / monthlyPaymentWithoutOffset);
            } else {
                double monthlyRate = annualRatePercent / 100.0 / 12.0;
                if (monthlyPaymentWithoutOffset <= effectivePrincipal * monthlyRate) {
                    // Payment doesn't cover interest
                    numberOfPayments = years * 12;
                } else {
                    numberOfPayments = (int) Math.ceil(
                        Math.log(monthlyPaymentWithoutOffset / (monthlyPaymentWithoutOffset - effectivePrincipal * monthlyRate))
                        / Math.log(1 + monthlyRate)
                    );
                }
            }
            
            effectiveYears = (int) Math.ceil(numberOfPayments / 12.0);
            monthlyPayment = monthlyPaymentWithoutOffset;
            // effectivePrincipal stays as (principal - offsetAmount) for interest calculation
        }
        
        double totalPaid = monthlyPayment * numberOfPayments;
        // Total interest is total paid minus what was actually borrowed (principal - offset)
        double totalInterest = totalPaid - (principal - offsetAmount);
        
        return new MortgageResult(monthlyPayment, totalPaid, totalInterest, effectivePrincipal, effectiveYears);
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
