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

export interface ChartRequest {
  mortgage: MortgageRequest;
  savings: SavingsRequest;
}

export interface ChartData {
  years: number[];
  standardBalance: number[];
  offsetBalance: number[];
  savingsBalance: number[];
  yearlyPrincipal: number[];
  yearlyInterest: number[];
}

export interface ChartResponse {
  mortgage: {
    monthlyPayment: number;
    totalPaid: number;
    yearlyData: { year: number; principalPaid: number; interestPaid: number }[];
  };
  savings: {
    totalSaved: number;
    yearlyData: { year: number; balance: number }[];
  };
  chartData: ChartData;
}

export interface ChartCompareRequest {
  mortgage: {
    principal: number;
    annualRatePercent: number;
    years: number;
    offsetMode: string;
    offsetRatePercent: number;
  };
  savings: {
    initialAmount: number;
    monthlyContribution: number;
    annualInterestRatePercent: number;
    taxRatePercent: number;
    periodicity: string;
    years: number;
  };
  offsetAmount: number;
}

export interface ChartCompareResponse {
  years: number[];
  offsetBenefit: number[];
  savingsBenefit: number[];
  difference: number[];
}
