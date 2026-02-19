package com.example.mortgage.infrastructure;

import com.example.mortgage.domain.MortgageCalculatorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MortgageConfig {
    
    @Bean
    public MortgageCalculatorService mortgageCalculatorService() {
        return new MortgageCalculatorService();
    }
}
