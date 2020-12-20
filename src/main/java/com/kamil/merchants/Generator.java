package com.kamil.merchants;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class Generator {

    public String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
