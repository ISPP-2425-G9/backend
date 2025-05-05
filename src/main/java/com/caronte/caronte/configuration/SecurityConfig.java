package com.caronte.caronte.configuration;

import java.util.Arrays;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.caronte.caronte.configuration.authorization.Authorization;
import com.caronte.caronte.configuration.jwt.AuthEntryPointJwt;
import com.caronte.caronte.configuration.jwt.AuthTokenFilter;
import com.caronte.caronte.configuration.services.UserDetailsServiceImpl;

@Configuration
public class SecurityConfig {

	public final UserDetailsServiceImpl userDetailsService;
	public final AuthEntryPointJwt unauthorizedHandler;
	public final DataSource dataSource;

	private static final String ADMIN = Authorization.ADMIN.name(); 
	private static final String CUSTOMER = Authorization.CUSTOMER.name();
	private static final String CUSTOMER_FREE = Authorization.CUSTOMER_FREE.name(); 
	private static final String CUSTOMER_PREMIUM = Authorization.CUSTOMER_PREMIUM.name(); 
	private static final String COMPANY = Authorization.COMPANY.name();
	private static final String COMPANY_FREE = Authorization.COMPANY_FREE.name(); 
	private static final String COMPANY_PREMIUM = Authorization.COMPANY_PREMIUM.name(); 

	public SecurityConfig(UserDetailsServiceImpl userDetailsService, AuthEntryPointJwt unauthorizedHandler, DataSource dataSource){
		this.userDetailsService = userDetailsService;
		this.unauthorizedHandler = unauthorizedHandler;
		this.dataSource = dataSource;
	}

    @Bean
	SecurityFilterChain configure(HttpSecurity http) throws Exception {
		http
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.csrf(AbstractHttpConfigurer::disable)		
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))			
			.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
			.exceptionHandling(exepciontHandling -> exepciontHandling.authenticationEntryPoint(unauthorizedHandler))
			.authorizeHttpRequests(authorizeRequests -> authorizeRequests
				// Swagger
				.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger-resources/**", "/webjars/**").permitAll()				
				// Admin
				.requestMatchers("/api/admin/**").hasAuthority(ADMIN)
				// Auth
				.requestMatchers(HttpMethod.DELETE, "/api/auth/*").authenticated()
				.requestMatchers("/api/auth/admin/**").hasAuthority(ADMIN)
				.requestMatchers("/api/auth/companies/signup").anonymous()
				.requestMatchers("/api/auth/customers/signup").anonymous()
				.requestMatchers("/api/auth/customers/**").hasAnyAuthority(CUSTOMER)
				.requestMatchers("/api/auth/companies/**").hasAnyAuthority(COMPANY)
				.requestMatchers("/api/auth/login").anonymous()
				.requestMatchers("/api/auth/password/remember", "/api/auth/password/remember/verify").anonymous()
				.requestMatchers(HttpMethod.PUT, "/api/auth/password/*").authenticated()
				// Companies
				.requestMatchers("/api/companies/companiesTypes").hasAnyAuthority(COMPANY, CUSTOMER)
				.requestMatchers("/api/companies/premium").authenticated()
				// Contacts
				.requestMatchers("/api/contacts/**").hasAuthority(CUSTOMER_PREMIUM)
				// Death Certificate		
				.requestMatchers("/api/deathCertificate/all").hasAuthority(ADMIN)	
				.requestMatchers("/api/deathCertificate/obituary/*").hasAuthority(CUSTOMER)		
				.requestMatchers("/api/deathCertificate/upload").permitAll()
				.requestMatchers("/api/deathCertificate/upload/loggedInUser").permitAll()
				// Messages				
				.requestMatchers("/api/messages/*/validate-code/*").permitAll()
				.requestMatchers("/api/messages", "/api/messages/*").hasAuthority(CUSTOMER_PREMIUM)

				// Obituary				
				.requestMatchers("/api/obituary/**").hasAuthority(CUSTOMER)
				// Plans				
				.requestMatchers("/api/plans/*").authenticated()
				// Receivers				
				.requestMatchers("/api/receiver/getReceivers/obituary/*").hasAuthority(CUSTOMER)
				// Status				
				.requestMatchers("/api/status").permitAll()
				// Templates				
				.requestMatchers("/api/templates/urls").permitAll()
				.anyRequest().permitAll()
			)
			.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}	

    @Bean
	AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}

	@Bean
    PasswordEncoder passwordEncoder() {
    	return new BCryptPasswordEncoder();
    }

    // ✅ Fuente de configuración de CORS para HttpSecurity
    @Bean
    UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("https://wpl.caronte.site", "http://wpl.caronte.site",
				"https://www.wpl.caronte.site", "http://www.wpl.caronte.site")); // ✅ Permitir solo el frontend
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type")); // ✅ Importante para el JWT
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}