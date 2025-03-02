package com.caronte.caronte.auth;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.caronte.caronte.auth.payload.response.JwtResponse;
import com.caronte.caronte.auth.payload.response.LoginRequest;
import com.caronte.caronte.auth.payload.response.RegisterRequestCompany;
import com.caronte.caronte.auth.payload.response.RegisterRequestCustomer;
import com.caronte.caronte.company.Company;
import com.caronte.caronte.configuration.jwt.JwtUtils;
import com.caronte.caronte.configuration.services.UserDetailsImpl;
import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.util.ErrorHandler;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
	private final JwtUtils jwtUtils;
    private final AuthService authService;

	public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, AuthService authService) {
		this.authenticationManager = authenticationManager;
		this.jwtUtils = jwtUtils;
		this.authService = authService;
	}

    @PostMapping("/login")
	public ResponseEntity<?> authenticateUser(
            @Valid @RequestBody LoginRequest loginRequest,
            BindingResult bindingResult) {
        ErrorHandler errors = ErrorHandler.catchError(bindingResult);
        if(errors.hasErrors())
            return ResponseEntity.badRequest().body(errors);
        
		try{
			Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getId(), loginRequest.getPassword()));

			SecurityContextHolder.getContext().setAuthentication(authentication);
			String jwt = jwtUtils.generateJwtToken(authentication);

			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
			List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());
			JwtResponse jwtResponse = new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), roles);
			return ResponseEntity.ok().body(jwtResponse);
		}catch(BadCredentialsException exception){
            errors.addError("*","Credenciales incorrectas");
			return ResponseEntity.badRequest().body(errors);
		}
	}

	@PostMapping("/customers/signup")
    public ResponseEntity<?> registerCustomer(
            @Valid @RequestBody RegisterRequestCustomer registerRequest,
            BindingResult bindingResult) {
        ErrorHandler errors = ErrorHandler.catchError(bindingResult);
        Customer customer = authService.validateAndBuildCustomer(registerRequest, errors);
        
        if(errors.hasErrors())
            return ResponseEntity.badRequest().body(errors);
        
        try {
            authService.save(customer);
        } catch (DataIntegrityViolationException ex) {
            errors.addError("*", ex.getMostSpecificCause().getMessage());
            return ResponseEntity.badRequest().body(errors);
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body("¡Cliente registrado correctamente!");
    }

    @PostMapping("/companies/signup")
    public ResponseEntity<?> registerCompany(
            @Valid @RequestBody RegisterRequestCompany registerRequest,
            BindingResult bindingResult) {
        ErrorHandler errors = ErrorHandler.catchError(bindingResult);
        Company company = authService.validateAndBuildCompany(registerRequest, errors);
        
        if(errors.hasErrors())
            return ResponseEntity.badRequest().body(errors);
    
        try {
            authService.save(company);
        } catch (DataIntegrityViolationException ex) {
            errors.addError("*", ex.getMostSpecificCause().getMessage());
            return ResponseEntity.badRequest().body(errors);
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body("¡Compañía registrada correctamente!");
    }
}