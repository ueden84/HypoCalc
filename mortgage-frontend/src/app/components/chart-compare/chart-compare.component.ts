import { Component, Input, OnChanges, SimpleChanges, AfterViewInit, ViewChild, ElementRef, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { Chart, ChartConfiguration, ChartOptions, registerables } from 'chart.js';
import { ChartCompareResponse, MortgageRequest, SavingsRequest } from '../../models/mortgage.model';
import { TipService } from '../../services/tip.service';

@Component({
  selector: 'app-chart-compare',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatProgressBarModule],
  templateUrl: './chart-compare.component.html',
  styleUrls: ['./chart-compare.component.scss']
})
export class ChartCompareComponent implements OnChanges, AfterViewInit, OnDestroy {
  @Input() chartData: ChartCompareResponse | null = null;
  @Input() mortgageRequest: MortgageRequest | null = null;
  @Input() savingsRequest: SavingsRequest | null = null;

  @ViewChild('compareChartCanvas') compareChartCanvas!: ElementRef<HTMLCanvasElement>;

  private compareChart: Chart | null = null;
  private tipService = inject(TipService);

  tip: string | null = null;
  tipLoading = false;

  constructor() {
    Chart.register(...registerables);
  }

  ngAfterViewInit(): void {
    if (this.chartData) {
      this.createChart();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['chartData'] && this.chartData) {
      if (this.compareChart) {
        this.updateChart();
      } else if (this.compareChartCanvas) {
        this.createChart();
      }
      this.fetchTip();
    }
  }

  private fetchTip(): void {
    if (!this.mortgageRequest || !this.savingsRequest || !this.chartData) {
      return;
    }

    this.tipLoading = true;
    this.tip = null;

    const years = this.chartData.years;
    const difference = this.chartData.difference;
    
    let crossoverYear = -1;
    for (let i = 1; i < difference.length; i++) {
      if (difference[i] < 0) {
        crossoverYear = years[i];
        break;
      }
    }
    
    let maxOffsetAdvantage = 0;
    for (let i = 0; i < difference.length; i++) {
      if (difference[i] > maxOffsetAdvantage) {
        maxOffsetAdvantage = difference[i];
      }
    }
    
    let maxSavingsAdvantage = 0;
    for (let i = 0; i < difference.length; i++) {
      if (difference[i] < maxSavingsAdvantage) {
        maxSavingsAdvantage = difference[i];
      }
    }
    
    const getBenefitAtYear = (targetYear: number): number => {
      const index = years.indexOf(targetYear);
      return index >= 0 ? difference[index] : 0;
    };

    const tipRequest = {
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
      },
      comparison: {
        years: this.chartData.years,
        offsetBenefit: this.chartData.offsetBenefit,
        savingsBenefit: this.chartData.savingsBenefit,
        difference: this.chartData.difference,
        crossoverYear: crossoverYear,
        maxOffsetAdvantage: maxOffsetAdvantage,
        maxSavingsAdvantage: maxSavingsAdvantage,
        benefitAtYear1: getBenefitAtYear(1),
        benefitAtYear3: getBenefitAtYear(3),
        benefitAtYear5: getBenefitAtYear(5),
        benefitAtYear10: getBenefitAtYear(10)
      }
    };

    this.tipService.getTip(tipRequest).subscribe({
      next: (response) => {
        this.tip = response.tip;
        this.tipLoading = false;
      },
      error: () => {
        this.tip = 'AI tip is temporarily unavailable. Please try again later.';
        this.tipLoading = false;
      }
    });
  }

  ngOnDestroy(): void {
    if (this.compareChart) {
      this.compareChart.destroy();
    }
  }

  private createChart(): void {
    if (!this.chartData || this.chartData.years.length === 0) {
      return;
    }

    const years = this.chartData.years.map((y: number) => `Year ${y}`);

    const lineData: ChartConfiguration<'line'>['data'] = {
      labels: years,
      datasets: [
        {
          data: this.chartData.offsetBenefit,
          label: 'Offset Cumulative Benefit',
          borderColor: '#1976d2',
          backgroundColor: 'rgba(25, 118, 210, 0.1)',
          tension: 0.3,
          fill: false
        },
        {
          data: this.chartData.savingsBenefit,
          label: 'Savings Cumulative Benefit',
          borderColor: '#388e3c',
          backgroundColor: 'rgba(56, 142, 60, 0.1)',
          tension: 0.3,
          fill: false
        },
        {
          data: this.chartData.difference,
          label: 'Difference (Offset - Savings)',
          borderColor: '#f57c00',
          backgroundColor: 'rgba(245, 124, 0, 0.1)',
          tension: 0.3,
          fill: false,
          borderDash: [5, 5]
        }
      ]
    };

    const lineOptions: ChartOptions<'line'> = {
      responsive: true,
      maintainAspectRatio: false,
      interaction: {
        mode: 'index',
        intersect: false,
      },
      scales: {
        y: {
          display: true,
          position: 'left',
          title: {
            display: true,
            text: 'Benefit (CZK)'
          }
        }
      },
      plugins: {
        legend: {
          display: true,
          position: 'top'
        }
      }
    };

    const canvas = document.getElementById('compareChart') as HTMLCanvasElement;
    if (canvas) {
      this.compareChart = new Chart(canvas, {
        type: 'line',
        data: lineData,
        options: lineOptions
      });
    }
  }

  private updateChart(): void {
    if (!this.chartData || this.chartData.years.length === 0) {
      return;
    }

    const years = this.chartData.years.map((y: number) => `Year ${y}`);

    if (this.compareChart) {
      this.compareChart.data.labels = years;
      this.compareChart.data.datasets[0].data = this.chartData.offsetBenefit;
      this.compareChart.data.datasets[1].data = this.chartData.savingsBenefit;
      this.compareChart.data.datasets[2].data = this.chartData.difference;
      this.compareChart.update();
    }
  }
}
