package com.example.mortgage.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class MortgageCalculatorServiceTest {
    
    private final MortgageCalculatorService service = new MortgageCalculatorService();
    
    @Test
    void shouldCalculateWithInterest() {
        MortgageResult result = service.calculate(300000, 5.0, 30);
        
        assertEquals(1610.46, result.monthlyPayment(), 0.01);
        assertEquals(579765.60, result.totalPaid(), 0.01);
        assertEquals(279765.60, result.totalInterest(), 0.01);
    }
    
    @Test
    void shouldCalculateWithZeroInterest() {
        MortgageResult result = service.calculate(300000, 0, 30);
        
        assertEquals(833.33, result.monthlyPayment(), 0.01);
        assertEquals(300000, result.totalPaid(), 0.01);
        assertEquals(0, result.totalInterest(), 0.01);
    }
    
    @Test
    void shouldRejectNegativePrincipal() {
        assertThrows(IllegalArgumentException.class, () ->
            service.calculate(-1000, 5.0, 30)
        );
    }
    
    @Test
    void shouldRejectZeroPrincipal() {
        assertThrows(IllegalArgumentException.class, () ->
            service.calculate(0, 5.0, 30)
        );
    }
    
    @Test
    void shouldRejectNegativeRate() {
        assertThrows(IllegalArgumentException.class, () ->
            service.calculate(100000, -1.0, 30)
        );
    }
    
    @Test
    void shouldRejectZeroYears() {
        assertThrows(IllegalArgumentException.class, () ->
            service.calculate(100000, 5.0, 0)
        );
    }
    
    @Test
    void shouldRejectNegativeYears() {
        assertThrows(IllegalArgumentException.class, () ->
            service.calculate(100000, 5.0, -5)
        );
    }
    
    @ParameterizedTest
    @CsvSource({
        "100000, 3.5, 15, 714.88",
        "200000, 4.0, 20, 1211.96",
        "500000, 6.0, 25, 3222.41"
    })
    void shouldCalculateVariousScenarios(double principal, double rate, int years, double expected) {
        MortgageResult result = service.calculate(principal, rate, years);
        assertEquals(expected, result.monthlyPayment(), 0.01);
    }
}
