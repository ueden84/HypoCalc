package com.example.mortgage.domain;

import java.util.ArrayList;
import java.util.List;

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
    
    public List<YearlyAmortization> calculateYearlyAmortization(double principal, double annualRatePercent, int years,
                                                                  double offsetAmount, String offsetMode, double offsetRatePercent) {
        List<YearlyAmortization> yearlyData = new ArrayList<>();
        
        if (principal <= 0 || years <= 0) {
            return yearlyData;
        }
        
        double effectivePrincipal = principal - offsetAmount;
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
            double monthlyRate = annualRatePercent / 100.0 / 12.0;
            double monthlyPaymentWithoutOffset;
            double factor = Math.pow(1 + monthlyRate, years * 12);
            monthlyPaymentWithoutOffset = principal * (monthlyRate * factor) / (factor - 1);
            
            if (monthlyPaymentWithoutOffset > effectivePrincipal * monthlyRate) {
                numberOfPayments = (int) Math.ceil(
                    Math.log(monthlyPaymentWithoutOffset / (monthlyPaymentWithoutOffset - effectivePrincipal * monthlyRate))
                    / Math.log(1 + monthlyRate)
                );
            }
            monthlyPayment = monthlyPaymentWithoutOffset;
        }
        
        double balance = effectivePrincipal;
        double monthlyRate = annualRatePercent / 100.0 / 12.0;
        
        for (int year = 1; year <= years; year++) {
            double yearlyPrincipal = 0;
            double yearlyInterest = 0;
            
            for (int month = 1; month <= 12; month++) {
                if (balance <= 0) break;
                
                double interestPayment = balance * monthlyRate;
                double principalPayment = monthlyPayment - interestPayment;
                
                if (principalPayment > balance) {
                    principalPayment = balance;
                }
                
                yearlyPrincipal += principalPayment;
                yearlyInterest += interestPayment;
                balance -= principalPayment;
                
                if (balance < 0.01) {
                    balance = 0;
                }
            }
            
            yearlyData.add(new YearlyAmortization(year, yearlyPrincipal, yearlyInterest));
        }
        
        return yearlyData;
    }
    
    public List<MonthlyAmortization> calculateMonthlyAmortization(double principal, double annualRatePercent, int years,
                                                                  double offsetAmount, String offsetMode, double offsetRatePercent) {
        List<MonthlyAmortization> monthlyData = new ArrayList<>();
        
        if (principal <= 0 || years <= 0) {
            return monthlyData;
        }
        
        double effectivePrincipal = principal - offsetAmount;
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
            double monthlyRate = annualRatePercent / 100.0 / 12.0;
            double monthlyPaymentWithoutOffset;
            double factor = Math.pow(1 + monthlyRate, years * 12);
            monthlyPaymentWithoutOffset = principal * (monthlyRate * factor) / (factor - 1);
            
            if (monthlyPaymentWithoutOffset > effectivePrincipal * monthlyRate) {
                numberOfPayments = (int) Math.ceil(
                    Math.log(monthlyPaymentWithoutOffset / (monthlyPaymentWithoutOffset - effectivePrincipal * monthlyRate))
                    / Math.log(1 + monthlyRate)
                );
            }
            monthlyPayment = monthlyPaymentWithoutOffset;
        }
        
        double balance = effectivePrincipal;
        double monthlyRate = annualRatePercent / 100.0 / 12.0;
        
        for (int month = 1; month <= numberOfPayments; month++) {
            if (balance <= 0) break;
            
            double interestPayment = balance * monthlyRate;
            double principalPayment = monthlyPayment - interestPayment;
            
            if (principalPayment > balance) {
                principalPayment = balance;
            }
            
            balance -= principalPayment;
            
            if (balance < 0.01) {
                balance = 0;
            }
            
            monthlyData.add(new MonthlyAmortization(month, principalPayment, interestPayment, balance));
        }
        
        return monthlyData;
    }
    
    public List<YearlyOffsetBenefit> calculateOffsetBenefit(double principal, double annualRatePercent, int years,
                                                           double offsetAmount, String offsetMode, double offsetRatePercent) {
        List<YearlyOffsetBenefit> yearlyData = new ArrayList<>();
        
        if (principal <= 0 || years <= 0 || offsetAmount <= 0) {
            return yearlyData;
        }
        
        double monthlyRate = annualRatePercent / 100.0 / 12.0;
        int totalMonths = years * 12;
        
        double monthlyPaymentWithoutOffset;
        if (annualRatePercent == 0) {
            monthlyPaymentWithoutOffset = principal / totalMonths;
        } else {
            double factor = Math.pow(1 + monthlyRate, totalMonths);
            monthlyPaymentWithoutOffset = principal * (monthlyRate * factor) / (factor - 1);
        }
        
        double effectivePrincipal = principal - offsetAmount;
        int numberOfPaymentsWithOffset = years * 12;
        
        double monthlyPaymentWithOffset;
        if (annualRatePercent == 0) {
            monthlyPaymentWithOffset = effectivePrincipal / numberOfPaymentsWithOffset;
        } else {
            double factor = Math.pow(1 + monthlyRate, numberOfPaymentsWithOffset);
            monthlyPaymentWithOffset = effectivePrincipal * (monthlyRate * factor) / (factor - 1);
        }
        
        if ("reduceTerm".equals(offsetMode) && offsetAmount > 0) {
            if (monthlyPaymentWithoutOffset > effectivePrincipal * monthlyRate) {
                numberOfPaymentsWithOffset = (int) Math.ceil(
                    Math.log(monthlyPaymentWithoutOffset / (monthlyPaymentWithoutOffset - effectivePrincipal * monthlyRate))
                    / Math.log(1 + monthlyRate)
                );
            }
            monthlyPaymentWithOffset = monthlyPaymentWithoutOffset;
        }
        
        double balanceWithoutOffset = principal;
        double balanceWithOffset = effectivePrincipal;
        
        double cumulativeSavings = 0;
        
        for (int year = 1; year <= years; year++) {
            double yearlyInterestWithoutOffset = 0;
            double yearlyInterestWithOffset = 0;
            
            for (int month = 1; month <= 12; month++) {
                if (balanceWithoutOffset > 0) {
                    double interestWithoutOffset = balanceWithoutOffset * monthlyRate;
                    yearlyInterestWithoutOffset += interestWithoutOffset;
                    double principalPayment = monthlyPaymentWithoutOffset - interestWithoutOffset;
                    if (principalPayment > balanceWithoutOffset) {
                        principalPayment = balanceWithoutOffset;
                    }
                    balanceWithoutOffset -= principalPayment;
                    if (balanceWithoutOffset < 0.01) balanceWithoutOffset = 0;
                }
                
                if (balanceWithOffset > 0) {
                    double interestWithOffset = balanceWithOffset * monthlyRate;
                    yearlyInterestWithOffset += interestWithOffset;
                    double principalPayment = monthlyPaymentWithOffset - interestWithOffset;
                    if (principalPayment > balanceWithOffset) {
                        principalPayment = balanceWithOffset;
                    }
                    balanceWithOffset -= principalPayment;
                    if (balanceWithOffset < 0.01) balanceWithOffset = 0;
                }
            }
            
            double yearlySavings = yearlyInterestWithoutOffset - yearlyInterestWithOffset;
            cumulativeSavings += yearlySavings;
            
            yearlyData.add(new YearlyOffsetBenefit(year, yearlyInterestWithoutOffset, yearlyInterestWithOffset, yearlySavings, cumulativeSavings));
        }
        
        return yearlyData;
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
    
    public record YearlyAmortization(int year, double principalPaid, double interestPaid) {}
    
    public record MonthlyAmortization(int month, double principalPaid, double interestPaid, double remainingBalance) {}
    
    public record YearlyOffsetBenefit(int year, double interestWithoutOffset, double interestWithOffset, double monthlySavings, double cumulativeSavings) {}
}
