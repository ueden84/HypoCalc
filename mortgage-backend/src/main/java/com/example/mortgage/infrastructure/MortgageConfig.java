package com.example.mortgage.infrastructure;

import com.example.mortgage.domain.MortgageCalculatorService;
import com.example.mortgage.domain.SavingsCalculatorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MortgageConfig {
    
    @Bean
    public MortgageCalculatorService mortgageCalculatorService() {
        return new MortgageCalculatorService();
    }
    
    @Bean
    public SavingsCalculatorService savingsCalculatorService() {
        return new SavingsCalculatorService();
    }
}
