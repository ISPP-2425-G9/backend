package com.caronte.caronte.message;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.caronte.caronte.user.UserService;
import com.caronte.caronte.util.ErrorHandler;

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
    public ResponseEntity<?> createMessage(
            @Valid @RequestBody CreateMessageRequestDto request,
            BindingResult bindingResult) {

        ErrorHandler errorHandler = ErrorHandler.catchError(bindingResult);
        errorHandler.throwIfHasErrors();
        
        Long customerId = userService.findCurrentUserId();
        Message createdMessage = messageService.createMessage(request, customerId);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                            .body(Map.of("messageId", createdMessage.getId()));
    }

}
