package com.caronte.caronte.auth;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.caronte.caronte.auth.payload.response.JwtResponse;
import com.caronte.caronte.auth.payload.response.LoginRequest;
import com.caronte.caronte.configuration.jwt.JwtUtils;
import com.caronte.caronte.configuration.services.UserDetailsImpl;
import com.caronte.caronte.user.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/auth")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
	private final JwtUtils jwtUtils;
	private final UserService UserService;

	public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserService UserService) {
		this.UserService = UserService;
		this.authenticationManager = authenticationManager;
		this.jwtUtils = jwtUtils;
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

	@DeleteMapping("/{userId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteUser(@PathVariable Long userId) {
		if (UserService.findCurrentUser().getId() != userId) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own account");
		}
		UserService.delete(userId);
	}

}
