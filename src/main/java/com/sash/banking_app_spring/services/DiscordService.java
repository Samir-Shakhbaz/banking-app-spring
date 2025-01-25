//package com.sash.banking_app_spring.services;
//
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//@Service
//public class DiscordService {
//    private final WebClient webClient;
//
//    public DiscordService(WebClient.Builder webClientBuilder) {
//
//        this.webClient = webClientBuilder.baseUrl("http://34.122.239.19:3000").build();
//    }
//
//    public Mono<String> createGroup(String guildId, String groupName) {
//        // Using uri with a dynamic guildId
//        return webClient.post()
//                .uri("/guilds/{guildId}/channels", guildId) // Dynamically inserting guildId
//                .bodyValue(new GroupRequest(groupName)) // Only sending groupName as body
//                .retrieve()
//                .bodyToMono(String.class);
//    }
//
//    private record GroupRequest(String name) {}  // Only passing name of the group here
//}
//
