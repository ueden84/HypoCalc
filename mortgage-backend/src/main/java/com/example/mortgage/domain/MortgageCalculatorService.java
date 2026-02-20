package com.example.mortgage.domain;

public class MortgageCalculatorService {
    
    public MortgageResult calculate(double principal, double annualRatePercent, int years) {
        return calculate(principal, annualRatePercent, years, 0.0, "reduceAmount", 0.0);
    }
    
    public MortgageResult calculate(double principal, double annualRatePercent, int years, 
                                    double offsetAmount, String offsetMode) {
        return calculate(principal, annualRatePercent, years, offsetAmount, offsetMode, 0.0);
    }
    
    public MortgageResult calculate(double principal, double annualRatePercent, int years, 
                                    double offsetAmount, String offsetMode, double offsetRatePercent) {
        validateInputs(principal, annualRatePercent, years);
        
        if (offsetAmount < 0) {
            throw new IllegalArgumentException("Offset amount cannot be negative");
        }
        if (offsetAmount > principal) {
            throw new IllegalArgumentException("Offset amount cannot exceed principal");
        }
        if (offsetRatePercent < 0) {
            throw new IllegalArgumentException("Offset rate cannot be negative");
        }
        if (offsetAmount > 0 && offsetRatePercent < annualRatePercent) {
            throw new IllegalArgumentException("Offset rate must be greater than or equal to annual rate");
        }
        
        int totalNumberOfPayments = years * 12;
        
        double monthlyPaymentOriginal;
        if (annualRatePercent == 0) {
            monthlyPaymentOriginal = principal / totalNumberOfPayments;
        } else {
            double monthlyRate = annualRatePercent / 100.0 / 12.0;
            double factor = Math.pow(1 + monthlyRate, totalNumberOfPayments);
            monthlyPaymentOriginal = principal * (monthlyRate * factor) / (factor - 1);
        }
        double totalPaidOriginal = monthlyPaymentOriginal * totalNumberOfPayments;
        double totalInterestOriginal = totalPaidOriginal - principal;
        
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
        
        if ("reduceTerm".equals(offsetMode) && offsetAmount > 0) {
            double monthlyPaymentWithoutOffset;
            if (annualRatePercent == 0) {
                monthlyPaymentWithoutOffset = principal / totalNumberOfPayments;
            } else {
                double monthlyRate = annualRatePercent / 100.0 / 12.0;
                double factor = Math.pow(1 + monthlyRate, totalNumberOfPayments);
                monthlyPaymentWithoutOffset = principal * (monthlyRate * factor) / (factor - 1);
            }
            
            if (annualRatePercent == 0) {
                numberOfPayments = (int) Math.ceil(effectivePrincipal / monthlyPaymentWithoutOffset);
            } else {
                double monthlyRate = annualRatePercent / 100.0 / 12.0;
                if (monthlyPaymentWithoutOffset <= effectivePrincipal * monthlyRate) {
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
        }
        
        double totalPaid = monthlyPayment * numberOfPayments;
        double totalInterest = totalPaid - effectivePrincipal;
        
        double totalOffsetInterestEarned = totalInterestOriginal - totalInterest;
        
        return new MortgageResult(monthlyPayment, totalPaid, totalInterest, effectivePrincipal, effectiveYears, totalOffsetInterestEarned);
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
