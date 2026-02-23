export interface MortgageRequest {
  principal: number;
  annualRatePercent: number;
  years: number;
  offsetAmount?: number;
  offsetMode?: 'reduceAmount' | 'reduceTerm';
  offsetRatePercent?: number;
}

export interface MortgageResult {
  monthlyPayment: number;
  totalPaid: number;
  totalInterest: number;
  effectivePrincipal?: number;
  effectiveYears?: number;
  totalOffsetInterestEarned?: number;
}

export interface SavingsRequest {
  initialAmount: number;
  monthlyContribution: number;
  annualInterestRatePercent: number;
  taxRatePercent: number;
  periodicity: 'yearly' | 'monthly';
  years: number;
}

export interface SavingsResult {
  initialAmount: number;
  monthlyContribution: number;
  totalContributions: number;
  totalInterestEarned: number;
  totalTaxPaid: number;
  totalSaved: number;
  effectiveYears?: number;
}
