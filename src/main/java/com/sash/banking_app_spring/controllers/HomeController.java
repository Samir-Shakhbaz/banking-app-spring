package com.sash.banking_app_spring.controllers;

import com.sash.banking_app_spring.client.ExchangeRateAPIClient;
import com.sash.banking_app_spring.client.Rate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ExchangeRateAPIClient exchangeRateAPIClient;

    @GetMapping("/")
    public String showIndexPage(Model model) {
        List<Rate> exchangeRates = exchangeRateAPIClient.getExchangeRates();

        // Pass the rates to the view
        model.addAttribute("exchangeRates", exchangeRates);

        return "index";
    }


}
