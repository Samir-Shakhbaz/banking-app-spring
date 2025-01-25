//package com.sash.banking_app_spring.controllersREST;
//
//import com.sash.banking_app_spring.services.DiscordService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/discord")
//public class DiscordController {
//
//    @Autowired
//    private DiscordService discordService;
//
//    @PostMapping("/create-group")
//    public ResponseEntity<String> createGroup(@RequestParam String guildId, @RequestParam String groupName) {
//        System.out.println("Received guildId: " + guildId); // Debugging line
//        System.out.println("Received groupName: " + groupName); // Debugging line
//        return ResponseEntity.ok(discordService.createGroup(guildId, groupName).block());
//    }
//
//    @GetMapping("/test")
//    public String testEndpoint() {
//        return "Discord API is working!";
//    }
//
//}