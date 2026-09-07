package com.parking.building.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.parking.common.entity.Building;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Integer> {

	List<Building> findByPincode(Integer pincode);
}
