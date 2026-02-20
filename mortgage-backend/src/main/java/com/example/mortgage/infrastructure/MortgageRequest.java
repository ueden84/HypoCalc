package com.example.mortgage.infrastructure;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.AssertTrue;

public record MortgageRequest(
    @NotNull(message = "Principal is required")
    @DecimalMin(value = "0.01", message = "Principal must be greater than 0")
    Double principal,
    
    @NotNull(message = "Annual rate is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Annual rate cannot be negative")
    Double annualRatePercent,
    
    @NotNull(message = "Years is required")
    @Min(value = 1, message = "Years must be greater than 0")
    Integer years,
    
    @DecimalMin(value = "0.00", inclusive = true, message = "Offset amount cannot be negative")
    Double offsetAmount,
    
    String offsetMode,
    
    @DecimalMin(value = "0.00", inclusive = true, message = "Offset rate cannot be negative")
    Double offsetRatePercent
) {
    public MortgageRequest {
        if (offsetAmount == null) {
            offsetAmount = 0.0;
        }
        if (offsetMode == null) {
            offsetMode = "reduceAmount";
        }
        if (offsetRatePercent == null) {
            offsetRatePercent = 0.0;
        }
    }
    
    @AssertTrue(message = "Offset amount cannot exceed principal amount")
    public boolean isOffsetAmountValid() {
        return offsetAmount == null || offsetAmount <= principal;
    }
    
    @AssertTrue(message = "Offset rate must be greater than or equal to annual rate")
    public boolean isOffsetRateValid() {
        if (offsetRatePercent == null || offsetAmount == null || offsetAmount <= 0) {
            return true;
        }
        return offsetRatePercent >= annualRatePercent;
    }
}
