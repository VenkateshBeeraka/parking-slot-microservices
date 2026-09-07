package com.parking.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.parking.common.dto.UserDTO;
import com.parking.common.entity.User;
import com.parking.user.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/{id}")
	public ResponseEntity<User> getUserById(@PathVariable Integer id) {
		return ResponseEntity.ok(userService.getUserById(id));
	}

	@GetMapping
	@PreAuthorize("hasAuthority('admin')")
	public ResponseEntity<List<User>> getAllUsers() {
		return ResponseEntity.ok(userService.getAllUsers());
	}

	@PutMapping("/{id}")
	public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody UserDTO dto) {
		return ResponseEntity.ok(userService.updateUser(id, dto));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('admin')")
	public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
		userService.deleteUser(id);
		return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully with ID: " + id);
	}

	@GetMapping("/count")
	public ResponseEntity<Long> countUsers() {
		return ResponseEntity.ok(userService.countUsers());
	}
}
