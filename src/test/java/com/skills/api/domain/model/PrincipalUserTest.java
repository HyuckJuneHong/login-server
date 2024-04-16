package com.skills.api.domain.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PrincipalUserTest {

	@DisplayName("create - Principal 사용자 생성을 성공한다. - PrincipalUser")
	@Test
	void create_PrincipalUser_success() {
		// Given
		String userId = "hong12";
		String name = "홍도산";
		String role = "USER";

		// When
		PrincipalUser actual = PrincipalUser.create(userId, name, Role.from(role));

		// Then
		assertThat(actual.userId()).isEqualTo(userId);
		assertThat(actual.name()).isEqualTo(name);
		assertThat(actual.role()).isEqualTo(Role.from(role));
	}

	@DisplayName("create - Principal 사용자 생성을 성공한다. - NullPointerException")
	@Test
	void create_NullPointerException_success() {
		// Given
		String userId = "hong12";
		String name = "홍도산";

		// When & Then
		assertThatThrownBy(() -> PrincipalUser.create(userId, name, null))
			.isInstanceOf(NullPointerException.class);
	}
}
