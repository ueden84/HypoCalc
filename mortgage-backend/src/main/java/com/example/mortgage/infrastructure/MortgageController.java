package com.example.mortgage.infrastructure;

import com.example.mortgage.application.MortgageCalculationUseCase;
import com.example.mortgage.domain.MortgageResult;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mortgage")
@CrossOrigin(origins = "http://localhost:4200")
public class MortgageController {
    
    private final MortgageCalculationUseCase useCase;
    
    public MortgageController(MortgageCalculationUseCase useCase) {
        this.useCase = useCase;
    }
    
    @PostMapping("/calculate")
    public ResponseEntity<MortgageResponse> calculate(@Valid @RequestBody MortgageRequest request) {
        MortgageResult result = useCase.execute(
            request.principal(),
            request.annualRatePercent(),
            request.years()
        );
        
        MortgageResponse response = new MortgageResponse(
            result.monthlyPayment(),
            result.totalPaid(),
            result.totalInterest()
        );
        
        return ResponseEntity.ok(response);
    }
}
