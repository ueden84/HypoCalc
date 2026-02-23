package com.example.mortgage.application;

import com.example.mortgage.domain.MortgageCalculatorService;
import com.example.mortgage.domain.MortgageCalculatorService.YearlyOffsetBenefit;
import com.example.mortgage.domain.SavingsCalculatorService;
import com.example.mortgage.domain.SavingsCalculatorService.YearlySavingsBalance;
import com.example.mortgage.infrastructure.ChartCompareRequest;
import com.example.mortgage.infrastructure.ChartCompareResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChartCompareUseCase {
    
    private final MortgageCalculatorService mortgageService;
    private final SavingsCalculatorService savingsService;
    
    public ChartCompareUseCase(MortgageCalculatorService mortgageService,
                              SavingsCalculatorService savingsService) {
        this.mortgageService = mortgageService;
        this.savingsService = savingsService;
    }
    
    public ChartCompareResponse execute(ChartCompareRequest request) {
        ChartCompareRequest.MortgageParams mortgage = request.mortgage();
        ChartCompareRequest.SavingsParams savings = request.savings();
        double offsetAmount = request.offsetAmount();
        
        List<YearlyOffsetBenefit> offsetBenefitData = mortgageService.calculateOffsetBenefit(
            mortgage.principal(),
            mortgage.annualRatePercent(),
            mortgage.years(),
            offsetAmount,
            mortgage.offsetMode(),
            mortgage.offsetRatePercent()
        );
        
        List<YearlySavingsBalance> savingsBalanceData = savingsService.calculateYearlyBalances(
            savings.initialAmount(),
            savings.monthlyContribution(),
            savings.annualInterestRatePercent(),
            savings.taxRatePercent(),
            savings.periodicity(),
            savings.years()
        );
        
        int years = Math.max(mortgage.years(), savings.years());
        
        List<Integer> yearList = new ArrayList<>();
        List<Double> offsetBenefitList = new ArrayList<>();
        List<Double> savingsBenefitList = new ArrayList<>();
        
        yearList.add(0);
        offsetBenefitList.add(0.0);
        savingsBenefitList.add(0.0);
        
        for (int year = 1; year <= years; year++) {
            yearList.add(year);
            
            double offsetBenefit = 0;
            if (year <= offsetBenefitData.size()) {
                offsetBenefit = offsetBenefitData.get(year - 1).cumulativeSavings();
            }
            offsetBenefitList.add(offsetBenefit);
            
            double savingsBenefit = 0;
            double totalContributions = savings.monthlyContribution() * 12 * year;
            if (year <= savingsBalanceData.size()) {
                double balance = savingsBalanceData.get(year - 1).balance();
                savingsBenefit = balance - savings.initialAmount() - totalContributions;
            } else if (savingsBalanceData.size() > 0) {
                double lastBalance = savingsBalanceData.get(savingsBalanceData.size() - 1).balance();
                savingsBenefit = lastBalance - savings.initialAmount() - totalContributions;
            }
            savingsBenefitList.add(savingsBenefit);
        }
        
        return ChartCompareResponse.create(yearList, offsetBenefitList, savingsBenefitList);
    }
}
