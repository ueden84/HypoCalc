package com.example.mortgage.infrastructure;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MortgageRequest(
    @NotNull(message = "Principal is required")
    @DecimalMin(value = "0.01", message = "Principal must be greater than 0")
    Double principal,
    
    @NotNull(message = "Annual rate is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Annual rate cannot be negative")
    Double annualRatePercent,
    
    @NotNull(message = "Years is required")
    @Min(value = 1, message = "Years must be greater than 0")
    Integer years
) {}
