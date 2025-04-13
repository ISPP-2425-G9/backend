package com.caronte.caronte.configuration.services;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.caronte.caronte.auth.payload.response.RememberPasswordRequest;
import com.caronte.caronte.user.UserRepository;
import com.caronte.caronte.util.exceptions.ResponseThrow;

@Component
public class VerificationCodeStore {
   
    private final Map<String, CodeData> CODE_MAP = new ConcurrentHashMap<>();
    private final Integer TIME = 10;

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final String domain;

    public VerificationCodeStore(UserRepository userRepository, EmailService emailService, 
            @Value("app.domain") String domain) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.domain = domain;
    }

    public void saveCode(String email) throws Exception {
        ResponseThrow.checkOrBadRequest(userRepository.existsByEmail(email), "There isn't user with email: " + email);
        String code = generateUniqueRandomCode();
        saveCode(email, code);
        String subject = "Recuperación de correo";
        String body = String.format("""
            Ha recibido correo de verificación para recuperar contraseña en la url: %s
            El código de verificación es el siguiente: %s
            """, domain, code);
        emailService.sendEmail(email, subject, body);
    }

    private void saveCode(String email, String code) {
        CODE_MAP.put(email, new CodeData(code, LocalDateTime.now().plusMinutes(TIME)));
    }

    public String getCode(String email) {
        ResponseThrow.checkOrBadRequest(userRepository.existsByEmail(email), "There isn't user with email: " + email);
        CodeData data = CODE_MAP.get(email);
        if (data != null && data.expirationTime().isAfter(LocalDateTime.now())) {
            return data.code();
        }
        return null;
    }

    public boolean verifyCode(RememberPasswordRequest rememberPasswordRequest) {
        String code = getCode(rememberPasswordRequest.email());
        return Objects.equals(code, rememberPasswordRequest.code());
    }

    @Scheduled(fixedRate = 60000)
    public void cleanupExpiredCodes() {
        LocalDateTime now = LocalDateTime.now();
        CODE_MAP.entrySet().removeIf(entry -> entry.getValue().expirationTime().isBefore(now));
    }

    private String generateUniqueRandomCode() {
        int randomNumber = (int)(Math.random() * 100_000); // 00000 - 99999
        return String.format("%05d", randomNumber);
    }

    private record CodeData(String code, LocalDateTime expirationTime) {
    }
}
