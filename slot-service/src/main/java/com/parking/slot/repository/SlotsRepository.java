package com.parking.slot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.parking.common.entity.Slot;

@Repository
public interface SlotsRepository extends JpaRepository<Slot, Integer> {

}
