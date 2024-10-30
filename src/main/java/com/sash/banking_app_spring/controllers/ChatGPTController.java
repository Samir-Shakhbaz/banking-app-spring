package com.sash.banking_app_spring.controllers;

import com.sash.banking_app_spring.services.ChatGPTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ChatGPTController {

    @Autowired
    private ChatGPTService chatGPTService;

    @GetMapping("/chatgpt")
    public String showChatGPTPage() {
        return "chatgpt"; // This should correspond to the chatgpt.html template
    }

    @PostMapping("/askgpt")
    public String requestAI(@RequestParam("content") String content, Model model) {
        try {
            String answer = chatGPTService.askGPT(content);
            model.addAttribute("response", answer);
        } catch (Exception e) {
            model.addAttribute("response", "Error fetching joke: " + e.getMessage());
        }
        return "chatgpt";
    }
}

