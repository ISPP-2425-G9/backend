package com.caronte.caronte.auth;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
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
import com.caronte.caronte.auth.payload.response.RegisterRequest;
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

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest, BindingResult bindingResult) {
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
		
		User user;
		switch (registerRequest.getUserType().toUpperCase()) {
			case "CUSTOMER":
				if (registerRequest.getDni() == null || registerRequest.getDni().trim().isEmpty()) {
					return ResponseEntity.badRequest().body("Error: DNI is required for CUSTOMER.");
				}
				if (registerRequest.getPlanTypeValue() == null || registerRequest.getPlanTypeValue().trim().isEmpty() ||
					registerRequest.getPlanExpireDate() == null || registerRequest.getPlanExpireDate().trim().isEmpty() ||
					registerRequest.getPlanBillingAddress() == null || registerRequest.getPlanBillingAddress().trim().isEmpty()) {
					return ResponseEntity.badRequest().body("Error: Plan information is required for CUSTOMER.");
				}
				
				Customer customer = new Customer();
				customer.setDni(registerRequest.getDni());
				customer.setIsActive(true);
				
				Plan plan = new Plan();
				try {
					plan.setPlanType(PlanType.valueOf(registerRequest.getPlanTypeValue().toUpperCase()));
				} catch (IllegalArgumentException e) {
					return ResponseEntity.badRequest().body("Error: Invalid plan type for customer.");
				}
				plan.setExpireDate(LocalDate.parse(registerRequest.getPlanExpireDate()));
				plan.setBillingAddress(registerRequest.getPlanBillingAddress());
				
				customer.setPlan(plan);
				user = customer;
				break;
			case "COMPANY":
				if (registerRequest.getAddress() == null || registerRequest.getAddress().trim().isEmpty() ||
					registerRequest.getCity() == null || registerRequest.getCity().trim().isEmpty() ||
					registerRequest.getZipCode() == null || registerRequest.getZipCode().trim().isEmpty() ||
					registerRequest.getNif() == null || registerRequest.getNif().trim().isEmpty()) {
					return ResponseEntity.badRequest().body("Error: Address, city, zipCode and NIF are required for COMPANY.");
				}
				Company company = new Company();
				company.setAddress(registerRequest.getAddress());
				company.setCity(registerRequest.getCity());
				company.setZipCode(registerRequest.getZipCode());
				company.setNif(registerRequest.getNif());
				company.setDescription(registerRequest.getDescription()); // Optional
				company.setImageUrl(registerRequest.getImageUrl()); // Optional
				user = company;
				break;
			default:
				return ResponseEntity.badRequest().body("Error: Invalid user type.");
		}
		
		user.setName(registerRequest.getName());
		user.setEmail(registerRequest.getId());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		System.out.println(passwordEncoder.encode(registerRequest.getPassword()));
		user.setTelephone(registerRequest.getTelephone());
		
		try {
			userRepository.save(user);
		} catch (DataIntegrityViolationException ex) {
			String errorMessage = ex.getMostSpecificCause().getMessage();
			return ResponseEntity.badRequest().body("Data integrity error: " + errorMessage);
		}
		
		return ResponseEntity.ok("User registered successfully!");
	}


}