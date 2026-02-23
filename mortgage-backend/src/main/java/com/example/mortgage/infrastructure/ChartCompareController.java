package com.example.mortgage.infrastructure;

import com.example.mortgage.application.ChartCompareUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chart")
@CrossOrigin(origins = "http://localhost:4200")
public class ChartCompareController {
    
    private final ChartCompareUseCase useCase;
    
    public ChartCompareController(ChartCompareUseCase useCase) {
        this.useCase = useCase;
    }
    
    @PostMapping("/compare")
    public ResponseEntity<ChartCompareResponse> compare(@Valid @RequestBody ChartCompareRequest request) {
        ChartCompareResponse response = useCase.execute(request);
        return ResponseEntity.ok(response);
    }
}
