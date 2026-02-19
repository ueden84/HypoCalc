export interface MortgageRequest {
  principal: number;
  annualRatePercent: number;
  years: number;
}

export interface MortgageResult {
  monthlyPayment: number;
  totalPaid: number;
  totalInterest: number;
}
