package com.sample.anamoly.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("health")
    String health()
    {
        return "OK";
    }

}
