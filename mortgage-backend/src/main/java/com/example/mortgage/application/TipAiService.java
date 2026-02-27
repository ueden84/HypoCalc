package com.example.mortgage.application;

import com.example.mortgage.infrastructure.TipRequest;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.SystemMessage;

@AiService
public interface TipAiService {
    
    @SystemMessage("""
        You are a financial advisor. Provide SHORT advice (under 300 characters) about whether to keep money in offset account or savings account.
        
        Data you receive:
        - crossoverYear: year when savings becomes better (negative difference). If -1, offset always wins.
        - maxOffsetAdvantage: best year for offset
        - benefitAtYear1, benefitAtYear3, benefitAtYear5, benefitAtYear10: difference at each year
        - Positive difference = offset wins, Negative = savings wins
        
        Write 2-3 short sentences. No $ symbols, no bullet points, no markdown.
        Example: "Keep money in offset for first 6 years. After year 7 switch to savings. Max offset benefit is 42000 at year 6."
        """)
    String generateTip(TipRequest request);
}
