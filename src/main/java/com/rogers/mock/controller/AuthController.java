package com.rogers.mock.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authserver/oauth")
public class AuthController {

    @PostMapping("/token")
    public Map<String, Object> getToken() {

        Map<String, Object> response = new HashMap<>();

        response.put("access_token", "mock-token");
        response.put("token_type", "Bearer");
        response.put("expires_in", 3600);

        return response;
    }
}