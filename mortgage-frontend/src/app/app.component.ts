import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MortgageCalculatorComponent } from './components/mortgage-calculator/mortgage-calculator.component';
import { SavingsCalculatorComponent } from './components/savings-calculator/savings-calculator.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, MortgageCalculatorComponent, SavingsCalculatorComponent],
  template: `
    <div class="app-container">
      <div class="calculators-row">
        <app-mortgage-calculator></app-mortgage-calculator>
        <app-savings-calculator></app-savings-calculator>
      </div>
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
}
