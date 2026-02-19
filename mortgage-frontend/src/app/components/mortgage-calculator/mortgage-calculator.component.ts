import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { MortgageService } from '../../services/mortgage.service';
import { MortgageRequest, MortgageResult } from '../../models/mortgage.model';

@Component({
  selector: 'app-mortgage-calculator',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatTableModule
  ],
  templateUrl: './mortgage-calculator.component.html',
  styleUrls: ['./mortgage-calculator.component.scss']
})
export class MortgageCalculatorComponent {
  mortgageForm: FormGroup;
  result: MortgageResult | null = null;
  loading = false;
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private mortgageService: MortgageService
  ) {
    this.mortgageForm = this.fb.group({
      principal: ['', [Validators.required, Validators.min(0.01)]],
      annualRatePercent: ['', [Validators.required, Validators.min(0)]],
      years: ['', [Validators.required, Validators.min(1)]]
    });
  }

  onSubmit(): void {
    if (this.mortgageForm.valid) {
      this.loading = true;
      this.error = null;
      this.result = null;

      const request: MortgageRequest = this.mortgageForm.value;

      this.mortgageService.calculateMortgage(request).subscribe({
        next: (result) => {
          this.result = result;
          this.loading = false;
        },
        error: (err) => {
          this.error = err.error?.error || 'An error occurred while calculating';
          this.loading = false;
        }
      });
    }
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('cs-CZ', {
      style: 'currency',
      currency: 'CZK'
    }).format(value);
  }
}
