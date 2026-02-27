package com.example.mortgage.infrastructure;

import com.example.mortgage.application.TipAiService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class TipController {
    
    private TipAiService tipAiService;
    
    public TipController(TipAiService tipAiService) {
        this.tipAiService = tipAiService;
    }
    
    @PostMapping("/tips")
    public ResponseEntity<TipResponse> getTip(@Valid @RequestBody TipRequest request) {
        try {
            String tip = tipAiService.generateTip(request);
            return ResponseEntity.ok(new TipResponse(tip));
        } catch (Exception e) {
            return ResponseEntity.ok(new TipResponse("AI tip is temporarily unavailable. Please try again later." + e));
        }
    }
}
