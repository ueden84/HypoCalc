package com.example.mortgage.application;

import com.example.mortgage.domain.MortgageCalculatorService;
import com.example.mortgage.domain.MortgageResult;
import org.springframework.stereotype.Service;

@Service
public class MortgageCalculationUseCase {
    
    private final MortgageCalculatorService calculatorService;
    
    public MortgageCalculationUseCase(MortgageCalculatorService calculatorService) {
        this.calculatorService = calculatorService;
    }
    
    public MortgageResult execute(double principal, double annualRatePercent, int years) {
        return calculatorService.calculate(principal, annualRatePercent, years);
    }
}
