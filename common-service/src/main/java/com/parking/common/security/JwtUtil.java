package com.parking.common.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	@Value("${JwtUtil.SECRET_KEY:parkingslotsecretkeyparkingslotsecretkey123}")
	private String secretKey;

	private Key getSigningKey() {
		byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
		if (keyBytes.length < 32) {
			try {
				MessageDigest md = MessageDigest.getInstance("SHA-256");
				keyBytes = md.digest(keyBytes);
			} catch (Exception e) {
				byte[] padded = new byte[32];
				System.arraycopy(keyBytes, 0, padded, 0, keyBytes.length);
				keyBytes = padded;
			}
		}
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateToken(Integer id, String role) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("id", id);
		claims.put("role", role);
		return createToken(claims);
	}

	private String createToken(Map<String, Object> claims) {
		return Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	public Claims getAllClaims(String token) {
		try {
			return Jwts.parserBuilder()
					.setSigningKey(getSigningKey())
					.build()
					.parseClaimsJws(token)
					.getBody();
		} catch (Exception e) {
			try {
				Key fallbackKey = new SecretKeySpec("parkingslot".getBytes(StandardCharsets.UTF_8), "HmacSHA256");
				return Jwts.parserBuilder()
						.setSigningKey(fallbackKey)
						.build()
						.parseClaimsJws(token)
						.getBody();
			} catch (Exception ignored) {
			}
			throw e;
		}
	}
}
