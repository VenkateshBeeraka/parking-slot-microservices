package com.parking.auth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.parking.auth.repository.AuthenticationRepository;
import com.parking.common.entity.User;
import com.parking.common.exception.ResourceNotFoundException;

@Service
public class MyUserDetailsService implements UserDetailsService {

	@Autowired
	private AuthenticationRepository authenticationRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = authenticationRepository.findByemail(email);
		if (user == null) {
			user = authenticationRepository.findByEmail(email);
		}
		if (user == null) {
			throw new ResourceNotFoundException("user not found with email " + email);
		}
		return new LoginPrincipal(user);
	}
}
