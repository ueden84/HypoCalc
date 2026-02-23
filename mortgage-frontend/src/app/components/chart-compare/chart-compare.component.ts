import { Component, Input, OnChanges, SimpleChanges, AfterViewInit, ViewChild, ElementRef, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { Chart, ChartConfiguration, ChartOptions, registerables } from 'chart.js';
import { ChartCompareResponse } from '../../models/mortgage.model';

@Component({
  selector: 'app-chart-compare',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  templateUrl: './chart-compare.component.html',
  styleUrls: ['./chart-compare.component.scss']
})
export class ChartCompareComponent implements OnChanges, AfterViewInit, OnDestroy {
  @Input() chartData: ChartCompareResponse | null = null;

  @ViewChild('compareChartCanvas') compareChartCanvas!: ElementRef<HTMLCanvasElement>;

  private compareChart: Chart | null = null;

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
    }
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
