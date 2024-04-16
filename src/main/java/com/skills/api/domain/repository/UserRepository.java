package com.skills.api.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skills.api.domain.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

	Optional<UserEntity> findByUserId(String userId);

	boolean existsByUserId(String userId);

	boolean existsByRegNo(String regNo);
}
