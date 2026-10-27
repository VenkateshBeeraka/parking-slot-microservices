package com.parking.auth.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.parking.auth.repository.AuthenticationRepository;
import com.parking.auth.security.LoginPrincipal;
import com.parking.common.dto.AdminLogin;
import com.parking.common.dto.LoginRequest;
import com.parking.common.dto.UserDTO;
import com.parking.common.dto.UserLogin;
import com.parking.common.entity.User;
import com.parking.common.security.JwtUtil;

@Service
public class AuthenticationService {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private AuthenticationRepository authenticationRepository;

	public User saveuser(UserDTO dto) {
		String email = dto.getEmail() != null ? dto.getEmail().trim() : null;
		if (email == null || email.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
		}
		if (authenticationRepository.findByEmail(email) != null) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists");
		}

		// Self-registration always creates a regular user. The id is generated and the role
		// and buildings are never taken from the request, so callers cannot overwrite an
		// existing account or grant themselves admin rights.
		User user = new User();
		user.setName(dto.getName());
		user.setEmail(email);
		user.setPassword(dto.getPassword());
		user.setCity(dto.getCity());
		user.setRole("USER");
		return authenticationRepository.save(user);
	}

	public Object getLogIn(LoginRequest loginRequest) {
		Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
		LoginPrincipal principal = (LoginPrincipal) auth.getPrincipal();

		User myuser = principal.getUser();
		Integer id = myuser.getId();
		String role = myuser.getRole();

		final String jwt = jwtUtil.generateToken(id, role);

		if ("admin".equalsIgnoreCase(role)) {
			User user = authenticationRepository.findById(id).orElse(null);
			return new AdminLogin(jwt, user != null ? user.getBuildings() : null);
		} else {
			return new UserLogin(jwt, new ArrayList<>());
		}
	}
}
