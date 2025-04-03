package com.caronte.caronte.auth.payload.response;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.caronte.caronte.configuration.services.UserDetailsImpl;
import com.caronte.caronte.user.User;
import com.stripe.exception.StripeException;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse {

	private String token;
	private String type = "Bearer";
	private Long id;
	private String username;
	private List<String> roles;
	private String name;
	private LocalDateTime experedPlanDate;

	public JwtResponse(String accessToken, Long id, String username, List<String> roles, String name, LocalDateTime experedPlanDate) {
		this.token = accessToken;
		this.id = id;
		this.username = username;
		this.roles = roles;
		this.name = name;
	}

	public JwtResponse(String accessToken, UserDetailsImpl userDetailsImpl, String name, LocalDateTime experedPlanDate) {
		this.token = accessToken;
		this.id = userDetailsImpl.getId();
		this.username = userDetailsImpl.getUsername();
		this.roles = userDetailsImpl.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
		this.name = name;
	}

	public JwtResponse(String accessToken, User user) throws StripeException {
		this.token = accessToken;
		UserDetailsImpl userDetailsImpl = UserDetailsImpl.build(user);
		this.id = user.getId();
		this.username = userDetailsImpl.getUsername();
		this.roles = userDetailsImpl.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
		this.name = user.getName();
		this.experedPlanDate = user.getExpiringDate();
	}


	@Override
	public String toString() {
		return "JwtResponse [token=" + token + ", type=" + type + ", id=" + id + ", username=" + username
				+ ", roles=" + roles + ", name=" + name + "]";
	}

}

