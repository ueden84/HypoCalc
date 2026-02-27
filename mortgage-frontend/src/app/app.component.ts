import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MortgageCalculatorComponent } from './components/mortgage-calculator/mortgage-calculator.component';
import { SavingsCalculatorComponent } from './components/savings-calculator/savings-calculator.component';
import { ChartComponent, ChartData } from './components/chart/chart.component';
import { ChartCompareComponent } from './components/chart-compare/chart-compare.component';
import { MortgageRequest, MortgageResult, SavingsRequest, SavingsResult, ChartResponse, ChartCompareResponse } from './models/mortgage.model';
import { ChartService } from './services/chart.service';
import { ChartCompareService } from './services/chart-compare.service';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    HttpClientModule,
    MortgageCalculatorComponent,
    SavingsCalculatorComponent,
    ChartComponent,
    ChartCompareComponent
  ],
  template: `
    <div class="app-container">
      <div class="calculators-row">
        <app-mortgage-calculator (calculated)="onMortgageCalculated($event)"></app-mortgage-calculator>
        <app-savings-calculator (calculated)="onSavingsCalculated($event)"></app-savings-calculator>
      </div>
      <app-chart *ngIf="chartData" [chartData]="chartData"></app-chart>
      <app-chart-compare *ngIf="compareChartData" 
        [chartData]="compareChartData"
        [mortgageRequest]="mortgageRequest"
        [savingsRequest]="savingsRequest">
      </app-chart-compare>
    </div>
  `,
  styles: [`
    .app-container {
      min-height: 100vh;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      padding: 20px;
    }
    .calculators-row {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(450px, 500px));
      justify-content: center;
      gap: 20px;
    }
  `]
})
export class AppComponent {
  title = 'mortgage-frontend';
  
  mortgageRequest: MortgageRequest | null = null;
  savingsRequest: SavingsRequest | null = null;
  
  chartData: ChartData | null = null;
  compareChartData: ChartCompareResponse | null = null;
  
  constructor(
    private chartService: ChartService,
    private chartCompareService: ChartCompareService
  ) {}
  
  onMortgageCalculated(data: { request: MortgageRequest; result: MortgageResult }): void {
    this.mortgageRequest = data.request;
    this.checkAndGenerateChart();
  }
  
  onSavingsCalculated(data: { request: SavingsRequest; result: SavingsResult }): void {
    this.savingsRequest = data.request;
    this.checkAndGenerateChart();
  }
  
  private checkAndGenerateChart(): void {
    if (this.mortgageRequest && this.savingsRequest) {
      const chartRequest = {
        mortgage: {
          principal: this.mortgageRequest.principal,
          annualRatePercent: this.mortgageRequest.annualRatePercent,
          years: this.mortgageRequest.years,
          offsetAmount: this.mortgageRequest.offsetAmount || 0,
          offsetMode: this.mortgageRequest.offsetMode || 'reduceAmount',
          offsetRatePercent: this.mortgageRequest.offsetRatePercent || 0
        },
        savings: {
          initialAmount: this.savingsRequest.initialAmount,
          monthlyContribution: this.savingsRequest.monthlyContribution,
          annualInterestRatePercent: this.savingsRequest.annualInterestRatePercent,
          taxRatePercent: this.savingsRequest.taxRatePercent,
          periodicity: this.savingsRequest.periodicity,
          years: this.savingsRequest.years
        }
      };
      
      this.chartService.calculateChart(chartRequest).subscribe({
        next: (response) => {
          this.chartData = response.chartData;
        },
        error: (err) => {
          console.error('Error generating chart:', err);
        }
      });
      
      const offsetAmount = this.mortgageRequest.offsetAmount || 0;
      if (offsetAmount > 0) {
        const compareRequest = {
          mortgage: {
            principal: this.mortgageRequest.principal,
            annualRatePercent: this.mortgageRequest.annualRatePercent,
            years: this.mortgageRequest.years,
            offsetMode: this.mortgageRequest.offsetMode || 'reduceAmount',
            offsetRatePercent: this.mortgageRequest.offsetRatePercent || 0
          },
          savings: {
            initialAmount: this.savingsRequest.initialAmount,
            monthlyContribution: this.savingsRequest.monthlyContribution,
            annualInterestRatePercent: this.savingsRequest.annualInterestRatePercent,
            taxRatePercent: this.savingsRequest.taxRatePercent,
            periodicity: this.savingsRequest.periodicity,
            years: this.savingsRequest.years
          },
          offsetAmount: offsetAmount
        };
        
        this.chartCompareService.compareChart(compareRequest).subscribe({
          next: (response) => {
            this.compareChartData = response;
          },
          error: (err) => {
            console.error('Error generating compare chart:', err);
          }
        });
      } else {
        this.compareChartData = null;
      }
    }
  }
}
