package com.skills.api.domain.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserEntityTest {

	@DisplayName("create - 사용자를 요구사항에 맞게 생성한다. - UserEntity")
	@Test
	void create_userEntity_success() {
		// Given
		String userId = "hong12";
		String password = "123456";
		String name = "홍도산";
		String regNo = "980101-1234567";

		// When
		UserEntity actual = UserEntity.create(userId, password, name, regNo);

		// Then
		assertThat(actual.getUserId()).isEqualTo(userId);
		assertThat(actual.getPassword()).isEqualTo(password);
		assertThat(actual.getName()).isEqualTo(name);
		assertThat(actual.getRegNo()).isEqualTo(regNo);
	}

	@DisplayName("create - 사용자를 요구사항에 맞게 생성하지 못한다. - NullPointerException")
	@Test
	void create_NullPointerException_fail() {
		// Given
		String userId = "hong123";
		String password = "123456";
		String name = "홍도산";

		// When & Then
		assertThatThrownBy(() -> UserEntity.create(userId, password, name, null))
			.isInstanceOf(NullPointerException.class);
	}
}
