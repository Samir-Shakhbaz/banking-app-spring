package com.sash.banking_app_spring.controllersREST;

import com.sash.banking_app_spring.services.DiscordServiceREST;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/discord")
public class DiscordControllerREST {

    @Autowired
    private DiscordServiceREST discordServiceREST;

    @PostMapping("/create-group")
    public ResponseEntity<String> createGroup(@RequestParam String guildId, @RequestParam String groupName) {
        try {

            String result = discordServiceREST.createGroup(guildId, groupName);

            return ResponseEntity.ok(result);
        } catch (Exception e) {

            return ResponseEntity.status(500).body("Failed to create group: " + e.getMessage());
        }
    }

    @GetMapping("/test")
    public String testEndpoint() {
        return "Discord API is working!";
    }
}
