package com.example.aqi_project.Models;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController



public class HelloController {

    @GetMapping("/hp")
    public String hello() {
        return "Hello, World!";
    }
}