package com.skills.support.fixture;

import com.skills.api.domain.entity.UserEntity;
import com.skills.api.domain.model.PrincipalUser;
import com.skills.api.domain.model.Role;
import com.skills.api.dto.request.LoginRequest;
import com.skills.api.dto.request.SignUpRequest;

public class UserFixture {

	public static UserEntity createUserEntity() {
		return UserEntity.create("hong12", "123456", "홍도산", "980101-1234567");
	}

	public static PrincipalUser createPrincipalUser() {
		return PrincipalUser.create("hong12", "홍도산", Role.USER);
	}

	public static SignUpRequest createSignUpRequest() {
		return SignUpRequest.builder()
			.userId("hong12")
			.password("123456")
			.name("홍도산")
			.regNo("980101-1234567")
			.build();
	}

	public static SignUpRequest createSignUpRequest(String userId, String regNo) {
		return SignUpRequest.builder()
			.userId(userId)
			.password("123456")
			.name("홍도산")
			.regNo(regNo)
			.build();
	}

	public static SignUpRequest createSignUpRequest(String userId, String password, String name, String regNo) {
		return SignUpRequest.builder()
			.userId(userId)
			.password(password)
			.name(name)
			.regNo(regNo)
			.build();
	}

	public static LoginRequest createLoginRequest(String userId, String password) {
		return new LoginRequest(userId, password);
	}

	public static LoginRequest createLoginRequest(UserEntity userEntity) {
		return new LoginRequest(userEntity.getUserId(), userEntity.getPassword());
	}

	public static LoginRequest createLoginRequest(SignUpRequest signUpRequest) {
		return new LoginRequest(signUpRequest.userId(), signUpRequest.password());
	}
}
