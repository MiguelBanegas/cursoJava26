package com.punto_venta.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppConfigController {

    @Value("${app.api.url}")
    private String apiUrl;

    @GetMapping(value = "/config.js", produces = "application/javascript")
    public String config() {
        return "window.APP_CONFIG = { apiUrl: " + toJsString(apiUrl) + " };";
    }

    private String toJsString(String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }
}
