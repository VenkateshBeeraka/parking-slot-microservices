package com.parking.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.parking.common.dto.UserDTO;
import com.parking.common.entity.User;
import com.parking.common.exception.ResourceNotFoundException;
import com.parking.user.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	private Integer currentUserId() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return (auth != null && auth.getPrincipal() instanceof Integer) ? (Integer) auth.getPrincipal() : null;
	}

	private boolean isAdmin() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth != null && auth.getAuthorities().stream().anyMatch(a -> "admin".equalsIgnoreCase(a.getAuthority()));
	}

	// Users may only read or edit their own profile; admins may access any profile
	private void requireSelfOrAdmin(Integer id) {
		if (!isAdmin() && (id == null || !id.equals(currentUserId()))) {
			throw new AccessDeniedException("You can only access your own profile");
		}
	}

	public User getProfile(Integer id) {
		requireSelfOrAdmin(id);
		return getUserById(id);
	}

	public User getUserById(Integer id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
	}

	public User getUserByEmail(String email) {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			user = userRepository.findByemail(email);
		}
		if (user == null) {
			throw new ResourceNotFoundException("User not found with email: " + email);
		}
		return user;
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public User updateUser(Integer id, UserDTO dto) {
		requireSelfOrAdmin(id);
		User user = getUserById(id);
		if (dto.getName() != null && !dto.getName().isBlank()) user.setName(dto.getName().trim());
		if (dto.getCity() != null && !dto.getCity().isBlank()) user.setCity(dto.getCity().trim());
		if (dto.getRole() != null && isAdmin()) user.setRole(dto.getRole());
		return userRepository.save(user);
	}

	public void deleteUser(Integer id) {
		User user = getUserById(id);
		userRepository.delete(user);
	}

	public long countUsers() {
		return userRepository.count();
	}
}
