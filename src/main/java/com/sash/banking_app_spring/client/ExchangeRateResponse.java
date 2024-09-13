package com.sash.banking_app_spring.client;

import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Data
public class ExchangeRateResponse {
    private String disclaimer;
    private String license;
    private long timestamp;
    private String base;
    @Getter
    private Map<String, Double> rates;
    List<Rate> ratesList;

}