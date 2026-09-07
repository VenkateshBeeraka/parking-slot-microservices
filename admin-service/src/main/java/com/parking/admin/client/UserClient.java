package com.parking.admin.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "user-service")
public interface UserClient {

	@GetMapping("/users/count")
	Long countUsers();
}
