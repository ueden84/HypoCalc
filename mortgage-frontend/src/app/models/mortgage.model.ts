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
