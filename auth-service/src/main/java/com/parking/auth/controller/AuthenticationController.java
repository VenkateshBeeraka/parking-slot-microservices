package com.parking.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.parking.auth.service.AuthenticationService;
import com.parking.common.constant.RestUri;
import com.parking.common.dto.LoginRequest;
import com.parking.common.dto.UserDTO;
import com.parking.common.entity.User;

@RestController
public class AuthenticationController {

	@Autowired
	private AuthenticationService authenticationService;

	@PostMapping(path = RestUri.REGISTER)
	public User saveuser(@Validated @RequestBody UserDTO userDto) {
		return authenticationService.saveuser(userDto);
	}

	@PostMapping(path = RestUri.LOGIN)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) {
		return ResponseEntity.status(HttpStatus.OK).body(authenticationService.getLogIn(loginRequest));
	}
}
