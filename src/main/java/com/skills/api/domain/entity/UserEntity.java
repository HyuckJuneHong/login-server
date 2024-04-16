package com.skills.api.domain.entity;

import java.util.Objects;

import com.skills.api.domain.model.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "user_id", nullable = false, unique = true)
	private String userId;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "reg_no", nullable = false, unique = true)
	private String regNo;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	private Role role;

	@Column(name = "refresh_token")
	private String refreshToken;

	private UserEntity(String userId, String password, String name, String regNo, Role role) {
		this.userId = userId;
		this.password = password;
		this.name = name;
		this.regNo = regNo;
		this.role = role;
	}

	public static UserEntity create(String userId, String password, String name, String regNo) {
		return new UserEntity(
			Objects.requireNonNull(userId),
			Objects.requireNonNull(password),
			Objects.requireNonNull(name),
			Objects.requireNonNull(regNo),
			Role.USER
		);
	}

	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
