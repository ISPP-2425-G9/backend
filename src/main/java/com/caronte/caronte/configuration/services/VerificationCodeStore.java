package com.caronte.caronte.configuration.services;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

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
            @Value("${app.domain}") String domain) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.domain = domain;
    }

    public void saveCode(String email) throws Exception {
        ResponseThrow.checkOrBadRequest(userRepository.existsByEmail(email), "No existe usuario con email " + email);
        String code = generateRandomCode();
        saveCode(email, code);
        String subject = "Recuperación de contraseña en CARONTE";
        String body = String.format("""
            Este es un correo de verificación para recuperar la contraseña de tu cuenta en %s
            El código de verificación es el siguiente: %s
            """, domain, code);
        emailService.sendEmail(email, subject, body);
    }

    private void saveCode(String email, String code) {
        CODE_MAP.put(email, new CodeData(code, LocalDateTime.now().plusMinutes(TIME)));
    }

    public String getCode(String email) {
        ResponseThrow.checkOrBadRequest(userRepository.existsByEmail(email), "No existe usuario con email " + email);
        CodeData data = CODE_MAP.get(email);
        if(data == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se ha enviado correo de recuperación");
        
        ResponseThrow.checkOrBadRequest(data.expirationTime().isAfter(LocalDateTime.now()), "Tiempo de recuperación expirado");
        return data.code();
    }

    public void verifyCode(RememberPasswordRequest rememberPasswordRequest) {
        String code = getCode(rememberPasswordRequest.email());
        ResponseThrow.checkOrBadRequest(Objects.equals(code, rememberPasswordRequest.code()), 
                                 "Código de recuperación incorrecto");
    }

    public void removeCode(String email) {
        this.CODE_MAP.remove(email);
    }

    @Scheduled(fixedRate = 60000)
    public void cleanupExpiredCodes() {
        LocalDateTime now = LocalDateTime.now();
        CODE_MAP.entrySet().removeIf(entry -> entry.getValue().expirationTime().isBefore(now));
    }

    private String generateRandomCode() {
        int randomNumber = (int)(Math.random() * 100_000);
        return String.format("%05d", randomNumber);
    }

    private record CodeData(String code, LocalDateTime expirationTime) {
    }
}
