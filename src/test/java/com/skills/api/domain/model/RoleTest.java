package com.skills.api.domain.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.skills.global.error.exception.NotFoundException;
import com.skills.global.error.model.ErrorMessage;

class RoleTest {

	@DisplayName("from - 권한을 성공적으로 가져온다. - Role")
	@Test
	void from_role_success() {
		// When
		Role actual = Role.from("USER");

		// Then
		assertThat(actual).isEqualTo(Role.USER);
	}

	@DisplayName("from - 존재하지 않는 권한이다. - NotFoundException")
	@Test
	void from_NotFoundException_fail() {
		// When & Then
		assertThatThrownBy(() -> Role.from("not-found-role"))
			.isInstanceOf(NotFoundException.class)
			.hasMessage(ErrorMessage.NOT_FOUND_ROLE.getMessage());
	}
}
