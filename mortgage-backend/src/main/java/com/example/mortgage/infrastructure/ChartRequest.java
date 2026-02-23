package com.example.mortgage.infrastructure;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ChartRequest(
    @Valid
    ChartMortgageRequest mortgage,
    
    @Valid
    ChartSavingsRequest savings
) {
    public record ChartMortgageRequest(
        @NotNull(message = "Principal is required")
        @DecimalMin(value = "1.00", inclusive = true, message = "Principal must be greater than 0")
        @DecimalMax(value = "100000000.00", inclusive = true, message = "Principal cannot exceed 100,000,000")
        Double principal,
        
        @NotNull(message = "Annual rate is required")
        @DecimalMin(value = "0.00", inclusive = true, message = "Annual rate cannot be negative")
        @DecimalMax(value = "20.00", inclusive = true, message = "Annual rate cannot exceed 20%")
        Double annualRatePercent,
        
        @NotNull(message = "Years is required")
        @Min(value = 1, message = "Years must be at least 1")
        @Max(value = 50, message = "Years cannot exceed 50")
        Integer years,
        
        @DecimalMin(value = "0.00", inclusive = true, message = "Offset amount cannot be negative")
        @DecimalMax(value = "100000000.00", inclusive = true, message = "Offset amount cannot exceed 100,000,000")
        Double offsetAmount,
        
        String offsetMode,
        
        @DecimalMin(value = "0.00", inclusive = true, message = "Offset rate cannot be negative")
        @DecimalMax(value = "20.00", inclusive = true, message = "Offset rate cannot exceed 20%")
        Double offsetRatePercent
    ) {
        public ChartMortgageRequest {
            if (offsetAmount == null) offsetAmount = 0.0;
            if (offsetRatePercent == null) offsetRatePercent = 0.0;
            if (offsetMode == null) offsetMode = "reduceAmount";
        }
    }
    
    public record ChartSavingsRequest(
        @NotNull(message = "Initial amount is required")
        @DecimalMin(value = "0.00", inclusive = true, message = "Initial amount cannot be negative")
        @DecimalMax(value = "100000000.00", inclusive = true, message = "Initial amount cannot exceed 100,000,000")
        Double initialAmount,
        
        @NotNull(message = "Monthly contribution is required")
        @DecimalMin(value = "0.00", inclusive = true, message = "Monthly contribution cannot be negative")
        @DecimalMax(value = "1000000.00", inclusive = true, message = "Monthly contribution cannot exceed 1,000,000")
        Double monthlyContribution,
        
        @NotNull(message = "Annual interest rate is required")
        @DecimalMin(value = "0.00", inclusive = true, message = "Annual interest rate cannot be negative")
        @DecimalMax(value = "20.00", inclusive = true, message = "Annual interest rate cannot exceed 20%")
        Double annualInterestRatePercent,
        
        @NotNull(message = "Tax rate is required")
        @DecimalMin(value = "0.00", inclusive = true, message = "Tax rate cannot be negative")
        @DecimalMax(value = "100.00", inclusive = true, message = "Tax rate cannot exceed 100%")
        Double taxRatePercent,
        
        @NotNull(message = "Periodicity is required")
        String periodicity,
        
        @NotNull(message = "Years is required")
        @Min(value = 1, message = "Years must be at least 1")
        @Max(value = 50, message = "Years cannot exceed 50")
        Integer years
    ) {
        public ChartSavingsRequest {
            if (initialAmount == null) initialAmount = 0.0;
            if (monthlyContribution == null) monthlyContribution = 0.0;
            if (taxRatePercent == null) taxRatePercent = 15.0;
            if (periodicity == null) periodicity = "monthly";
        }
    }
}
