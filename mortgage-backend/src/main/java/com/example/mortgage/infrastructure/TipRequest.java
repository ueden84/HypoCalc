package com.example.mortgage.infrastructure;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

public record TipRequest(
    @NotNull @Valid MortgageDetails mortgage,
    @NotNull @Valid SavingsDetails savings,
    @NotNull @Valid ComparisonResult comparison
) {
    public record MortgageDetails(
        @NotNull @Positive Double principal,
        @NotNull @Positive Double annualRatePercent,
        @NotNull @Positive Integer years,
        @NotNull @Positive Double offsetAmount,
        @NotNull String offsetMode,
        @NotNull @Positive Double offsetRatePercent
    ) {}

    public record SavingsDetails(
        @NotNull @PositiveOrZero Double initialAmount,
        @NotNull @PositiveOrZero Double monthlyContribution,
        @NotNull @Positive Double annualInterestRatePercent,
        @NotNull @Positive Double taxRatePercent,
        @NotNull String periodicity,
        @NotNull @Positive Integer years
    ) {}

    public record ComparisonResult(
        @NotNull List<Integer> years,
        @NotNull List<Double> offsetBenefit,
        @NotNull List<Double> savingsBenefit,
        @NotNull List<Double> difference,
        @NotNull Integer crossoverYear,
        @NotNull Double maxOffsetAdvantage,
        @NotNull Double maxSavingsAdvantage,
        @NotNull Double benefitAtYear1,
        @NotNull Double benefitAtYear3,
        @NotNull Double benefitAtYear5,
        @NotNull Double benefitAtYear10
    ) {}
}
