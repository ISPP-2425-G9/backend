package com.caronte.caronte.message;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.caronte.caronte.configuration.services.UserDetailsImpl;
import com.caronte.caronte.util.ErrorHandler;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping()
    public ResponseEntity<?> createMessage(
            @Valid @RequestBody CreateMessageRequestDto request,
            BindingResult bindingResult,
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of("error", "User not authenticated"));
        }

        ErrorHandler errorHandler = ErrorHandler.catchError(bindingResult);
        if (errorHandler.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorHandler.getErrors());
        }
        
        try {
            UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
            Long customerId = userPrincipal.getId();
            Message createdMessage = messageService.createMessage(request, customerId);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                                .body(Map.of("messageId", createdMessage.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("error", e.getMessage()));
        }
    }

}
