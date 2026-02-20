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
        assertEquals(579767.35, result.totalPaid(), 0.01);
        assertEquals(279767.35, result.totalInterest(), 0.01);
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
        "500000, 6.0, 25, 3221.51"
    })
    void shouldCalculateVariousScenarios(double principal, double rate, int years, double expected) {
        MortgageResult result = service.calculate(principal, rate, years);
        assertEquals(expected, result.monthlyPayment(), 0.01);
    }
    
    @Test
    void shouldCalculateSampleWithoutOffset() {
        MortgageResult result = service.calculate(4000000, 4.79, 25);
        
        assertEquals(22896.82, result.monthlyPayment(), 0.01);
        assertEquals(6869045.30, result.totalPaid(), 0.01);
        assertEquals(2869045.30, result.totalInterest(), 0.01);
    }
    
    @Test
    void shouldCalculateSampleWithOffsetReduceAmount() {
        MortgageResult result = service.calculate(4000000, 4.79, 25, 1000000, "reduceAmount", 4.79);
        
        assertEquals(3000000, result.effectivePrincipal(), 0.01);
        assertEquals(17172.61, result.monthlyPayment(), 0.01);
        assertEquals(5151783.98, result.totalPaid(), 0.01);
        assertEquals(2151783.98, result.totalInterest(), 0.01);
        assertEquals(717261.32, result.totalOffsetInterestEarned(), 0.01);
    }
    
    @Test
    void shouldCalculateSampleWithOffsetReduceTerm() {
        MortgageResult result = service.calculate(4000000, 4.79, 25, 1000000, "reduceTerm", 4.79);
        
        assertEquals(3000000, result.effectivePrincipal(), 0.01);
        assertEquals(22896.82, result.monthlyPayment(), 0.01);
    }
}
