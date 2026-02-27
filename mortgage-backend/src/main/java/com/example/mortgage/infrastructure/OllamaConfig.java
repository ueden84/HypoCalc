package com.example.mortgage.infrastructure;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OllamaConfig {

    @Bean
    public ChatLanguageModel chatLanguageModel(
            @Value("${langchain4j.ollama.base-url}") String baseUrl,
            @Value("${langchain4j.ollama.model-name}") String modelName,
            @Value("${langchain4j.ollama.temperature:0.2}") double temperature,
            @Value("${langchain4j.ollama.timeout:60s}") String timeout
    ) {
        return OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(modelName)
                .temperature(temperature)
                .timeout(java.time.Duration.ofSeconds(60))
                .build();
    }
}
