package com.leadestate.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    // Endpoint: /
    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
                "message", "LeadEstate API is running 🚀",
                "status", "OK"
        );
    }
}