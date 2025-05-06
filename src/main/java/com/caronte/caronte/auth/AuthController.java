package com.caronte.caronte.auth;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.caronte.caronte.auth.payload.response.CompanyUpdateRequest;
import com.caronte.caronte.auth.payload.response.CustomerUpdateRequest;
import com.caronte.caronte.auth.payload.response.JwtResponse;
import com.caronte.caronte.auth.payload.response.LoginRequest;
import com.caronte.caronte.auth.payload.response.RegisterRequestCompany;
import com.caronte.caronte.auth.payload.response.RegisterRequestCustomer;
import com.caronte.caronte.auth.payload.response.RememberPasswordRequest;
import com.caronte.caronte.auth.payload.response.UserChangePasswordRequest;
import com.caronte.caronte.company.Company;
import com.caronte.caronte.company.CompanyService;
import com.caronte.caronte.configuration.jwt.JwtUtils;
import com.caronte.caronte.configuration.services.UserDetailsImpl;
import com.caronte.caronte.configuration.services.VerificationCodeStore;
import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerService;
import com.caronte.caronte.plan.PlanService;
import com.caronte.caronte.user.User;
import com.caronte.caronte.user.UserService;
import com.caronte.caronte.util.ErrorHandler;
import com.stripe.exception.StripeException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;

