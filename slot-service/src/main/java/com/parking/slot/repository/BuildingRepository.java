package com.parking.slot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.parking.common.entity.Building;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Integer> {

}
