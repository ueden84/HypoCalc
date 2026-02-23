import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSliderModule } from '@angular/material/slider';
import { MatRadioModule } from '@angular/material/radio';
import { SavingsService } from '../../services/savings.service';
import { SavingsRequest, SavingsResult } from '../../models/mortgage.model';

@Component({
  selector: 'app-savings-calculator',
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
    MatSliderModule,
    MatRadioModule
  ],
  templateUrl: './savings-calculator.component.html',
  styleUrls: ['./savings-calculator.component.scss']
})
export class SavingsCalculatorComponent implements OnInit {
  savingsForm: FormGroup;
  result: SavingsResult | null = null;
  loading = false;
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private savingsService: SavingsService
  ) {
    this.savingsForm = this.fb.group({
      initialAmount: [0, [Validators.required, Validators.min(0), Validators.max(100000000)]],
      monthlyContribution: [5000, [Validators.required, Validators.min(0), Validators.max(1000000)]],
      annualInterestRatePercent: [4.5, [Validators.required, Validators.min(0), Validators.max(20)]],
      taxRatePercent: [15, [Validators.required, Validators.min(0), Validators.max(100)]],
      periodicity: ['yearly', [Validators.required]],
      years: [10, [Validators.required, Validators.min(1), Validators.max(50)]]
    });
  }

  ngOnInit(): void {}

  onSubmit(): void {
    if (this.savingsForm.invalid) {
      this.savingsForm.markAllAsTouched();
      return;
    }

    this.loading = true;
    this.error = null;

    const request: SavingsRequest = this.savingsForm.value;

    this.savingsService.calculateSavings(request).subscribe({
      next: (result) => {
        this.result = result;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.message || 'An error occurred';
        this.loading = false;
      }
    });
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('cs-CZ', {
      style: 'currency',
      currency: 'CZK',
      minimumFractionDigits: 0,
      maximumFractionDigits: 2
    }).format(value);
  }

  getInitialAmountDisplay(): string {
    const value = this.savingsForm.get('initialAmount')?.value;
    if (value === null || value === undefined || value === '') {
      return '';
    }
    if (typeof value === 'string') {
      return value;
    }
    return new Intl.NumberFormat('cs-CZ', {
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(value);
  }

  onInitialAmountInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const rawValue = input.value.replace(/\s/g, '').replace(',', '');
    
    if (rawValue === '') {
      this.savingsForm.get('initialAmount')?.setValue(null, { emitEvent: true });
    } else if (/^\d+$/.test(rawValue)) {
      this.savingsForm.get('initialAmount')?.setValue(parseInt(rawValue, 10), { emitEvent: true });
    } else {
      this.savingsForm.get('initialAmount')?.setValue(rawValue, { emitEvent: true });
    }
    this.savingsForm.get('initialAmount')?.updateValueAndValidity({ emitEvent: true });
  }

  onInitialAmountBlur(): void {
    const control = this.savingsForm.get('initialAmount');
    control?.markAsTouched();
    const value = control?.value;
    if (value === null || value === undefined || isNaN(Number(value)) || typeof value === 'string') {
      control?.setValue(0);
    }
    control?.updateValueAndValidity({ emitEvent: true });
  }

  getMonthlyContributionDisplay(): string {
    const value = this.savingsForm.get('monthlyContribution')?.value;
    if (value === null || value === undefined || value === '') {
      return '';
    }
    if (typeof value === 'string') {
      return value;
    }
    return new Intl.NumberFormat('cs-CZ', {
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(value);
  }

  onMonthlyContributionInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const rawValue = input.value.replace(/\s/g, '').replace(',', '');
    
    if (rawValue === '') {
      this.savingsForm.get('monthlyContribution')?.setValue(null, { emitEvent: true });
    } else if (/^\d+$/.test(rawValue)) {
      this.savingsForm.get('monthlyContribution')?.setValue(parseInt(rawValue, 10), { emitEvent: true });
    } else {
      this.savingsForm.get('monthlyContribution')?.setValue(rawValue, { emitEvent: true });
    }
    this.savingsForm.get('monthlyContribution')?.updateValueAndValidity({ emitEvent: true });
  }

  onMonthlyContributionBlur(): void {
    const control = this.savingsForm.get('monthlyContribution');
    control?.markAsTouched();
    const value = control?.value;
    if (value === null || value === undefined || isNaN(Number(value)) || typeof value === 'string') {
      control?.setValue(0);
    }
    control?.updateValueAndValidity({ emitEvent: true });
  }

  getInterestRateDisplay(): string {
    const value = this.savingsForm.get('annualInterestRatePercent')?.value;
    if (value === null || value === undefined || value === '') {
      return '';
    }
    if (typeof value === 'string') {
      return value;
    }
    return new Intl.NumberFormat('cs-CZ', {
      minimumFractionDigits: 0,
      maximumFractionDigits: 2
    }).format(value);
  }

  onInterestRateInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const rawValue = input.value.replace(',', '.');
    
    if (rawValue === '') {
      this.savingsForm.get('annualInterestRatePercent')?.setValue(null, { emitEvent: true });
    } else if (/^\d*\.?\d+$/.test(rawValue)) {
      this.savingsForm.get('annualInterestRatePercent')?.setValue(parseFloat(rawValue), { emitEvent: true });
    } else {
      this.savingsForm.get('annualInterestRatePercent')?.setValue(rawValue, { emitEvent: true });
    }
    this.savingsForm.get('annualInterestRatePercent')?.updateValueAndValidity({ emitEvent: true });
  }

  onInterestRateBlur(): void {
    const control = this.savingsForm.get('annualInterestRatePercent');
    control?.markAsTouched();
    const value = control?.value;
    if (value === null || value === undefined || isNaN(Number(value)) || typeof value === 'string') {
      control?.setValue(0);
    }
    control?.updateValueAndValidity({ emitEvent: true });
  }

  getTaxRateDisplay(): string {
    const value = this.savingsForm.get('taxRatePercent')?.value;
    if (value === null || value === undefined || value === '') {
      return '';
    }
    if (typeof value === 'string') {
      return value;
    }
    return new Intl.NumberFormat('cs-CZ', {
      minimumFractionDigits: 0,
      maximumFractionDigits: 2
    }).format(value);
  }

  onTaxRateInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const rawValue = input.value.replace(',', '.');
    
    if (rawValue === '') {
      this.savingsForm.get('taxRatePercent')?.setValue(null, { emitEvent: true });
    } else if (/^\d*\.?\d+$/.test(rawValue)) {
      this.savingsForm.get('taxRatePercent')?.setValue(parseFloat(rawValue), { emitEvent: true });
    } else {
      this.savingsForm.get('taxRatePercent')?.setValue(rawValue, { emitEvent: true });
    }
    this.savingsForm.get('taxRatePercent')?.updateValueAndValidity({ emitEvent: true });
  }

  onTaxRateBlur(): void {
    const control = this.savingsForm.get('taxRatePercent');
    control?.markAsTouched();
    const value = control?.value;
    if (value === null || value === undefined || isNaN(Number(value)) || typeof value === 'string') {
      control?.setValue(15);
    }
    control?.updateValueAndValidity({ emitEvent: true });
  }

  getYearsDisplay(): string {
    const value = this.savingsForm.get('years')?.value;
    if (value === null || value === undefined || value === '') {
      return '';
    }
    if (typeof value === 'string') {
      return value;
    }
    return value.toString();
  }

  isInvalidNumber(value: any): boolean {
    return typeof value === 'string' && value !== '';
  }

  onYearsInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const rawValue = input.value;
    
    if (rawValue === '') {
      this.savingsForm.get('years')?.setValue(null, { emitEvent: true });
    } else if (/^\d+$/.test(rawValue)) {
      this.savingsForm.get('years')?.setValue(parseInt(rawValue, 10), { emitEvent: true });
    } else {
      this.savingsForm.get('years')?.setValue(rawValue, { emitEvent: true });
    }
    this.savingsForm.get('years')?.updateValueAndValidity({ emitEvent: true });
  }

  onYearsBlur(): void {
    const control = this.savingsForm.get('years');
    control?.markAsTouched();
    const value = control?.value;
    if (value === null || value === undefined || isNaN(Number(value)) || typeof value === 'string') {
      control?.setValue(1);
    }
    control?.updateValueAndValidity({ emitEvent: true });
  }
}
