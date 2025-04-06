package com.caronte.caronte.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class AESCipher {
    
    @Value("${security.aes.secret-key}")
    private String secretKey;

    public String encrypt(String data) {
        try {
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            return null;
        }
    }

    public String decrypt(String encryptedData) {
        try {
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(original);
        } catch (Exception e) {
            return null;
        }
    }
}
