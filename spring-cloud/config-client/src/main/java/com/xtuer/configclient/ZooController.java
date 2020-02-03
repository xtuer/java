package com.xtuer.configclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ZooController {
    @Value("${username}")
    private String username;

    @Value("${password}")
    private String password;

    @GetMapping("/")
    public String index() {
        return username + " : " + password;
    }
}
