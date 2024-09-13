package com.sash.banking_app_spring.client;

import com.sash.banking_app_spring.client.Rate;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class ExchangeRateAPIClient {

    private final String API_URL = "https://openexchangerates.org/api/latest.json?app_id=4e897b0821a54b5fb1f65c64b6f8f1c8";

    private final RestTemplate restTemplate;

    public ExchangeRateAPIClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Rate> getExchangeRates() {
       ExchangeResponse response = restTemplate.getForObject(API_URL, ExchangeResponse.class);
        log.debug(response);

//        Map<String, Double> rates = (Map<String, Double>) response.get("rates");
        List<Rate> rateList = new ArrayList<>();

        for (Map.Entry<String, Double> entry : response.getRates().entrySet()) {
            log.debug(entry);
            Rate rate = new Rate();
            rate.setCurrency(entry.getKey());
            rate.setRate((Double)entry.getValue());
            rateList.add(rate);
        }

        return rateList;
    }
}

