package com.sash.banking_app_spring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIConfig {

    @Value("${OPENAI_API_KEY}")
    private String apiKey;

    @Bean
    public String openAiApiKey() {
        return apiKey;
    }

}
