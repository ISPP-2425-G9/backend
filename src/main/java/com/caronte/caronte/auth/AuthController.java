package com.caronte.caronte.auth;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.caronte.caronte.auth.payload.response.CompanyUpdateRequest;
import com.caronte.caronte.auth.payload.response.CustomerUpdateRequest;
import com.caronte.caronte.auth.payload.response.JwtResponse;
import com.caronte.caronte.auth.payload.response.LoginRequest;
import com.caronte.caronte.auth.payload.response.RegisterRequestCompany;
import com.caronte.caronte.auth.payload.response.RegisterRequestCustomer;
import com.caronte.caronte.auth.payload.response.UserChangePasswordRequest;
import com.caronte.caronte.company.Company;
import com.caronte.caronte.company.CompanyService;
import com.caronte.caronte.configuration.jwt.JwtUtils;
import com.caronte.caronte.configuration.services.UserDetailsImpl;
import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerService;
import com.caronte.caronte.user.User;
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
        errors.throwIfHasErrors();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getId(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());
		String name = authService.getNameById(userDetails.getId());
        JwtResponse jwtResponse = new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), roles, name);
        return ResponseEntity.ok().body(jwtResponse);
	}

	@PostMapping("/customers/signup")
	public ResponseEntity<?> registerCustomer(
			@Valid @RequestBody RegisterRequestCustomer registerRequest,
			BindingResult bindingResult) {
		ErrorHandler errors = ErrorHandler.catchError(bindingResult);
		Customer customer = authService.validateAndBuildCustomer(registerRequest, errors);
        errors.throwIfHasErrors();

        authService.save(customer);
        LoginRequest loginRequest = LoginRequest.of(registerRequest.getEmail(), registerRequest.getPassword1());
        return this.authenticateUser(loginRequest, bindingResult);
	}

	@PostMapping("/companies/signup")
	public ResponseEntity<?> registerCompany(
			@Valid @RequestBody RegisterRequestCompany registerRequest,
			BindingResult bindingResult) {
		ErrorHandler errors = ErrorHandler.catchError(bindingResult);
		Company company = authService.validateAndBuildCompany(registerRequest, errors);
        errors.throwIfHasErrors();

        authService.save(company);
        LoginRequest loginRequest = LoginRequest.of(registerRequest.getEmail(), registerRequest.getPassword1());
        return this.authenticateUser(loginRequest, bindingResult);
	}

	@GetMapping("/customers/{customerId}")
	public ResponseEntity<?> getCustomer(@PathVariable Long customerId) {
        userService.authorizeUserOrAdmin(customerId, "You can't access this data");

		try {
			Customer customer = customerService.findById(customerId);
			return ResponseEntity.ok().body(customer);
		} catch (IllegalArgumentException exception) {
			return ResponseEntity.badRequest().body("Customer not found");
		}
	}

	@PutMapping("/customers/{customerId}")
	public ResponseEntity<?> updateCustomer(
			@PathVariable Long customerId,
			@RequestBody @Valid CustomerUpdateRequest request) {
		try {
            userService.authorizeUserOrAdmin(customerId);
			Customer customer = customerService.update(customerId, request);
			UserDetailsImpl userDetails = UserDetailsImpl.build(customer);
			String jwt = jwtUtils.generateJwtToken(userDetails);
			List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
					.collect(Collectors.toList());
			String name = customer.getName();
			JwtResponse jwtResponse = new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), roles, name);
			return ResponseEntity.ok().body(jwtResponse);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

	}

	@PutMapping("/password/{userId}")
	public ResponseEntity<?> updateCustomer(
			@PathVariable Long userId,
			@RequestBody @Valid UserChangePasswordRequest request) {
        userService.authorizeUserOrAdmin(userId);
        User user = userService.changePassword(userId, request);
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        String jwt = jwtUtils.generateJwtToken(userDetails);
        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());
		String name = user.getName();
		JwtResponse jwtResponse = new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), roles, name);
        return ResponseEntity.ok().body(jwtResponse);
	}

	@GetMapping("/companies/{companyId}")

	public ResponseEntity<?> getCompany(@PathVariable Long companyId) {
		if (userService.findCurrentUser().getId() != companyId) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't access this data");
		}
		try {
			Company company = companyService.findById(companyId);
			return ResponseEntity.ok().body(company);
		} catch (IllegalArgumentException exception) {
			return ResponseEntity.badRequest().body(exception.getMessage());
		}
	}

	@PutMapping("/companies/{companyId}")

	public ResponseEntity<?> updateCompany(
			@PathVariable Long companyId,
			@RequestBody @Valid CompanyUpdateRequest request) {
        userService.authorizeUser(companyId);
        Company company = companyService.update(companyId, request);
        UserDetailsImpl userDetails = UserDetailsImpl.build(company);
        String jwt = jwtUtils.generateJwtToken(userDetails);
        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());
		String name = company.getName();
		JwtResponse jwtResponse = new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), roles, name);
        return ResponseEntity.ok().body(jwtResponse);
	}

	@DeleteMapping("/{userId}")
	public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
		if (userService.findCurrentUser().getId() != userId) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own account");
		}
		userService.delete(userId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/admin/customers")
	public ResponseEntity<?> getCustomers() {
		return ResponseEntity.ok().body(customerService.findAll());
	}

	@GetMapping("/admin/customers/{customerId}")

	public ResponseEntity<?> getCustomerByAdmin(@PathVariable Long customerId) {
		try {
			Customer customer = customerService.findById(customerId);
			return ResponseEntity.ok().body(customer);
		} catch (IllegalArgumentException exception) {
			return ResponseEntity.badRequest().body("Customer not found");
		}
	}

	@PutMapping("/admin/customers/{customerId}")
	public ResponseEntity<?> updateCustomerByAdmin(@PathVariable Long customerId,
			@RequestBody @Valid CustomerUpdateRequest request) {
		Customer customer = customerService.update(customerId, request);
		return ResponseEntity.ok(customer);
	}

	@GetMapping("/admin/companies")
	public ResponseEntity<?> getCompanies() {
		return ResponseEntity.ok().body(companyService.findAll());
	}

	@GetMapping("/admin/companies/{companyId}")
	public ResponseEntity<?> getCompanyByAdmin(@PathVariable Long companyId) {
		try {
			Company company = companyService.findById(companyId);
			return ResponseEntity.ok().body(company);
		} catch (IllegalArgumentException exception) {
			return ResponseEntity.badRequest().body("Company not found");
		}
	}

	@PutMapping("/admin/companies/{companyId}")
	public ResponseEntity<Company> updateCompanyByAdmin(
			@PathVariable Long companyId,
			@RequestBody @Valid CompanyUpdateRequest request) {
		Company company = companyService.update(companyId, request);
        return ResponseEntity.ok(company);
	}

	@DeleteMapping("/admin/users/{userId}")
 	public ResponseEntity<?> deleteAdmin(@PathVariable Long userId) {
 		userService.delete(userId);
        return ResponseEntity.noContent().build();
 	}

}
