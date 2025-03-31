package com.caronte.caronte.message;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.caronte.caronte.configuration.services.UserDetailsImpl;
import com.caronte.caronte.util.ErrorHandler;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping()
    public ResponseEntity<?> createMessage(
            @Valid @RequestBody MessageRequestDto request,
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

    @GetMapping("/{customer_id}/my_messages")
    public ResponseEntity<?> getMessagesByCustomerId(
            @PathVariable Long customer_id,
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of("error", "User not authenticated"));
        }

        try {
            UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
            Long customerId = userPrincipal.getId();
            if (!customerId.equals(customer_id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                    .body(Map.of("error", "User not authorized to access this resource"));
            }
            List<MessageRequestDto> messages = messageService.getMessagesRequestDtoByCustomerId(customerId);
            
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{message_id}")
    public ResponseEntity<?> getMessageById(
            @PathVariable Long message_id,
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of("error", "User not authenticated"));
        }

        try {
            UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
            Long customerId = userPrincipal.getId();
            Message message = messageService.getMessageById(message_id);
            MessageRequestDto messageDto = messageService.getMessageRequestDtoByMessageId(message_id, customerId);
            if (message.getCustomer().getId().equals(customerId)) {
                return ResponseEntity.ok(messageDto);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                    .body(Map.of("error", "User not authorized to access this resource"));
            }
        
        }catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getReason()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{message_id}")
    public ResponseEntity<?> updateMessage(
            @PathVariable Long message_id,
            @Valid @RequestBody MessageRequestDto request,
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
            Message updatedMessage = messageService.updateMessage(message_id, request, customerId);
            
            return ResponseEntity.ok(updatedMessage);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getReason()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{message_id}")
    public ResponseEntity<?> deleteMessage(
            @PathVariable Long message_id,
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of("error", "User not authenticated"));
        }

        try {
            UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
            Long customerId = userPrincipal.getId();
            messageService.deleteMessage(message_id, customerId);
            
            return ResponseEntity.ok().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getReason()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("error", e.getMessage()));
        }
    }
}
