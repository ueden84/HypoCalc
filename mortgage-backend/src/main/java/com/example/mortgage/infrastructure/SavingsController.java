package com.example.mortgage.infrastructure;

import com.example.mortgage.application.SavingsCalculationUseCase;
import com.example.mortgage.domain.SavingsResult;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/savings")
@CrossOrigin(origins = "http://localhost:4200")
public class SavingsController {
    
    private final SavingsCalculationUseCase useCase;
    
    public SavingsController(SavingsCalculationUseCase useCase) {
        this.useCase = useCase;
    }
    
    @PostMapping("/calculate")
    public ResponseEntity<SavingsResponse> calculate(@Valid @RequestBody SavingsRequest request) {
        SavingsResult result = useCase.execute(
            request.initialAmount(),
            request.monthlyContribution(),
            request.annualInterestRatePercent(),
            request.taxRatePercent(),
            request.periodicity(),
            request.years()
        );
        
        SavingsResponse response = new SavingsResponse(
            result.initialAmount(),
            result.monthlyContribution(),
            result.totalContributions(),
            result.totalInterestEarned(),
            result.totalTaxPaid(),
            result.totalSaved(),
            result.effectiveYears()
        );
        
        return ResponseEntity.ok(response);
    }
}
