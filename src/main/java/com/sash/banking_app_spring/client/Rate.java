package com.sash.banking_app_spring.client;

import lombok.Data;

@Data
public class Rate {
    private String currency;
    private Double rate;
}
