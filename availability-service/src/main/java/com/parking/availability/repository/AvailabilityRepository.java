package com.parking.availability.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.parking.common.entity.Availability;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Integer> {

}
