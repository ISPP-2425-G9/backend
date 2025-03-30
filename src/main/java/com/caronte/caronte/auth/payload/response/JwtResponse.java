package com.caronte.caronte.auth.payload.response;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.caronte.caronte.configuration.services.UserDetailsImpl;
import com.caronte.caronte.user.User;

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

	public JwtResponse(String accessToken, Long id, String username, List<String> roles, String name) {
		this.token = accessToken;
		this.id = id;
		this.username = username;
		this.roles = roles;
		this.name = name;
	}

	public JwtResponse(String accessToken, UserDetailsImpl userDetailsImpl, String name) {
		this.token = accessToken;
		this.id = userDetailsImpl.getId();
		this.username = userDetailsImpl.getUsername();
		this.roles = userDetailsImpl.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
		this.name = name;
	}

	public JwtResponse(String accessToken, User user) {
		this.token = accessToken;
		UserDetailsImpl userDetailsImpl = UserDetailsImpl.build(user);
		this.id = user.getId();
		this.username = userDetailsImpl.getUsername();
		this.roles = userDetailsImpl.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
		this.name = user.getName();
	}


	@Override
	public String toString() {
		return "JwtResponse [token=" + token + ", type=" + type + ", id=" + id + ", username=" + username
				+ ", roles=" + roles + ", name=" + name + "]";
	}

}

