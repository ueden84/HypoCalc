import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule, AbstractControl, ValidationErrors } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { MatSliderModule } from '@angular/material/slider';
import { MatRadioModule } from '@angular/material/radio';
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
    MatSliderModule,
    MatRadioModule
  ],
  templateUrl: './mortgage-calculator.component.html',
  styleUrls: ['./mortgage-calculator.component.scss']
})
export class MortgageCalculatorComponent implements OnInit {
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
      years: [30, [Validators.required, Validators.min(1), Validators.max(30)]],
      offsetAmount: [0, [Validators.min(0), this.offsetValidator.bind(this)]],
      offsetMode: ['reduceAmount'],
      offsetRatePercent: [0, [Validators.min(0), Validators.max(20), this.offsetRateValidator.bind(this)]]
    });
  }

  ngOnInit(): void {
    this.mortgageForm.get('principal')?.valueChanges.subscribe((principal) => {
      const offsetControl = this.mortgageForm.get('offsetAmount');
      const currentOffset = offsetControl?.value;
      if (principal && currentOffset > principal) {
        offsetControl?.setValue(principal, { emitEvent: false });
      }
    });

    this.mortgageForm.get('annualRatePercent')?.valueChanges.subscribe((annualRate) => {
      const offsetRateControl = this.mortgageForm.get('offsetRatePercent');
      if (offsetRateControl) {
        offsetRateControl.updateValueAndValidity({ emitEvent: false });
      }
    });
  }

  offsetValidator(control: AbstractControl): ValidationErrors | null {
    const offset = control.value;
    const principal = this.mortgageForm?.get('principal')?.value;
    if (offset && principal && offset > principal) {
      return { max: true };
    }
    return null;
  }

  offsetRateValidator(control: AbstractControl): ValidationErrors | null {
    const offsetRate = control.value;
    const annualRate = this.mortgageForm?.get('annualRatePercent')?.value;
    if (offsetRate !== null && offsetRate !== undefined && annualRate !== null && annualRate !== undefined) {
      if (offsetRate < annualRate) {
        return { minRate: true };
      }
    }
    return null;
  }

  getOffsetMax(): number {
    const principal = this.mortgageForm.get('principal')?.value;
    if (principal && principal > 0) {
      return Math.min(principal, 20000000);
    }
    return 20000000;
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

  onInterestRateInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const rawValue = input.value;
    
    const numericValue = parseFloat(rawValue.replace(',', '.'));
    
    if (!isNaN(numericValue) && rawValue !== '') {
      this.mortgageForm.get('annualRatePercent')?.setValue(numericValue, { emitEvent: true });
    } else if (rawValue === '') {
      this.mortgageForm.get('annualRatePercent')?.setValue(null, { emitEvent: true });
    }
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

  getOffsetDisplay(): string {
    const value = this.mortgageForm.get('offsetAmount')?.value;
    if (value === null || value === undefined || value === '') {
      return '';
    }
    return new Intl.NumberFormat('cs-CZ', {
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(value);
  }

  onOffsetInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const rawValue = input.value.replace(/\s/g, '').replace(',', '');
    const numericValue = parseInt(rawValue, 10);
    
    if (!isNaN(numericValue) && rawValue !== '') {
      this.mortgageForm.get('offsetAmount')?.setValue(numericValue, { emitEvent: true });
    } else if (rawValue === '') {
      this.mortgageForm.get('offsetAmount')?.setValue(0, { emitEvent: true });
    }
  }

  onOffsetBlur(): void {
    this.mortgageForm.get('offsetAmount')?.markAsTouched();
  }

  getOffsetRateDisplay(): string {
    const value = this.mortgageForm.get('offsetRatePercent')?.value;
    if (value === null || value === undefined || value === '') {
      return '';
    }
    return this.formatCzechNumber(value);
  }

  onOffsetRateInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const rawValue = input.value;
    
    const numericValue = parseFloat(rawValue.replace(',', '.'));
    
    if (!isNaN(numericValue) && rawValue !== '') {
      this.mortgageForm.get('offsetRatePercent')?.setValue(numericValue, { emitEvent: true });
    } else if (rawValue === '') {
      this.mortgageForm.get('offsetRatePercent')?.setValue(0, { emitEvent: true });
    }
  }

  onOffsetRateBlur(): void {
    this.mortgageForm.get('offsetRatePercent')?.markAsTouched();
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

  onSubmit(): void {
    if (this.mortgageForm.valid) {
      this.loading = true;
      this.error = null;
      const hadPreviousResult = this.result !== null;
      this.result = null;

      const request: MortgageRequest = this.mortgageForm.value;

      this.mortgageService.calculateMortgage(request).subscribe({
        next: (result) => {
          this.result = result;
          this.loading = false;
          // Only scroll to results on first calculation, not when recalculating
          if (!hadPreviousResult) {
            setTimeout(() => {
              const resultsElement = document.getElementById('results-section');
              if (resultsElement) {
                resultsElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
              }
            }, 100);
          }
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
