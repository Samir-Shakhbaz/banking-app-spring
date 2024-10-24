//package com.sash.banking_app_spring.client;
//
//import com.google.gson.JsonElement;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
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
//public class ExchangeRateService {
//
//    private final String API_URL = "https://v6.exchangerate-api.com/v6/ade4b0083b752f02b6eb8154/latest/USD";
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
//            // Parse the JSON response using GSON
//            JsonElement root = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent()));
//            JsonObject jsonObject = root.getAsJsonObject();
//
//            // Check the result status
//            String result = jsonObject.get("result").getAsString();
//            if ("success".equals(result)) {
//                // Extract conversion rates
//                JsonObject rates = jsonObject.getAsJsonObject("conversion_rates");
//                for (Map.Entry<String, JsonElement> entry : rates.entrySet()) {
//                    conversionRates.put(entry.getKey(), entry.getValue().getAsDouble());
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return conversionRates;
//    }
//}
