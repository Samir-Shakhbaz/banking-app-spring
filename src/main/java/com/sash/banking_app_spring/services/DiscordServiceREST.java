package com.sash.banking_app_spring.services;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
public class DiscordServiceREST {

    public String createGroup(String guildId, String groupName) throws Exception {
        String command = "node /Users/sam/Desktop/discord-api-library/index.js " + guildId + " " + groupName;

        Process process = Runtime.getRuntime().exec(command);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line);
        }

        process.waitFor();

        return output.toString();
    }

}
