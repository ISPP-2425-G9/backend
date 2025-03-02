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
import com.caronte.caronte.company.CompanyRepository;
import com.caronte.caronte.configuration.jwt.JwtUtils;
import com.caronte.caronte.configuration.services.UserDetailsImpl;
import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.plan.Plan;
import com.caronte.caronte.plan.PlanType;
import com.caronte.caronte.user.UserRepository;
import com.caronte.caronte.user.User;
import java.util.Objects;
import jakarta.validation.Valid;
import com.caronte.caronte.util.ErrorHandler;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
	private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

	public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserRepository userRepository, PasswordEncoder passwordEncoder,
            CustomerRepository customerRepository, CompanyRepository companyRepository) {
		this.authenticationManager = authenticationManager;
		this.jwtUtils = jwtUtils;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
        this.customerRepository = customerRepository;
        this.companyRepository = companyRepository;
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
			return ResponseEntity.badRequest().body("Credenciales incorrectas");
		}
	}

	@PostMapping("/customers/signup")
    public ResponseEntity<?> registerCustomer(
            @Valid @RequestBody RegisterRequestCustomer registerRequest,
            BindingResult bindingResult) {
        ErrorHandler errors = ErrorHandler.catchError(bindingResult);

        if (Objects.nonNull(registerRequest.getPassword1()) && !Objects.equals(registerRequest.getPassword1(), registerRequest.getPassword2()))
            errors.addError("password", "Las contraseñas no coinciden");
    
        if (userRepository.findByEmail(registerRequest.getId()).isPresent()) 
            errors.addError("email", "El email ya está en uso");
        
        if (customerRepository.findByDni(registerRequest.getDni()).isPresent())
            errors.addError("dni", "El dni ya está en uso");

        if(errors.hasErrors())
            return ResponseEntity.badRequest().body(errors);
        
    
        Customer customer = registerRequest.parse(passwordEncoder);
    
        try {
            userRepository.save(customer);
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
        
        if (Objects.nonNull(registerRequest.getPassword1()) && !Objects.equals(registerRequest.getPassword1(), registerRequest.getPassword2()))
            errors.addError("password", "Las contraseñas no coinciden");
    
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent())
            errors.addError("email", "El email ya está en uso");

        if (companyRepository.findByNif(registerRequest.getNif()).isPresent())
            errors.addError("nif", "El NIF ya está en uso");
        
        if(errors.hasErrors())
            return ResponseEntity.badRequest().body(errors);
    
        Company company = registerRequest.parse(passwordEncoder);
    
        try {
            userRepository.save(company);
        } catch (DataIntegrityViolationException ex) {
            errors.addError("*", ex.getMostSpecificCause().getMessage());
            return ResponseEntity.badRequest().body(errors);
        }
        
        return ResponseEntity.status(HttpStatus.CREATED).body("¡Compañía registrada correctamente!");
    }
}