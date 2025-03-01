package com.caronte.caronte.configuration.jwt;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.caronte.caronte.configuration.services.UserDetailsImpl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${caronte.app.jwt.secret}")
	private String jwtSecret;

	@Value("${caronte.app.jwt.expiration}")
	private long jwtExpirationMs;

	public String generateJwtToken(Authentication authentication) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		Map<String, Object> claims = new HashMap<>();
		claims.put("authorities",
				userPrincipal.getAuthorities().stream().map(auth -> auth.getAuthority()).collect(Collectors.toList()));
		claims.put("id", userPrincipal.getId());
		byte[] keyString = Decoders.BASE64.decode(jwtSecret);
		Key key = Keys.hmacShaKeyFor(keyString);
		return Jwts.builder()
					.claims()
					.add(claims)
					.subject(userPrincipal.getUsername())
					.issuedAt(new Date())
					.expiration(new Date((new Date()).getTime() + jwtExpirationMs))
					.and()
					.signWith(key)
					.compact();
					
	}

	public String getUserNameFromJwtToken(String token) {
		SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));  // Convertir jwtSecret en una Key válida
		return Jwts.parser()
				.verifyWith(key)  			// Usar parserBuilder() en lugar de parser()
				.build()  					// Construir el parser
				.parseSignedClaims(token)  	// Parsear el JWT
				.getPayload()  				// Obtener el cuerpo de los claims
				.getSubject();  			// Extraer el "subject" (en este caso, el nombre de usuario)
	}

	public boolean validateJwtToken(String authToken) {
		try {
			SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));  
			Jwts.parser().verifyWith(key).build().parseSignedClaims(authToken);
			return true;
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}


	public static PrivateKey getPrivateKeyFromString(String privateKeyBase64) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(privateKeyBase64);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }


	public static PublicKey getPublicKeyFromString(String publicKeyBase64) throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(publicKeyBase64);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
}
