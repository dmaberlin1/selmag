package com.dmadev.manager.tools;

import net.minidev.json.JSONUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class passwordEncoder {
    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("password"));
    }
}
