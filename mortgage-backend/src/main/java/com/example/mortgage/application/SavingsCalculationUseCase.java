package com.example.mortgage.application;

import com.example.mortgage.domain.SavingsCalculatorService;
import com.example.mortgage.domain.SavingsResult;
import org.springframework.stereotype.Service;

@Service
public class SavingsCalculationUseCase {
    
    private final SavingsCalculatorService calculatorService;
    
    public SavingsCalculationUseCase(SavingsCalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }
    
    public SavingsResult execute(double initialAmount, double monthlyContribution, 
                                double annualInterestRatePercent, double taxRatePercent,
                                String periodicity, int years) {
        return calculatorService.calculate(initialAmount, monthlyContribution, 
                                          annualInterestRatePercent, taxRatePercent,
                                          periodicity, years);
    }
}
