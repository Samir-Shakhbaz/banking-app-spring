package com.sash.banking_app_spring.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OpenAIResponseParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String parseResponse(String jsonResponse) {
        try {
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode messageNode = root.path("choices").get(0).path("message").path("content");
            return messageNode.asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error parsing response";
        }
    }
}
