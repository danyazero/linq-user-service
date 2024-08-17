package org.zero.userservice.utils;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class SHAEncoder {
    public static String apply(String originalString) {
        return apply(originalString, Encryption.SHA256);
    }

    public static String apply(String originalString, Encryption encryption) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance(encryption.getTitle());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 encoding exception");
        }
        byte[] encodedHash = digest.digest(
                originalString.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder()
                .encodeToString(encodedHash);
    }

    public enum Encryption {
        SHA256("SHA-256"),
        SHA1("SHA-1");

        private String title;

        Encryption(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }
}
