package com.example.mortgage.infrastructure;

import com.example.mortgage.application.ChartCalculationUseCase;
import com.example.mortgage.infrastructure.ChartRequest.ChartMortgageRequest;
import com.example.mortgage.infrastructure.ChartRequest.ChartSavingsRequest;
import com.example.mortgage.infrastructure.ChartResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chart")
@CrossOrigin(origins = "http://localhost:4200")
public class ChartController {
    
    private final ChartCalculationUseCase useCase;
    
    public ChartController(ChartCalculationUseCase useCase) {
        this.useCase = useCase;
    }
    
    @PostMapping("/calculate")
    public ResponseEntity<ChartResponse> calculate(@Valid @RequestBody ChartRequest request) {
        ChartResponse response = useCase.execute(request.mortgage(), request.savings());
        return ResponseEntity.ok(response);
    }
}
