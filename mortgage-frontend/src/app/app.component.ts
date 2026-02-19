import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MortgageCalculatorComponent } from './components/mortgage-calculator/mortgage-calculator.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, MortgageCalculatorComponent],
  template: `
    <div class="app-container">
      <app-mortgage-calculator></app-mortgage-calculator>
    </div>
  `,
  styles: [`
    .app-container {
      min-height: 100vh;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      padding: 20px;
    }
  `]
})
export class AppComponent {
  title = 'mortgage-frontend';
}
