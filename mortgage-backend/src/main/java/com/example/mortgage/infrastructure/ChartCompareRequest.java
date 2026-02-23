package com.example.mortgage.infrastructure;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record ChartCompareRequest(
    @NotNull @Valid MortgageParams mortgage,
    @NotNull @Valid SavingsParams savings,
    @NotNull @Positive Double offsetAmount
) {
    public record MortgageParams(
        @NotNull @Positive Double principal,
        @NotNull @Positive Double annualRatePercent,
        @NotNull @Positive Integer years,
        @NotNull String offsetMode,
        @NotNull @Positive Double offsetRatePercent
    ) {}
    
    public record SavingsParams(
        @NotNull @PositiveOrZero Double initialAmount,
        @NotNull @PositiveOrZero Double monthlyContribution,
        @NotNull @Positive Double annualInterestRatePercent,
        @NotNull @Positive Double taxRatePercent,
        @NotNull String periodicity,
        @NotNull @Positive Integer years
    ) {}
}
