package com.skills.api.domain.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.skills.global.error.exception.NotFoundException;
import com.skills.global.error.model.ErrorMessage;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Role {

	USER,
	ADMIN;

	private static final Map<String, Role> ROLE_MAP;

	static {
		ROLE_MAP = Collections.unmodifiableMap(
			Arrays.stream(Role.values())
				.collect(Collectors.toMap(Role::toString, Function.identity()))
		);
	}

	public static Role from(String role) {
		return Optional.ofNullable(ROLE_MAP.get(role.toUpperCase()))
			.orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_FOUND_ROLE));
	}
}
