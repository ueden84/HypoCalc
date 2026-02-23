package com.example.mortgage.application;

import com.example.mortgage.domain.MortgageCalculatorService;
import com.example.mortgage.domain.SavingsCalculatorService;
import com.example.mortgage.domain.MortgageCalculatorService.YearlyAmortization;
import com.example.mortgage.domain.MortgageCalculatorService.MonthlyAmortization;
import com.example.mortgage.domain.SavingsCalculatorService.YearlySavingsBalance;
import com.example.mortgage.infrastructure.ChartRequest.ChartMortgageRequest;
import com.example.mortgage.infrastructure.ChartRequest.ChartSavingsRequest;
import com.example.mortgage.infrastructure.ChartResponse;
import com.example.mortgage.infrastructure.ChartResponse.ChartData;
import com.example.mortgage.infrastructure.ChartResponse.MortgageData;
import com.example.mortgage.infrastructure.ChartResponse.SavingsData;
import com.example.mortgage.infrastructure.ChartResponse.YearlyData;
import com.example.mortgage.infrastructure.ChartResponse.YearlySavingsData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChartCalculationUseCase {
    
    private final MortgageCalculatorService mortgageService;
    private final SavingsCalculatorService savingsService;
    
    public ChartCalculationUseCase(MortgageCalculatorService mortgageService,
                                    SavingsCalculatorService savingsService) {
        this.mortgageService = mortgageService;
        this.savingsService = savingsService;
    }
    
    public ChartResponse execute(ChartMortgageRequest mortgageReq, ChartSavingsRequest savingsReq) {
        int years = Math.max(mortgageReq.years(), savingsReq.years());
        
        var mortgageResult = mortgageService.calculate(
            mortgageReq.principal(),
            mortgageReq.annualRatePercent(),
            mortgageReq.years(),
            mortgageReq.offsetAmount(),
            mortgageReq.offsetMode(),
            mortgageReq.offsetRatePercent()
        );
        
        var savingsResult = savingsService.calculate(
            savingsReq.initialAmount(),
            savingsReq.monthlyContribution(),
            savingsReq.annualInterestRatePercent(),
            savingsReq.taxRatePercent(),
            savingsReq.periodicity(),
            savingsReq.years()
        );
        
        List<YearlyAmortization> yearlyAmortization = mortgageService.calculateYearlyAmortization(
            mortgageReq.principal(),
            mortgageReq.annualRatePercent(),
            mortgageReq.years(),
            mortgageReq.offsetAmount(),
            mortgageReq.offsetMode(),
            mortgageReq.offsetRatePercent()
        );
        
        List<MonthlyAmortization> monthlyAmortization = mortgageService.calculateMonthlyAmortization(
            mortgageReq.principal(),
            mortgageReq.annualRatePercent(),
            mortgageReq.years(),
            0.0,
            "reduceAmount",
            0.0
        );
        
        List<YearlySavingsBalance> yearlySavings = savingsService.calculateYearlyBalances(
            savingsReq.initialAmount(),
            savingsReq.monthlyContribution(),
            savingsReq.annualInterestRatePercent(),
            savingsReq.taxRatePercent(),
            savingsReq.periodicity(),
            savingsReq.years()
        );
        
        List<Integer> yearList = new ArrayList<>();
        List<Double> standardBalanceList = new ArrayList<>();
        List<Double> offsetBalanceList = new ArrayList<>();
        List<Double> savingsBalanceList = new ArrayList<>();
        List<Double> yearlyPrincipalList = new ArrayList<>();
        List<Double> yearlyInterestList = new ArrayList<>();
        
        double runningStandardBalance = mortgageReq.principal();
        double runningOffsetBalance = mortgageReq.principal() - mortgageReq.offsetAmount();
        
        int mortgageYears = yearlyAmortization.size();
        int savingsYears = yearlySavings.size();
        
        for (int year = 1; year <= years; year++) {
            yearList.add(year);
            
            if (year <= mortgageYears) {
                YearlyAmortization ya = yearlyAmortization.get(year - 1);
                yearlyPrincipalList.add(ya.principalPaid());
                yearlyInterestList.add(ya.interestPaid());
            } else {
                yearlyPrincipalList.add(0.0);
                yearlyInterestList.add(0.0);
            }
            
            if (year <= mortgageYears) {
                runningStandardBalance -= yearlyAmortization.get(year - 1).principalPaid();
                if (runningStandardBalance < 0) runningStandardBalance = 0;
                standardBalanceList.add(runningStandardBalance);
            } else {
                standardBalanceList.add(0.0);
            }
            
            if (year <= mortgageYears) {
                runningOffsetBalance -= yearlyAmortization.get(year - 1).principalPaid();
                if (runningOffsetBalance < 0) runningOffsetBalance = 0;
                offsetBalanceList.add(runningOffsetBalance);
            } else {
                offsetBalanceList.add(0.0);
            }
            
            if (year <= savingsYears) {
                savingsBalanceList.add(yearlySavings.get(year - 1).balance());
            } else if (savingsYears > 0) {
                savingsBalanceList.add(yearlySavings.get(savingsYears - 1).balance());
            } else {
                savingsBalanceList.add(savingsReq.initialAmount() + savingsReq.monthlyContribution() * 12 * year);
            }
        }
        
        List<YearlyData> mortgageYearlyData = yearlyAmortization.stream()
            .map(ya -> new YearlyData(ya.year(), ya.principalPaid(), ya.interestPaid()))
            .toList();
        
        List<YearlySavingsData> savingsYearlyData = yearlySavings.stream()
            .map(ysb -> new YearlySavingsData(ysb.year(), ysb.balance()))
            .toList();
        
        MortgageData mortgageData = new MortgageData(
            mortgageResult.monthlyPayment(),
            mortgageResult.totalPaid(),
            mortgageYearlyData
        );
        
        SavingsData savingsData = new SavingsData(
            savingsResult.totalSaved(),
            savingsYearlyData
        );
        
        ChartData chartData = new ChartData(
            yearList,
            standardBalanceList,
            offsetBalanceList,
            savingsBalanceList,
            yearlyPrincipalList,
            yearlyInterestList
        );
        
        return new ChartResponse(mortgageData, savingsData, chartData);
    }
}
