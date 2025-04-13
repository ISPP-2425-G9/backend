package com.caronte.caronte.util;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AESCipherTest {

    private AESCipher aesCipher;

    @BeforeEach
    void setUp() throws Exception {
        aesCipher = new AESCipher();
        Field secretKeyField = AESCipher.class.getDeclaredField("secretKey");
        secretKeyField.setAccessible(true);
        secretKeyField.set(aesCipher, "1234567890123456");
    }

    @Test
    void testEncryptDecrypt() {
        String originalText = "Hello, World!";
        String encrypted = aesCipher.encrypt(originalText);
        assertNotNull(encrypted, "El resultado de la encriptación no debe ser nulo");
        String decrypted = aesCipher.decrypt(encrypted);
        assertEquals(originalText, decrypted, "El texto desencriptado debe coincidir con el original");
    }

    @Test
    void testEncryptEmptyString() {
        String empty = "";
        String encrypted = aesCipher.encrypt(empty);
        assertNotNull(encrypted, "La encriptación de cadena vacía no debe ser nula");
        String decrypted = aesCipher.decrypt(encrypted);
        assertEquals(empty, decrypted, "El texto desencriptado de una cadena vacía debe ser vacío");
    }

    @Test
    void testDecryptInvalidData() {
        String invalidData = "not a valid base64 string";
        String decrypted = aesCipher.decrypt(invalidData);
        assertNull(decrypted, "La desencriptación de datos inválidos debe retornar null");
    }

    @Test
    void testEncryptNull() {
        String result = aesCipher.encrypt(null);
        assertNull(result, "La encriptación de null debe retornar null");
    }

    @Test
    void testDecryptNull() {
        String result = aesCipher.decrypt(null);
        assertNull(result, "La desencriptación de null debe retornar null");
    }
}
