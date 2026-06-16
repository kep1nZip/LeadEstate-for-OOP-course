package com.leadestate.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HelloController {

    // Endpoint root: /api
    @GetMapping
    public Map<String, String> root() {
        return Map.of("message", "API jalan");
    }

    // Endpoint tambahan: /api/hello
    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("message", "Hello dari backend!");
    }
}