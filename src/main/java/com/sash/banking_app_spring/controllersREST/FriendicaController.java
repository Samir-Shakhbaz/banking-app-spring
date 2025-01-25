package com.sash.banking_app_spring.controllersREST;

import com.sash.banking_app_spring.services.FriendicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/friendica")
public class FriendicaController {

    @Autowired
    private FriendicaService friendicaService;

    @GetMapping("/groups/create")
    public String showCreateGroupPage() {
        return "create-friendica-group";
    }

//    @PostMapping("/groups")
//    public String createFriendicaGroup(@RequestParam String name) {
//        return friendicaService.createGroup(name);
//    }

    @PostMapping("/groups")
    public String createFriendicaGroup(@RequestParam String name, Model model) {
        String response = friendicaService.createGroup(name);
        model.addAttribute("message", "Group Successfully Created!");
        return "create-friendica-group";
    }

    @GetMapping("/test")
    public String testConnection() {
        return "Friendica API Controller is working!";
    }
}
