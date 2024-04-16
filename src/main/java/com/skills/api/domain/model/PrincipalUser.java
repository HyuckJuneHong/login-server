package com.skills.api.domain.model;

import java.util.Objects;

public record PrincipalUser(
	String userId,
	String name,
	Role role
) {

	public static PrincipalUser create(String userId, String name, Role role) {
		return new PrincipalUser(
			Objects.requireNonNull(userId),
			Objects.requireNonNull(name),
			Objects.requireNonNull(role)
		);
	}
}
