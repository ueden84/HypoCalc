package com.example.mortgage.infrastructure;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SavingsRequest(
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
    @Min(value = 1, message = "Years must be greater than 0")
    @Max(value = 50, message = "Years cannot exceed 50")
    Integer years
) {}
