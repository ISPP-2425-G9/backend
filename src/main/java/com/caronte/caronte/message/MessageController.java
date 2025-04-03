package com.caronte.caronte.message;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
import com.caronte.caronte.message.DTOs.MessageRequestDto;
import com.caronte.caronte.user.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/messages")
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;

    public MessageController(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Map<String, ?>> createMessage(
            @Valid @RequestBody MessageRequestDto request, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of("error", "User not authenticated"));
        }
        
        Long customerId = userService.findCurrentUserId();
        Message createdMessage = messageService.createMessage(request, customerId);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("messageId", createdMessage.getId()));
    }

    @GetMapping("/my-messages")
    public ResponseEntity<?> getMessagesByCustomerId(
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of("error", "User not authenticated"));
        }

        try {
            UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
            Long customerId = userPrincipal.getId();
            List<MessageRequestDto> messages = messageService.getMessagesRequestDtoByCustomerId(customerId);
            
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("error", false));
        }
    }

    @GetMapping("/{messageId}")
    public ResponseEntity<?> getMessageById(@PathVariable Long messageId,
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of("error", "User not authenticated"));
        }

        try {
            UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
            Long customerId = userPrincipal.getId();
            Message message = messageService.getMessageById(messageId);
            MessageRequestDto messageDto = messageService.getMessageRequestDtoByMessageId(messageId, customerId);
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

    @GetMapping("/{messageId}/is-owner")
    public ResponseEntity<Map<String, ?>> isMessageOwner(@PathVariable Long messageId, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of("error", false));
        }

        try {
            UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
            Long customerId = userPrincipal.getId();
            boolean isOwner = messageService.isOwner(messageId, customerId);

            return ResponseEntity.ok(Map.of("isOwner", isOwner));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{messageId}/validate-code/{code}")
    public ResponseEntity<Map<String, ?>> validateMessageCode(
            @PathVariable Long messageId, 
            @PathVariable String code) {
        try {
            boolean isValid = messageService.validateMessageCode(messageId, code);

            return ResponseEntity.ok(Map.of("isValid", isValid));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{messageId}")
    public ResponseEntity<?> updateMessage(
            @PathVariable Long messageId,
            @Valid @RequestBody MessageRequestDto request,
            Authentication authentication) {
        
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of("error", "User not authenticated"));
        }

        Long customerId = userService.findCurrentUserId();
        Message updatedMessage = messageService.updateMessage(messageId, request, customerId);
        return ResponseEntity.ok(updatedMessage);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long messageId, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of("error", "User not authenticated"));
        }

        Long customerId = userService.findCurrentUserId();
        messageService.deleteMessage(messageId, customerId);
        return ResponseEntity.ok().build();
    }
}
