package com.sash.banking_app_spring.client;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ExchangeResponse {

    private String disclaimer;
    private String license;
    private String timestamp;
    private String base;

    private Map<String, Double> rates;

}

//  "disclaimer": "Usage subject to terms: https://openexchangerates.org/terms",
//          "license": "https://openexchangerates.org/license",
//          "timestamp": 1726178400,
//          "base": "USD",