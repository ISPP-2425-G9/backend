package com.caronte.caronte.util;

public class RegexContants {
    public static final String REGEX_ZIP_CODE = "^\\d{5}$";
    public static final String REGEX_DNI = "^\\d{8}[A-Z]$";
    public static final String REGEX_NIF = "^[ABCDEFGHJNPQRSUVW]\\d{7}[0-9A-J]$";
    public static final String REGEX_EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public static final String REGEX_TELEPHONE = "^\\d{9}$";
    public static final String REGEX_RGB = "^(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2}),(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2}),(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})$";

}
