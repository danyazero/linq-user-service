package org.zero.userservice.utils;

public class IdempotencyValueProvider {

    public static String generate(String... values) {
        return String.join(".", values);
    }
}
