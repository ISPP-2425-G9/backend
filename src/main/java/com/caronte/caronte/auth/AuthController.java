package com.caronte.caronte.auth;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.caronte.caronte.auth.payload.response.CompanyUpdateRequest;
import com.caronte.caronte.auth.payload.response.CustomerUpdateRequest;
import com.caronte.caronte.auth.payload.response.JwtResponse;
import com.caronte.caronte.auth.payload.response.LoginRequest;
import com.caronte.caronte.company.Company;
import com.caronte.caronte.company.CompanyService;
import com.caronte.caronte.configuration.jwt.JwtUtils;
import com.caronte.caronte.configuration.services.UserDetailsImpl;
import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtUtils jwtUtils;
	private CustomerService customerService;
	private CompanyService companyService;

	public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils,
			CustomerService customerService, CompanyService companyService) {
		this.authenticationManager = authenticationManager;
		this.jwtUtils = jwtUtils;
		this.customerService = customerService;
		this.companyService = companyService;
	}

	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getId(), loginRequest.getPassword()));

			SecurityContextHolder.getContext().setAuthentication(authentication);
			String jwt = jwtUtils.generateJwtToken(authentication);

			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
			List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
					.collect(Collectors.toList());
			JwtResponse jwtResponse = new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), roles);
			return ResponseEntity.ok().body(jwtResponse);
		} catch (BadCredentialsException exception) {
			return ResponseEntity.badRequest().body("Bad Credentials!");
		}
	}

	@GetMapping("/customers/{customerId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> getCustomer(@PathVariable Long customerId) {
		try {
			Customer customer = customerService.findById(customerId);
			return ResponseEntity.ok().body(customer);
		} catch (IllegalArgumentException exception) {
			return ResponseEntity.badRequest().body("Customer not found");
		}
	}

	@PutMapping("/customers/{customerId}")
	@ResponseStatus(HttpStatus.OK)
	public Customer updateCustomer(
			@PathVariable Long customerId,
			@RequestBody @Valid CustomerUpdateRequest request) {

		return customerService.update(customerId, request);
	}

	@GetMapping("/companies/{companyId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> getCompany(@PathVariable Long companyId) {
		try {
			Company company = companyService.findById(companyId);
			return ResponseEntity.ok().body(company);
		} catch (IllegalArgumentException exception) {
			return ResponseEntity.badRequest().body("Company not found");
		}
	}

	@PutMapping("/companies/{companyId}")
	@ResponseStatus(HttpStatus.OK)
	public Company updateCompany(
			@PathVariable Long companyId,
			@RequestBody @Valid CompanyUpdateRequest request) {

		return companyService.update(companyId, request);
	}
}
