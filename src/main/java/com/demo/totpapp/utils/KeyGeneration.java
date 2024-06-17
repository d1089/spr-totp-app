package com.demo.totpapp.utils;

import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class KeyGeneration {

    public String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        String encodedKey = base32.encodeToString(bytes);
        // Remove padding if present
        return encodedKey.replace("=", "");
    }
}
