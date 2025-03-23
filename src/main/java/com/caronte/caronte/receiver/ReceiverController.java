package com.caronte.caronte.receiver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.caronte.caronte.configuration.services.UserDetailsImpl;
import com.caronte.caronte.obituary.Obituary;
import com.caronte.caronte.obituary.ObituaryService;

@RestController
@RequestMapping("api/receiver")
public class ReceiverController {


    private final ReceiverService receiverService;
    private final ObituaryService obituaryService;

    public ReceiverController(ReceiverService receiverService, ObituaryService obituaryService) {
        this.receiverService = receiverService;
        this.obituaryService = obituaryService;
    }

    @GetMapping("/getReceivers/obituary/{obituaryId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getReceiversByObituaryId(@PathVariable Long obituaryId, Authentication authentication) {
        try {
            UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
            Long customerId = userPrincipal.getId();
            Obituary obituary = obituaryService.getObituaryById(obituaryId);
            if (!obituary.getCustomer().getId().equals(customerId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't access this data");
            }
            List<ReceiverResponseDTO> receivers = receiverService.getReceiversByObituaryId(obituary);
            return ResponseEntity.ok().body(receivers);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @ExceptionHandler({ MethodArgumentNotValidException.class, IllegalArgumentException.class })
    public ResponseEntity<Map<String, String>> handleValidationExceptions(Exception ex) {
        Map<String, String> errors = new HashMap<>();

        if (ex instanceof MethodArgumentNotValidException) {
            ((MethodArgumentNotValidException) ex).getBindingResult().getFieldErrors()
                    .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        } else {
            errors.put("error", ex.getMessage());
        }

        return ResponseEntity.badRequest().body(errors);
    }






}
