package com.parking.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.parking.common.entity.User;

@Repository
public interface AuthenticationRepository extends JpaRepository<User, Integer> {

	User findByEmail(String email);

	User findByemail(String email);
}
