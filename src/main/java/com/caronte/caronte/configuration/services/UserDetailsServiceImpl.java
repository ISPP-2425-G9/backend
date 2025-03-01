package com.caronte.caronte.configuration.services;

import java.util.regex.Pattern;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caronte.caronte.company.Company;
import com.caronte.caronte.company.CompanyRepository;
import com.caronte.caronte.customer.Customer;
import com.caronte.caronte.customer.CustomerRepository;
import com.caronte.caronte.user.User;
import com.caronte.caronte.user.UserRepository;
import com.caronte.caronte.util.RegexContants;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	UserRepository userRepository;
	CustomerRepository customerRepository;
	CompanyRepository companyRepository;

	
	public UserDetailsServiceImpl(UserRepository userRepository, CustomerRepository customerRepository, CompanyRepository companyRepository) {
		this.userRepository = userRepository;
		this.customerRepository = customerRepository;
		this.companyRepository = companyRepository;
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if(Pattern.matches(RegexContants.REGEX_EMAIL, username)){
			User user = userRepository.findByEmail(username)
			.orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + username));
			return UserDetailsImpl.build(user);
		} else if(Pattern.matches(RegexContants.REGEX_DNI, username)) {
			Customer customer = customerRepository.findByDni(username)
			.orElseThrow(() -> new UsernameNotFoundException("User Not Found with DNI: " + username));
			return UserDetailsImpl.build(customer);

		} else if(Pattern.matches(RegexContants.REGEX_NIF, username)) {
			Company company = companyRepository.findByNif(username)
			.orElseThrow(() -> new UsernameNotFoundException("User Not Found with NIF: " + username));
			return UserDetailsImpl.build(company);
		}
		return null;
	}

}
