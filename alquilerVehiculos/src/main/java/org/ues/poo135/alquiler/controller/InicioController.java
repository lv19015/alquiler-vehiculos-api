package org.ues.poo135.alquiler.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InicioController {

    @GetMapping("/")
    public String inicio() {
        return "Test 1: Sistema de alquiler de vehículos funcionando con Spring Boot :)";
    }
}