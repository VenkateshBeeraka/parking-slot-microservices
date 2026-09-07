package com.parking.admin.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "building-service")
public interface BuildingClient {

	@GetMapping("/buildings/count")
	Long countBuildings();
}
