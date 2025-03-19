package com.caronte.caronte.obituary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.caronte.caronte.configuration.services.UserDetailsImpl;
import com.caronte.caronte.user.UserService;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Price;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerListParams;
import com.stripe.param.PaymentIntentCreateParams;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/obituary")
public class ObituaryController {

    @Value("${stripe.obituary.price.id}")
    private String obituaryPriceId;

    private final ObituaryService obituaryService;
    private final UserService userService;

    public ObituaryController(ObituaryService obituaryService, UserService userService) {
        this.obituaryService = obituaryService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createObituary(@RequestBody @Valid ObituraryRequestDto request,
            Authentication authentication) {
        try {
            UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
            Long customerId = userPrincipal.getId();

            Obituary obituary = obituaryService.createObituaryWithReceivers(request, customerId);

            System.out.println(obituary);

            return ResponseEntity.ok("Obituary created successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/update/{obituary_id}")
    public ResponseEntity<?> updateObituary(@RequestBody @Valid ObituraryRequestDto request,
            @PathVariable Long obituary_id,
            Authentication authentication) {
        try {
            UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
            Long customerId = userPrincipal.getId();

            obituaryService.updateObituaryWithReceivers(customerId, obituary_id, request);

            return ResponseEntity.ok("Obituary updated successfully");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{obituary_id}")
    public ResponseEntity<String> deleteObituary(@PathVariable Long obituary_id, Authentication authentication) {
        try {
            UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
            Long customerId = userPrincipal.getId();

            obituaryService.deleteObituaryByCustomer(customerId, obituary_id);

            return ResponseEntity.ok("Obituary deleted successfully");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred");
        }
    }

    @GetMapping("/myObituaries")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getAllObituariesByCustomer(Authentication authentication) {
        try {
            UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
            Long customerId = userPrincipal.getId();
            Iterable<Obituary> obituaries = obituaryService.getAllObituariesByCustomer(customerId);
            return ResponseEntity.ok().body(obituaries);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @GetMapping("/myObituaries/{obituaryId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getObituaryById(@PathVariable Long obituaryId, Authentication authentication) {
        try {
            UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
            Long customerId = userPrincipal.getId();
            Obituary obituary = obituaryService.getObituaryById(obituaryId);
            if (!obituary.getCustomer().getId().equals(customerId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't access this data");
            }
            obituary.setCustomer(null);
            return ResponseEntity.ok().body(obituary);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    // TODO: IMPORTANTE!!! Actualmente esto es una versión inicial, se debe de cambiar dependiendo de como se realice el pago
    @PostMapping("/pay")
    public ResponseEntity<?> payOblituary(@PathVariable Long obituaryId,
            @RequestBody PayObituaryDTO payObituaryDTO) {
        try {
            String email = userService.findCurrentUser().getEmail();
            String paymentMethodId = payObituaryDTO.getPaymentId();
            List<Customer> customers = Customer.list(CustomerListParams.builder()
                    .setEmail(email)
                    .setLimit(1L)
                    .build()).getData();

            // Se realiza una búsqueda de si existe dicho Customer en Stripe por su email, en caso contrario, se crea
            Customer customer = !customers.isEmpty() ? customers.getFirst()
                    : Customer.create(
                            CustomerCreateParams.builder()
                                    .setEmail(email)
                                    .build());

            Price price = Price.retrieve(obituaryPriceId);

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(price.getUnitAmount())
                    .setCurrency(price.getCurrency())
                    .setCustomer(customer.getId()) // ID del cliente
                    .setPaymentMethod(paymentMethodId) // ID del método de pago (debe venir del frontend o generado antes)
                    .setConfirm(true) // Confirma el pago inmediatamente
                    .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                            .setEnabled(true)
                            .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                            .build())
                    .build();

            PaymentIntent.create(params);

            return ResponseEntity.ok("Compra de esquela exitosa");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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