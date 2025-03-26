package com.caronte.caronte.configuration.services;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.caronte.caronte.admin.Admin;
import com.caronte.caronte.company.Company;
import com.caronte.caronte.configuration.authorization.Authorization;
import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.plan.PlanType;
import com.caronte.caronte.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserDetailsImpl implements UserDetails {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String username;

	@JsonIgnore
	private String password;

	private Collection<? extends GrantedAuthority> authorities;

	public UserDetailsImpl(Long id, String username, String password,
			Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.authorities = authorities;
	}

	public static UserDetailsImpl build(User user) {
		if(user instanceof Admin){
			return build((Admin) user);
		} else if(user instanceof Customer){
			return build((Customer) user);
		} else if(user instanceof Company) {
			return build((Company) user);
		}
		throw new IllegalArgumentException("User isn't instance of Admin, Customer or Company");
	}

	public static UserDetailsImpl build(Admin admin) {
		List<GrantedAuthority> authorities = List.of(Authorization.ADMIN.getAuthority());

		return new UserDetailsImpl(admin.getId(), admin.getEmail(),
				admin.getPassword(),
				authorities);
	}

	public static UserDetailsImpl build(Customer customer) {
		PlanType planType = customer.getPlan().getPlanType();
		SimpleGrantedAuthority customer_authorization = null;

		if(planType == PlanType.FREE) {
			customer_authorization = Authorization.CUSTOMER_FREE.getAuthority();
		} else if(planType == PlanType.PREMIUM) {
			customer_authorization = Authorization.CUSTOMER_PREMIUM.getAuthority();
		} else {
			throw new IllegalArgumentException("Invalid plan type");
		}

		List<GrantedAuthority> authorities = Arrays.asList(Authorization.CUSTOMER.getAuthority(), customer_authorization);
		return new UserDetailsImpl(customer.getId(), customer.getEmail(),
				customer.getPassword(),
				authorities);
	}

	public static UserDetailsImpl build(Company company) {
		PlanType planType = company.getPlan().getPlanType();
		SimpleGrantedAuthority customer_authorization = null;

		if(planType == PlanType.FREE) {
			customer_authorization = Authorization.COMPANY_FREE.getAuthority();
		} else if(planType == PlanType.PREMIUM) {
			customer_authorization = Authorization.COMPANY_PREMIUM.getAuthority();
		} else {
			throw new IllegalArgumentException("Invalid plan type");
		}

		List<GrantedAuthority> authorities = List.of(Authorization.COMPANY.getAuthority(), customer_authorization);

		return new UserDetailsImpl(company.getId(), company.getEmail(),
				company.getPassword(),
				authorities);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}


	public Long getId() {
		return id;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

    public boolean isAdmin() {
        return this.getAuthorities().contains(Authorization.ADMIN.getAuthority());
    }

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserDetailsImpl other = (UserDetailsImpl) obj;
		return Objects.equals(id, other.id);
	}

}
