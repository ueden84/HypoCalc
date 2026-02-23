package com.example.mortgage.infrastructure;

import java.util.List;

public record ChartCompareResponse(
    List<Integer> years,
    List<Double> offsetBenefit,
    List<Double> savingsBenefit,
    List<Double> difference
) {
    public static ChartCompareResponse create(List<Integer> years,
                                               List<Double> offsetBenefit,
                                               List<Double> savingsBenefit) {
        List<Double> difference = new java.util.ArrayList<>();
        for (int i = 0; i < years.size(); i++) {
            double diff = offsetBenefit.get(i) - savingsBenefit.get(i);
            difference.add(diff);
        }
        return new ChartCompareResponse(years, offsetBenefit, savingsBenefit, difference);
    }
}
