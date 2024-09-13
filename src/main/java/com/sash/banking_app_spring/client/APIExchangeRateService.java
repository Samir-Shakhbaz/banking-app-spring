//package com.sash.banking_app_spring.client;
//
//import com.google.gson.Gson;
//import org.springframework.stereotype.Service;
//
//import java.io.InputStreamReader;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//public class APIExchangeRateService {
//
//    private final String API_URL = "https://openexchangerates.org/api/latest.json?app_id=4e897b0821a54b5fb1f65c64b6f8f1c8"; // Replace with your actual App ID
//
//    public Map<String, Double> getExchangeRates() {
//        Map<String, Double> conversionRates = new HashMap<>();
//
//        try {
//            // Set up the connection
//            URL url = new URL(API_URL);
//            HttpURLConnection request = (HttpURLConnection) url.openConnection();
//            request.connect();
//
//            // Use GSON to parse the JSON response directly into the ApiExchangeRateResponse class
//            InputStream inputStream = (InputStream) request.getContent();
//            InputStreamReader reader = new InputStreamReader(inputStream);
//
//            // Parse the response
//            Gson gson = new Gson();
//           APIExchngeRateResponse response = gson.fromJson(reader, APIExchngeRateResponse.class);
//
//            // Extract conversion rates
//            conversionRates = response.getRates();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return conversionRates;
//    }
//}
