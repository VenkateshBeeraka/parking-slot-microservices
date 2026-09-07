package com.parking.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.parking.common.dto.UserDTO;
import com.parking.common.entity.User;
import com.parking.common.exception.ResourceNotFoundException;
import com.parking.user.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

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
		User user = getUserById(id);
		if (dto.getName() != null) user.setName(dto.getName());
		if (dto.getCity() != null) user.setCity(dto.getCity());
		if (dto.getRole() != null) user.setRole(dto.getRole());
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
