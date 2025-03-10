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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.caronte.caronte.auth.payload.response.CompanyUpdateRequest;
import com.caronte.caronte.auth.payload.response.CustomerUpdateRequest;
import com.caronte.caronte.auth.payload.response.JwtResponse;
import com.caronte.caronte.auth.payload.response.LoginRequest;
import com.caronte.caronte.auth.payload.response.RegisterRequestCompany;
import com.caronte.caronte.auth.payload.response.RegisterRequestCustomer;
import com.caronte.caronte.company.Company;
import com.caronte.caronte.company.CompanyService;
import com.caronte.caronte.configuration.jwt.JwtUtils;
import com.caronte.caronte.configuration.services.UserDetailsImpl;
import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerService;
import com.caronte.caronte.user.UserService;
import com.caronte.caronte.util.ErrorHandler;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final AuthService authService;
	private final JwtUtils jwtUtils;
	private final UserService userService;
	private final CustomerService customerService;
	private final CompanyService companyService;

	public AuthController(AuthenticationManager authenticationManager, AuthService authService, UserService userService, 
			CustomerService customerService, CompanyService companyService, JwtUtils jwtUtils) {
		this.authenticationManager = authenticationManager;
		this.authService = authService;
		this.userService = userService;
		this.customerService = customerService;
		this.companyService = companyService;
		this.jwtUtils = jwtUtils;
	}

	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(
			@Valid @RequestBody LoginRequest loginRequest,
			BindingResult bindingResult) {
		ErrorHandler errors = ErrorHandler.catchError(bindingResult);
		if (errors.hasErrors())
			return ResponseEntity.badRequest().body(errors);

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
			errors.addError("*", "Credenciales incorrectas");
			return ResponseEntity.badRequest().body(errors);
		}
	}

	@PostMapping("/customers/signup")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> registerCustomer(
			@Valid @RequestBody RegisterRequestCustomer registerRequest,
			BindingResult bindingResult) {
		ErrorHandler errors = ErrorHandler.catchError(bindingResult);
		Customer customer = authService.validateAndBuildCustomer(registerRequest, errors);

		if (errors.hasErrors())
			return ResponseEntity.badRequest().body(errors);

		try {
			authService.save(customer);
			LoginRequest loginRequest = LoginRequest.of(registerRequest.getEmail(), registerRequest.getPassword1());
			return this.authenticateUser(loginRequest, bindingResult);
		} catch (DataIntegrityViolationException ex) {
			errors.addError("*", ex.getMostSpecificCause().getMessage());
			return ResponseEntity.badRequest().body(errors);
		}
	}

	@PostMapping("/companies/signup")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> registerCompany(
			@Valid @RequestBody RegisterRequestCompany registerRequest,
			BindingResult bindingResult) {
		ErrorHandler errors = ErrorHandler.catchError(bindingResult);
		Company company = authService.validateAndBuildCompany(registerRequest, errors);

		if (errors.hasErrors())
			return ResponseEntity.badRequest().body(errors);

		try {
			authService.save(company);
			LoginRequest loginRequest = LoginRequest.of(registerRequest.getEmail(), registerRequest.getPassword1());
			return this.authenticateUser(loginRequest, bindingResult);
		} catch (DataIntegrityViolationException ex) {
			errors.addError("*", ex.getMostSpecificCause().getMessage());
			return ResponseEntity.badRequest().body(errors);
		}

	}


	@GetMapping("/customers/{customerId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> getCustomer(@PathVariable Long customerId) {
		if (userService.findCurrentUser().getId() != customerId) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't access this data");
		}
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

		if (userService.findCurrentUser().getId() != customerId) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't modify this data");
		}

		return customerService.update(customerId, request);
	}

	@GetMapping("/companies/{companyId}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> getCompany(@PathVariable Long companyId) {
		if (userService.findCurrentUser().getId() != companyId) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't access this data");
		}
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

		if (userService.findCurrentUser().getId() != companyId) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't modify this data");
		}
		return companyService.update(companyId, request);
	}

	@DeleteMapping("/{userId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteUser(@PathVariable Long userId) {
		if (userService.findCurrentUser().getId() != userId) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own account");
		}
		userService.delete(userId);
	}

	@GetMapping("/admin/customers")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> getCustomers() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't access this data");
		}
		return ResponseEntity.ok().body(customerService.findAll());
	}

	@GetMapping("/admin/companies")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> getCompanies() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't access this data");
		}
		return ResponseEntity.ok().body(companyService.findAll());
	}

}
