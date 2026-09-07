package com.parking.common.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;

@Component
public class JwtRequestFilters extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
			chain.doFilter(request, response);
			return;
		}

		final String authorizationHeader = request.getHeader("Authorization");

		String jwt = null;

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			jwt = authorizationHeader.substring(7);
			try {
				Claims claim = jwtUtil.getAllClaims(jwt);
				if (claim != null) {
					Integer id = (Integer) claim.get("id");
					String role = (String) claim.get("role");

					List<SimpleGrantedAuthority> authorities = new ArrayList<>();
					if (role != null && !role.trim().isEmpty()) {
						authorities.add(new SimpleGrantedAuthority(role));
						authorities.add(new SimpleGrantedAuthority(role.toLowerCase()));
						authorities.add(new SimpleGrantedAuthority(role.toUpperCase()));
						authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
						authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toLowerCase()));
					}

					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
							id, null, authorities);
					SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				}
			} catch (Exception e) {
				logger.error("Could not set user authentication in security context: " + e.getMessage());
			}
		}
		chain.doFilter(request, response);
	}
}
