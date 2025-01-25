package com.sash.banking_app_spring.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.logging.Logger;
import java.util.Base64;

@Service
public class FriendicaService {

    @Value("${friendica.api.url}")
    private String friendicaApiUrl;

    @Value("${friendica.api.username}")
    private String friendicaUsername;

    @Value("${friendica.api.password}")
    private String friendicaPassword;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final Logger logger = Logger.getLogger(FriendicaService.class.getName());

    public String createGroup(String name) {
        String url = friendicaApiUrl + "/friendica/group_create";

        // Encode username and password for Basic Auth
        String auth = friendicaUsername + ":" + friendicaPassword;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        String authHeader = "Basic " + encodedAuth;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = Map.of("name", name);

        logger.info("Sending request to Friendica: " + url);
        logger.info("Request body: " + requestBody.toString());

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            logger.info("Friendica Response: " + response.getBody());
            return response.getBody();
        } catch (Exception e) {
            logger.severe("Error creating Friendica group: " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }
}
