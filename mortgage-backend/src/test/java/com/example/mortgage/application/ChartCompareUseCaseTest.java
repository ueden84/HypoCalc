package com.example.mortgage.application;

import com.example.mortgage.domain.MortgageCalculatorService;
import com.example.mortgage.domain.SavingsCalculatorService;
import com.example.mortgage.infrastructure.ChartCompareRequest;
import com.example.mortgage.infrastructure.ChartCompareResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChartCompareUseCaseTest {
    
    private ChartCompareUseCase useCase;
    private MortgageCalculatorService mortgageService;
    private SavingsCalculatorService savingsService;
    
    @BeforeEach
    void setUp() {
        mortgageService = new MortgageCalculatorService();
        savingsService = new SavingsCalculatorService();
        useCase = new ChartCompareUseCase(mortgageService, savingsService);
    }
    
    @Test
    void shouldCalculateChartCompareWithDefaultValues() {
        ChartCompareRequest request = new ChartCompareRequest(
            new ChartCompareRequest.MortgageParams(4000000.0, 4.79, 25, "reduceAmount", 4.99),
            new ChartCompareRequest.SavingsParams(1000000.0, 0.0, 4.0, 15.0, "monthly", 25),
            1000000.0
        );
        
        ChartCompareResponse response = useCase.execute(request);
        
        assertNotNull(response);
        assertEquals(26, response.years().size());
        assertEquals(0, response.years().get(0));
        
        assertEquals(0.0, response.offsetBenefit().get(0));
        assertEquals(0.0, response.savingsBenefit().get(0));
        
        assertTrue(response.offsetBenefit().get(1) > 0, "First year offset benefit should be positive");
        
        assertTrue(response.savingsBenefit().get(1) > 0, "First year savings benefit should be positive");
        
        assertEquals(response.offsetBenefit().size(), response.savingsBenefit().size());
        assertEquals(response.offsetBenefit().size(), response.difference().size());
    }
    
    @Test
    void shouldCalculateChartCompareWithReduceTermMode() {
        ChartCompareRequest request = new ChartCompareRequest(
            new ChartCompareRequest.MortgageParams(4000000.0, 4.79, 25, "reduceTerm", 4.79),
            new ChartCompareRequest.SavingsParams(1000000.0, 0.0, 4.0, 15.0, "monthly", 25),
            1000000.0
        );
        
        ChartCompareResponse response = useCase.execute(request);
        
        assertNotNull(response);
        assertEquals(26, response.years().size());
        
        assertTrue(response.offsetBenefit().get(1) > 0);
    }
    
    @Test
    void shouldCalculateDifferenceCorrectly() {
        ChartCompareRequest request = new ChartCompareRequest(
            new ChartCompareRequest.MortgageParams(4000000.0, 4.79, 25, "reduceAmount", 4.79),
            new ChartCompareRequest.SavingsParams(0.0, 5000.0, 4.5, 15.0, "monthly", 10),
            1000000.0
        );
        
        ChartCompareResponse response = useCase.execute(request);
        
        for (int i = 0; i < response.years().size(); i++) {
            double expectedDifference = response.offsetBenefit().get(i) - response.savingsBenefit().get(i);
            assertEquals(expectedDifference, response.difference().get(i), 0.01);
        }
    }
    
    @Test
    void shouldHandleZeroOffsetAmount() {
        ChartCompareRequest request = new ChartCompareRequest(
            new ChartCompareRequest.MortgageParams(4000000.0, 4.79, 25, "reduceAmount", 4.79),
            new ChartCompareRequest.SavingsParams(1000000.0, 0.0, 4.0, 15.0, "monthly", 25),
            0.0
        );
        
        ChartCompareResponse response = useCase.execute(request);
        
        assertNotNull(response);
        for (int i = 0; i < response.offsetBenefit().size(); i++) {
            assertEquals(0.0, response.offsetBenefit().get(i), "Offset benefit should be 0 when offset amount is 0");
        }
    }
}
