package com.caronte.caronte.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

public class RegexContantsTest {

    @Test
    void testZipCodeRegex() {
        assertTrue(Pattern.matches(RegexContants.REGEX_ZIP_CODE, "12345"));
        assertFalse(Pattern.matches(RegexContants.REGEX_ZIP_CODE, "1234"));
        assertFalse(Pattern.matches(RegexContants.REGEX_ZIP_CODE, "123456"));
        assertFalse(Pattern.matches(RegexContants.REGEX_ZIP_CODE, "abcde"));
    }

    @Test
    void testDniRegex() {
        assertTrue(Pattern.matches(RegexContants.REGEX_DNI, "12345678Z"));
        assertFalse(Pattern.matches(RegexContants.REGEX_DNI, "1234567Z"));
        assertFalse(Pattern.matches(RegexContants.REGEX_DNI, "12345678z"));
        assertFalse(Pattern.matches(RegexContants.REGEX_DNI, "123456789"));
    }

    @Test
    void testNifRegex() {
        assertTrue(Pattern.matches(RegexContants.REGEX_NIF, "A1234567B"));
        assertFalse(Pattern.matches(RegexContants.REGEX_NIF, "Z1234567B"));
        assertFalse(Pattern.matches(RegexContants.REGEX_NIF, "A1234567"));
        assertFalse(Pattern.matches(RegexContants.REGEX_NIF, "AA234567B"));
    }

    @Test
    void testEmailRegex() {
        assertTrue(Pattern.matches(RegexContants.REGEX_EMAIL, "test@example.com"));
        assertTrue(Pattern.matches(RegexContants.REGEX_EMAIL, "user.name+tag+sorting@example.co.uk"));
        assertFalse(Pattern.matches(RegexContants.REGEX_EMAIL, "plainaddress"));
        assertFalse(Pattern.matches(RegexContants.REGEX_EMAIL, "missingatsign.com"));
        assertFalse(Pattern.matches(RegexContants.REGEX_EMAIL, "test@.com"));
    }

    @Test
    void testTelephoneRegex() {
        assertTrue(Pattern.matches(RegexContants.REGEX_TELEPHONE, "123456789"));
        assertFalse(Pattern.matches(RegexContants.REGEX_TELEPHONE, "12345678"));
        assertFalse(Pattern.matches(RegexContants.REGEX_TELEPHONE, "1234567890"));
        assertFalse(Pattern.matches(RegexContants.REGEX_TELEPHONE, "12345678A"));
    }

    @Test
    void testRgbRegex() {
        assertTrue(Pattern.matches(RegexContants.REGEX_RGB, "0,0,0"));
        assertTrue(Pattern.matches(RegexContants.REGEX_RGB, "255,255,255"));
        assertTrue(Pattern.matches(RegexContants.REGEX_RGB, "128,128,128"));
        assertFalse(Pattern.matches(RegexContants.REGEX_RGB, "256,0,0"));
        assertFalse(Pattern.matches(RegexContants.REGEX_RGB, "123,45"));
        assertFalse(Pattern.matches(RegexContants.REGEX_RGB, "12,34,56,78"));
        assertFalse(Pattern.matches(RegexContants.REGEX_RGB, "12,34"));
    }
}
