package org.zero.userservice.utils;

import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class Base64Encoder {
    public static String apply(String originalString) {
        return Base64.getEncoder().encodeToString(originalString.getBytes());
    }
}
