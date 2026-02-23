package com.example.mortgage.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class SavingsCalculatorServiceTest {
    
    private final SavingsCalculatorService service = new SavingsCalculatorService();
    
    @Test
    void shouldCalculateWithInitialAmountOnlyMonthlyPeriodicity() {
        SavingsResult result = service.calculate(1000000, 0, 3, 15, "monthly", 10);
        
        assertEquals(1000000, result.initialAmount());
        assertEquals(0, result.monthlyContribution());
        assertEquals(0, result.totalContributions());
        assertEquals(296950.52, result.totalInterestEarned(), 0.01);
        assertEquals(52403.03, result.totalTaxPaid(), 0.01);
        assertEquals(1290112.53, result.totalSaved(), 0.01);
        assertEquals(10, result.effectiveYears());
    }
    
    @Test
    void shouldCalculateWithInitialAmountAndMonthlyContribution() {
        SavingsResult result = service.calculate(1000000, 5000, 4.5, 15, "monthly", 10);
        
        assertEquals(1000000, result.initialAmount());
        assertEquals(5000, result.monthlyContribution());
        assertEquals(600000, result.totalContributions(), 0.01);
        assertEquals(10, result.effectiveYears());
        assertTrue(result.totalSaved() > 1600000);
    }
    
    @Test
    void shouldCalculateWithYearlyPeriodicity() {
        SavingsResult result = service.calculate(1000000, 0, 3, 15, "yearly", 10);
        
        assertEquals(1000000, result.initialAmount());
        assertEquals(10, result.effectiveYears());
        assertTrue(result.totalSaved() < 1290112.53);
    }
    
    @Test
    void shouldCalculateWithZeroInterestRate() {
        SavingsResult result = service.calculate(1000000, 5000, 0, 15, "monthly", 10);
        
        assertEquals(1000000, result.initialAmount());
        assertEquals(5000, result.monthlyContribution());
        assertEquals(600000, result.totalContributions(), 0.01);
        assertEquals(0, result.totalInterestEarned(), 0.01);
        assertEquals(0, result.totalTaxPaid(), 0.01);
        assertEquals(1600000, result.totalSaved(), 0.01);
    }
    
    @Test
    void shouldCalculateWithZeroTaxRate() {
        SavingsResult result = service.calculate(1000000, 0, 3, 0, "monthly", 10);
        
        assertEquals(1000000, result.initialAmount());
        assertEquals(0, result.totalTaxPaid(), 0.01);
        assertTrue(result.totalInterestEarned() > 341308);
    }
    
    @Test
    void shouldCalculateWithOnlyMonthlyContribution() {
        SavingsResult result = service.calculate(0, 5000, 3, 15, "monthly", 10);
        
        assertEquals(0, result.initialAmount());
        assertEquals(5000, result.monthlyContribution());
        assertEquals(600000, result.totalContributions(), 0.01);
        assertTrue(result.totalSaved() > 600000);
    }
    
    @Test
    void shouldThrowExceptionWhenInitialAmountIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> 
            service.calculate(-1000, 0, 3, 15, "monthly", 10));
    }
    
    @Test
    void shouldThrowExceptionWhenMonthlyContributionIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> 
            service.calculate(1000, -100, 3, 15, "monthly", 10));
    }
    
    @Test
    void shouldThrowExceptionWhenInterestRateIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> 
            service.calculate(1000, 0, -3, 15, "monthly", 10));
    }
    
    @Test
    void shouldThrowExceptionWhenTaxRateIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> 
            service.calculate(1000, 0, 3, -15, "monthly", 10));
    }
    
    @Test
    void shouldThrowExceptionWhenYearsIsZero() {
        assertThrows(IllegalArgumentException.class, () -> 
            service.calculate(1000, 0, 3, 15, "monthly", 0));
    }
    
    @Test
    void shouldThrowExceptionWhenYearsIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> 
            service.calculate(1000, 0, 3, 15, "monthly", -5));
    }
}
