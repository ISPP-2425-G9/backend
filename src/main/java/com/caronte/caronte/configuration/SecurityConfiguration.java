package com.caronte.caronte.configuration;

import static org.springframework.security.config.Customizer.withDefaults;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.caronte.caronte.configuration.jwt.AuthEntryPointJwt;
import com.caronte.caronte.configuration.jwt.AuthTokenFilter;
import com.caronte.caronte.configuration.services.UserDetailsServiceImpl;

@Configuration
// @EnableWebSecurity
public class SecurityConfiguration {
    
	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@Autowired
	AuthEntryPointJwt unauthorizedHandler;

	@Autowired
	DataSource dataSource;

    @Bean
	SecurityFilterChain configure(HttpSecurity http) throws Exception {
		
		http
			.cors(withDefaults())		
			.csrf(AbstractHttpConfigurer::disable)		
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))			
			.headers((headers) -> headers.frameOptions((frameOptions) -> frameOptions.disable()))
			.exceptionHandling((exepciontHandling) -> exepciontHandling.authenticationEntryPoint(unauthorizedHandler))			
			.authorizeHttpRequests(authorizeRequests ->	authorizeRequests
			.anyRequest().permitAll())
			.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);		
		return http.build();
	}

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
		return config.getAuthenticationManager();
	}	

    @Bean
	AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}

    @SuppressWarnings("deprecation")
	@Bean
    PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance(); // No encripta la contraseña

        // return new BCryptPasswordEncoder();
    }

}
