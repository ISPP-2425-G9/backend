package com.caronte.caronte.message;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.caronte.caronte.message.DTOs.MessageRequestDto;
import com.caronte.caronte.user.UserService;
import com.caronte.caronte.util.exceptions.ResponseThrow;

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

    @GetMapping("/{messageId}")
    public ResponseEntity<?> getMessageById(@PathVariable Long messageId) {
        Long customerId = userService.findCurrentUserId();
        MessageRequestDto messageDto = messageService.getMessageRequestDtoByMessageId(customerId,messageId);
        return ResponseEntity.ok(messageDto);
    }

    @GetMapping("/my-messages")
    public ResponseEntity<?> getMessagesByCustomerId() {
        Long customerId = userService.findCurrentUserId();
        List<MessageRequestDto> messages = messageService.getMessagesRequestDtoByCustomerId(customerId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping
    public ResponseEntity<Map<String, ?>> createMessage(@Valid @RequestBody MessageRequestDto request) {
        Long customerId = userService.findCurrentUserId();
        Message createdMessage = messageService.createMessage(request, customerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("messageId", createdMessage.getId()));
    }

    @PutMapping("/{messageId}")
    public ResponseEntity<?> updateMessage(@PathVariable Long messageId, @Valid @RequestBody MessageRequestDto request) {
        Long customerId = userService.findCurrentUserId();
        Message updatedMessage = messageService.updateMessage(messageId, request, customerId);
        return ResponseEntity.ok(updatedMessage);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long messageId) {
        Long customerId = userService.findCurrentUserId();
        messageService.deleteMessage(messageId, customerId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{messageId}/is-owner")
    public ResponseEntity<Map<String, ?>> isMessageOwner(@PathVariable Long messageId) {
        Long customerId = userService.findCurrentUserId();
        boolean isOwner = messageService.isOwner(messageId, customerId);
        return ResponseEntity.ok(Map.of("isOwner", isOwner));
    }

    @GetMapping("/{messageId}/validate-code/{code}")
    public ResponseEntity<?> validateMessageCode(@PathVariable Long messageId, @PathVariable String code) {
        boolean isValid = messageService.validateMessageCode(messageId, code);
        ResponseThrow.checkOrBadRequest(isValid, code);
        MessageRequestDto messageDto = messageService.getMessageRequestDtoByMessageId(messageId);
        return ResponseEntity.ok(messageDto);
    }
}