@RestController
@RequestMapping("api/auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final AuthService authService;
	private final JwtUtils jwtUtils;
	private final UserService userService;
	private final CustomerService customerService;
	private final CompanyService companyService;
	private final VerificationCodeStore verificationCodeStore;
	private final PlanService planService;

	public AuthController(AuthenticationManager authenticationManager, AuthService authService, UserService userService,
			CustomerService customerService, CompanyService companyService, JwtUtils jwtUtils, VerificationCodeStore verificationCodeStore,
			PlanService planService) {
		this.authenticationManager = authenticationManager;
		this.authService = authService;
		this.userService = userService;
		this.customerService = customerService;
		this.companyService = companyService;
		this.jwtUtils = jwtUtils;
		this.verificationCodeStore = verificationCodeStore;
		this.planService = planService;
	}

	@PostMapping("/login")
	public ResponseEntity<JwtResponse> authenticateUser(
			@Valid @RequestBody LoginRequest loginRequest,
			BindingResult bindingResult) throws StripeException {
		ErrorHandler errors = ErrorHandler.catchError(bindingResult);
		errors.throwIfHasErrors();

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getId(), loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
		String jwt = jwtUtils.generateJwtToken(authentication);
		User user = userService.findById(userDetailsImpl.getId());
		LocalDateTime expiredDate = planService.getExpiringDate(user);
		JwtResponse jwtResponse = new JwtResponse(jwt, user, expiredDate);
		return ResponseEntity.ok().body(jwtResponse);
	}

	@PostMapping("/customers/signup")
	public ResponseEntity<JwtResponse> registerCustomer(
			@Valid @RequestBody RegisterRequestCustomer registerRequest,
			BindingResult bindingResult) throws StripeException {
		ErrorHandler errors = ErrorHandler.catchError(bindingResult);
		Customer customer = authService.validateAndBuildCustomer(registerRequest, errors);
		errors.throwIfHasErrors();

		authService.save(customer);
		LoginRequest loginRequest = LoginRequest.of(registerRequest.getEmail(), registerRequest.getPassword1());
		return this.authenticateUser(loginRequest, bindingResult);
	}

	@PostMapping("/companies/signup")
	public ResponseEntity<JwtResponse> registerCompany(
			@Valid @RequestBody RegisterRequestCompany registerRequest,
			BindingResult bindingResult) throws StripeException {
		ErrorHandler errors = ErrorHandler.catchError(bindingResult);
		Company company = authService.validateAndBuildCompany(registerRequest, errors);
		errors.throwIfHasErrors();

		authService.save(company);
		LoginRequest loginRequest = LoginRequest.of(registerRequest.getEmail(), registerRequest.getPassword1());
		return this.authenticateUser(loginRequest, bindingResult);
	}

	@GetMapping("/customers/{customerId}")
	public ResponseEntity<Customer> getCustomer(@PathVariable Long customerId) {
		userService.authorizeUserOrAdmin(customerId, "You can't access this data");
		Customer customer = customerService.findById(customerId);
		return ResponseEntity.ok(customer);
	}

	@PutMapping("/customers/{customerId}")
	public ResponseEntity<JwtResponse> updateCustomer(
			@PathVariable Long customerId,
			@RequestBody @Valid CustomerUpdateRequest request) throws AccessDeniedException, StripeException {
		userService.authorizeUserOrAdmin(customerId);
		Optional<User> existingUser = userService.findByEmail(request.getEmail());
		if (existingUser.isPresent() && !Objects.equals(existingUser.get().getId(), customerId)) {
			throw new AccessDeniedException("This email is of other user");
		}

		Customer customer = customerService.update(customerId, request);
		UserDetailsImpl userDetails = UserDetailsImpl.build(customer);
		String jwt = jwtUtils.generateJwtToken(userDetails);
		User user = userService.findCurrentUser();
		LocalDateTime expiredDate = planService.getExpiringDate(user);
		JwtResponse jwtResponse = new JwtResponse(jwt, user, expiredDate);
		return ResponseEntity.ok(jwtResponse);
	}

	@PostMapping("/password/remember/verify")
	public ResponseEntity<String> verifyRememberCode(@RequestBody @Valid RememberPasswordRequest rememberPasswordRequest) throws Exception {
		verificationCodeStore.verifyCode(rememberPasswordRequest);
		userService.changePassword(rememberPasswordRequest.email(), rememberPasswordRequest.password());
		verificationCodeStore.removeCode(rememberPasswordRequest.email());
		return ResponseEntity.ok("Contraseña cambiada");
	}

	@PostMapping("/password/remember")
	public ResponseEntity<String> generateRememberCode(@RequestParam @Email String email) throws Exception {
		verificationCodeStore.saveCode(email);
		return ResponseEntity.ok("Mensaje de recuperación de contraseña enviado");
	}
	

	@PutMapping("/password/{userId}")
	public ResponseEntity<JwtResponse> updateCustomer(@PathVariable Long userId,
			@RequestBody @Valid UserChangePasswordRequest request) throws StripeException {
    userService.authorizeUserOrAdmin(userId);
    User user = userService.changePassword(userId, request);
    UserDetailsImpl userDetails = UserDetailsImpl.build(user);
		String jwt = jwtUtils.generateJwtToken(userDetails);
		LocalDateTime expiredDate = planService.getExpiringDate(user);
		JwtResponse jwtResponse = new JwtResponse(jwt, user, expiredDate);
		return ResponseEntity.ok().body(jwtResponse);
	}


	@GetMapping("/companies/{companyId}")
	public ResponseEntity<Company> getCompany(@PathVariable Long companyId) {
		userService.authorizeUser(companyId);
		Company company = companyService.findById(companyId);
		return ResponseEntity.ok().body(company);
	}

	@PutMapping("/companies/{companyId}")
	public ResponseEntity<JwtResponse> updateCompany(
			@PathVariable Long companyId,
    			@RequestBody @Valid CompanyUpdateRequest request) throws AccessDeniedException, StripeException {
    		userService.authorizeUser(companyId);
		userService.findByEmail(request.getEmail())
			.filter(user -> Objects.equals(user.getId(), companyId))
			.orElseThrow(() -> new AccessDeniedException("This email is of other user"));

  		Company company = companyService.update(companyId, request);
		UserDetailsImpl userDetails = UserDetailsImpl.build(company);
		String jwt = jwtUtils.generateJwtToken(userDetails);
		User user = userService.findCurrentUser();
		LocalDateTime expiredDate = planService.getExpiringDate(user);
		JwtResponse jwtResponse = new JwtResponse(jwt, user, expiredDate);
		return ResponseEntity.ok().body(jwtResponse);
	}

	@DeleteMapping("/{userId}")
	public ResponseEntity<?> deleteUser(@PathVariable Long userId) throws StripeException {
		userService.authorizeUser(userId);
		userService.delete(userId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/admin/customers")
	public ResponseEntity<List<Customer>> getCustomers() {
		return ResponseEntity.ok().body(customerService.findAll());
	}

	@GetMapping("/admin/customers/{customerId}")
	public ResponseEntity<Customer> getCustomerByAdmin(@PathVariable Long customerId) {
		Customer customer = customerService.findById(customerId);
		return ResponseEntity.ok().body(customer);
	}

	@PutMapping("/admin/customers/{customerId}")
	public ResponseEntity<Customer> updateCustomerByAdmin(@PathVariable Long customerId,
			@RequestBody @Valid CustomerUpdateRequest request) {
		Customer customer = customerService.update(customerId, request);
		return ResponseEntity.ok(customer);
	}

	@GetMapping("/admin/companies")
	public ResponseEntity<List<Company>> getCompanies() {
		return ResponseEntity.ok().body(companyService.findAll());
	}

	@GetMapping("/admin/companies/{companyId}")
	public ResponseEntity<Company> getCompanyByAdmin(@PathVariable Long companyId) {
		Company company = companyService.findById(companyId);
		return ResponseEntity.ok().body(company);
	}

	@PutMapping("/admin/companies/{companyId}")
	public ResponseEntity<Company> updateCompanyByAdmin(@PathVariable Long companyId,
			@RequestBody @Valid CompanyUpdateRequest request) {
		Company company = companyService.update(companyId, request);
		return ResponseEntity.ok(company);
	}

	@DeleteMapping("/admin/users/{userId}")
 	public ResponseEntity<?> deleteAdmin(@PathVariable Long userId) throws StripeException {
 		userService.delete(userId);
        return ResponseEntity.noContent().build();
 	}

}
