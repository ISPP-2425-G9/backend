package com.caronte.caronte.auth;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import com.caronte.caronte.plan.Plan;
import com.caronte.caronte.plan.PlanType;
import com.caronte.caronte.user.UserRepository;
import com.caronte.caronte.user.User;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
	private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

	public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.authenticationManager = authenticationManager;
		this.jwtUtils = jwtUtils;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

    @PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
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
			return ResponseEntity.badRequest().body("Bad Credentials!");
		}
	}

	 @PostMapping("/customers/signup")
    public ResponseEntity<?> registerCustomer(
            @Valid @RequestBody RegisterRequestCustomer registerRequest,
            BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors);
        }
    
        if (userRepository.findByEmail(registerRequest.getId()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }
    
        Customer customer = new Customer();
        customer.setDni(registerRequest.getDni());
        customer.setIsActive(true);
        Plan plan = new Plan();
        plan.setPlanType(PlanType.FREE);
        plan.setExpireDate(LocalDate.now().plusYears(100));
        plan.setBillingAddress("N/A");
        customer.setPlan(plan);
    
        customer.setName(registerRequest.getName());
        customer.setEmail(registerRequest.getId());
        customer.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        customer.setTelephone(registerRequest.getTelephone());
    
        try {
            userRepository.save(customer);
        } catch (DataIntegrityViolationException ex) {
            String errorMessage = ex.getMostSpecificCause().getMessage();
            return ResponseEntity.badRequest().body("Data integrity error: " + errorMessage);
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body("Customer registered successfully!");
    }

    @PostMapping("/companies/signup")
    public ResponseEntity<?> registerCompany(
            @Valid @RequestBody RegisterRequestCompany registerRequest,
            BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors);
        }
    
        if (userRepository.findByEmail(registerRequest.getId()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }
    
        Company company = new Company();
        company.setAddress(registerRequest.getAddress());
        company.setCity(registerRequest.getCity());
        company.setZipCode(registerRequest.getZipCode());
        company.setNif(registerRequest.getNif());
        company.setDescription(registerRequest.getDescription());
        company.setImageUrl(registerRequest.getImageUrl());
    
        company.setName(registerRequest.getName());
        company.setEmail(registerRequest.getId());
        company.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        company.setTelephone(registerRequest.getTelephone());
    
        try {
            userRepository.save(company);
        } catch (DataIntegrityViolationException ex) {
            String errorMessage = ex.getMostSpecificCause().getMessage();
            return ResponseEntity.badRequest().body("Data integrity error: " + errorMessage);
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body("Company registered successfully!");
    }
}