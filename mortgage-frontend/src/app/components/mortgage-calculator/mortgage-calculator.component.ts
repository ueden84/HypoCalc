import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { MatSliderModule } from '@angular/material/slider';
import { MortgageService } from '../../services/mortgage.service';
import { MortgageRequest, MortgageResult } from '../../models/mortgage.model';

@Component({
  selector: 'app-mortgage-calculator',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatTableModule,
    MatSliderModule
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
      principal: [2000000, [Validators.required, Validators.min(0), Validators.max(20000000)]],
      annualRatePercent: [5.0, [Validators.required, Validators.min(0), Validators.max(20)]],
      years: [30, [Validators.required, Validators.min(1), Validators.max(30)]]
    });
  }

  formatCzechNumber(value: number): string {
    return new Intl.NumberFormat('cs-CZ', {
      minimumFractionDigits: 0,
      maximumFractionDigits: 2
    }).format(value);
  }

  getPrincipalDisplay(): string {
    const value = this.mortgageForm.get('principal')?.value;
    if (value === null || value === undefined || value === '') {
      return '';
    }
    return new Intl.NumberFormat('cs-CZ', {
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(value);
  }

  onPrincipalInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const rawValue = input.value.replace(/\s/g, '').replace(',', '');
    const numericValue = parseInt(rawValue, 10);
    
    if (!isNaN(numericValue) && rawValue !== '') {
      this.mortgageForm.get('principal')?.setValue(numericValue, { emitEvent: true });
    } else if (rawValue === '') {
      this.mortgageForm.get('principal')?.setValue(null, { emitEvent: true });
    }
  }

  onPrincipalBlur(): void {
    this.mortgageForm.get('principal')?.markAsTouched();
  }

  getInterestRateDisplay(): string {
    const value = this.mortgageForm.get('annualRatePercent')?.value;
    if (value === null || value === undefined || value === '') {
      return '';
    }
    return this.formatCzechNumber(value);
  }

  onInputChange(field: string, event: Event): void {
    const input = event.target as HTMLInputElement;
    const value = parseFloat(input.value);
    if (!isNaN(value)) {
      this.mortgageForm.get(field)?.setValue(value, { emitEvent: true });
    }
  }

  onInterestRateInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const rawValue = input.value;
    
    // Allow typing including comma, dot, and numbers
    const numericValue = parseFloat(rawValue.replace(',', '.'));
    
    if (!isNaN(numericValue) && rawValue !== '') {
      // Update form value (backend uses dot)
      this.mortgageForm.get('annualRatePercent')?.setValue(numericValue, { emitEvent: true });
    } else if (rawValue === '') {
      // Empty field - set to null and emit event to trigger validation
      this.mortgageForm.get('annualRatePercent')?.setValue(null, { emitEvent: true });
    }
    // For partial inputs like just ',' or '.', don't update form yet
  }

  onInterestRateBlur(): void {
    this.mortgageForm.get('annualRatePercent')?.markAsTouched();
  }

  getYearsDisplay(): string {
    const value = this.mortgageForm.get('years')?.value;
    if (value === null || value === undefined || value === '') {
      return '';
    }
    return value.toString();
  }

  onYearsInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const rawValue = input.value;
    const numericValue = parseInt(rawValue, 10);
    
    if (!isNaN(numericValue) && rawValue !== '') {
      this.mortgageForm.get('years')?.setValue(numericValue, { emitEvent: true });
    } else if (rawValue === '') {
      this.mortgageForm.get('years')?.setValue(null, { emitEvent: true });
    }
  }

  onYearsBlur(): void {
    this.mortgageForm.get('years')?.markAsTouched();
  }

  getSliderValue(field: string): number {
    const value = this.mortgageForm.get(field)?.value;
    if (value === null || value === undefined || isNaN(value)) {
      return 0;
    }
    return value;
  }

  onSliderChange(field: string, value: number): void {
    this.mortgageForm.get(field)?.setValue(value, { emitEvent: true });
  }

  onSliderNgModelChange(value: number, field: string): void {
    if (!isNaN(value)) {
      this.mortgageForm.get(field)?.setValue(value, { emitEvent: true });
    }
  }

  formatSliderValue(value: number): string {
    return this.formatCzechNumber(value);
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
