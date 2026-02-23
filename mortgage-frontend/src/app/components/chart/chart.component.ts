import { Component, Input, OnChanges, SimpleChanges, AfterViewInit, ViewChild, ElementRef, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { Chart, ChartConfiguration, ChartOptions, registerables } from 'chart.js';

export interface ChartData {
  years: number[];
  standardBalance: number[];
  offsetBalance: number[];
  savingsBalance: number[];
  yearlyPrincipal: number[];
  yearlyInterest: number[];
}

@Component({
  selector: 'app-chart',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  templateUrl: './chart.component.html',
  styleUrls: ['./chart.component.scss']
})
export class ChartComponent implements OnChanges, AfterViewInit, OnDestroy {
  @Input() chartData: ChartData | null = null;

  @ViewChild('lineChartCanvas') lineChartCanvas!: ElementRef<HTMLCanvasElement>;
  @ViewChild('barChartCanvas') barChartCanvas!: ElementRef<HTMLCanvasElement>;

  private lineChart: Chart | null = null;
  private barChart: Chart | null = null;

  constructor() {
    Chart.register(...registerables);
  }

  ngAfterViewInit(): void {
    if (this.chartData) {
      this.createCharts();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['chartData'] && this.chartData) {
      if (this.lineChart && this.barChart) {
        this.updateCharts();
      } else if (this.lineChartCanvas && this.barChartCanvas) {
        this.createCharts();
      }
    }
  }

  ngOnDestroy(): void {
    if (this.lineChart) {
      this.lineChart.destroy();
    }
    if (this.barChart) {
      this.barChart.destroy();
    }
  }

  private createCharts(): void {
    if (!this.chartData || this.chartData.years.length === 0) {
      return;
    }

    const years = this.chartData.years.map(y => `Year ${y}`);

    const lineData: ChartConfiguration<'line'>['data'] = {
      labels: years,
      datasets: [
        {
          data: this.chartData.standardBalance,
          label: 'Standard Balance',
          borderColor: '#1976d2',
          backgroundColor: 'rgba(25, 118, 210, 0.1)',
          tension: 0.3,
          fill: false
        },
        {
          data: this.chartData.offsetBalance,
          label: 'Offset Balance',
          borderColor: '#388e3c',
          backgroundColor: 'rgba(56, 142, 60, 0.1)',
          tension: 0.3,
          fill: false
        },
        {
          data: this.chartData.savingsBalance,
          label: 'Savings Balance',
          borderColor: '#f57c00',
          backgroundColor: 'rgba(245, 124, 0, 0.1)',
          tension: 0.3,
          fill: false
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
            text: 'Balance (CZK)'
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

    const lineCanvas = document.getElementById('lineChart') as HTMLCanvasElement;
    if (lineCanvas) {
      this.lineChart = new Chart(lineCanvas, {
        type: 'line',
        data: lineData,
        options: lineOptions
      });
    }

    const barData: ChartConfiguration<'bar'>['data'] = {
      labels: years,
      datasets: [
        {
          data: this.chartData.yearlyPrincipal,
          label: 'Principal',
          backgroundColor: '#1976d2'
        },
        {
          data: this.chartData.yearlyInterest,
          label: 'Interest',
          backgroundColor: '#f44336'
        }
      ]
    };

    const barOptions: ChartOptions<'bar'> = {
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        y: {
          display: true,
          position: 'left',
          title: {
            display: true,
            text: 'Amount (CZK)'
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

    const barCanvas = document.getElementById('barChart') as HTMLCanvasElement;
    if (barCanvas) {
      this.barChart = new Chart(barCanvas, {
        type: 'bar',
        data: barData,
        options: barOptions
      });
    }
  }

  private updateCharts(): void {
    if (!this.chartData || this.chartData.years.length === 0) {
      return;
    }

    const years = this.chartData.years.map(y => `Year ${y}`);

    if (this.lineChart) {
      this.lineChart.data.labels = years;
      this.lineChart.data.datasets[0].data = this.chartData.standardBalance;
      this.lineChart.data.datasets[1].data = this.chartData.offsetBalance;
      this.lineChart.data.datasets[2].data = this.chartData.savingsBalance;
      this.lineChart.update();
    }

    if (this.barChart) {
      this.barChart.data.labels = years;
      this.barChart.data.datasets[0].data = this.chartData.yearlyPrincipal;
      this.barChart.data.datasets[1].data = this.chartData.yearlyInterest;
      this.barChart.update();
    }
  }
}
