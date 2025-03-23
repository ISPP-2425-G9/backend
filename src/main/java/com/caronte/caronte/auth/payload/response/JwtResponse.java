package com.caronte.caronte.auth.payload.response;

import java.util.List;

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

	@Override
	public String toString() {
		return "JwtResponse [token=" + token + ", type=" + type + ", id=" + id + ", username=" + username
				+ ", roles=" + roles + ", name=" + name + "]";
	}

}

