package com.caronte.caronte.configuration.services;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class VerificationCodeStore {
   
    private final Map<String, CodeData> codeMap = new ConcurrentHashMap<>();

    public void saveCode(String email, String code) {
        codeMap.put(email, new CodeData(code, LocalDateTime.now().plusMinutes(10)));
    }

    public String getCode(String email) {
        CodeData data = codeMap.get(email);
        if (data != null && data.expirationTime().isAfter(LocalDateTime.now())) {
            return data.code();
        }
        return null;
    }

    @Scheduled(fixedRate = 60000)
    public void cleanupExpiredCodes() {
        LocalDateTime now = LocalDateTime.now();
        codeMap.entrySet().removeIf(entry -> entry.getValue().expirationTime().isBefore(now));
    }

    private record CodeData(String code, LocalDateTime expirationTime) {
    }
}
