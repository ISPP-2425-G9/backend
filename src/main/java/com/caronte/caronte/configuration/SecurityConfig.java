package com.caronte.caronte.configuration;

import java.util.Arrays;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.web.filter.CorsFilter;

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
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))  // ✅ CORS habilitado aquí
			.csrf(AbstractHttpConfigurer::disable)		
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))			
			.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
			.exceptionHandling(exepciontHandling -> exepciontHandling.authenticationEntryPoint(unauthorizedHandler))
			.authorizeHttpRequests(authorizeRequests -> authorizeRequests
				.requestMatchers("/api/auth/login").anonymous()
				.requestMatchers("/api/auth/customers/signup", "/api/auth/companies/signup").anonymous()
				.requestMatchers("/api/auth/customers/**").hasAnyAuthority(ADMIN, CUSTOMER, CUSTOMER_FREE, CUSTOMER_PREMIUM) // ✅ Permitir acceso a clientes autenticados
				.requestMatchers("/api/auth/companies/**").hasAnyAuthority(ADMIN, COMPANY, COMPANY_FREE, COMPANY_PREMIUM) // ✅ Permitir acceso a empresas autenticadas
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

    // ✅ Configuración CORS aplicada a todas las rutas protegidas
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("https://sprint1.caronte.site", "http://sprint1.caronte.site", "https://www.sprint1.caronte.site", "http://www.sprint1.caronte.site")); // ✅ Asegurar que el frontend tiene acceso
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);	
    }

    // ✅ Fuente de configuración de CORS para HttpSecurity
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("https://sprint1.caronte.site", "http://sprint1.caronte.site", "https://www.sprint1.caronte.site", "http://www.sprint1.caronte.site"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
