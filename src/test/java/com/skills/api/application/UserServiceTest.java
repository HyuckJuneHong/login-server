package com.skills.api.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.skills.api.domain.entity.UserEntity;
import com.skills.api.domain.model.Role;
import com.skills.api.domain.repository.UserRepository;
import com.skills.api.dto.request.LoginRequest;
import com.skills.api.dto.request.SignUpRequest;
import com.skills.api.dto.response.LoginResponse;
import com.skills.global.error.exception.BadRequestException;
import com.skills.global.error.exception.ConflictException;
import com.skills.global.error.exception.NotFoundException;
import com.skills.global.error.model.ErrorMessage;
import com.skills.global.util.Aes128Util;
import com.skills.support.fixture.UserFixture;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@InjectMocks
	UserService userService;

	@Mock
	JwtService jwtService;

	@Mock
	UserRepository userRepository;

	@Mock
	PasswordEncoder passwordEncoder;

	@Mock
	Aes128Util aes128Util;

	@DisplayName("signUp - 사용자가 회원가입을 성공적으로 한다. - Void")
	@Test
	void signUp_void_success() {
		// Given
		SignUpRequest signUp = UserFixture.createSignUpRequest();

		given(aes128Util.encryptAes(any(String.class))).willReturn("encodedRegNo");
		given(userRepository.existsByUserId(any(String.class))).willReturn(false);
		given(userRepository.existsByRegNo(any(String.class))).willReturn(false);
		given(passwordEncoder.encode(any(String.class))).willReturn("encodedPassword");

		// When
		userService.signUp(signUp);

		// Then
		verify(userRepository).save(any(UserEntity.class));
	}

	@DisplayName("signUp - 해당 아이디는 이미 생성된 사용자 아이디이다. - ConflictException")
	@Test
	void signUp_userId_ConflictException_fail() {
		// Given
		SignUpRequest signUp = UserFixture.createSignUpRequest();

		given(aes128Util.encryptAes(any(String.class))).willReturn("encodedRegNo");
		given(userRepository.existsByUserId(any(String.class))).willReturn(true);

		// When & Then
		assertThatThrownBy(() -> userService.signUp(signUp))
			.isInstanceOf(ConflictException.class)
			.hasMessage(ErrorMessage.CONFLICT_USER_ID.getMessage());
	}

	@DisplayName("signUp - 해당 주민등록번호는 이미 회원가입을 진행한 사용자이다. - ConflictException")
	@Test
	void signUp_regNo_ConflictException_fail() {
		// Given
		SignUpRequest signUp = UserFixture.createSignUpRequest();

		given(aes128Util.encryptAes(any(String.class))).willReturn("encodedRegNo");
		given(userRepository.existsByUserId(any(String.class))).willReturn(false);
		given(userRepository.existsByRegNo(any(String.class))).willReturn(true);

		// When & Then
		assertThatThrownBy(() -> userService.signUp(signUp))
			.isInstanceOf(ConflictException.class)
			.hasMessage(ErrorMessage.CONFLICT_REG_NO.getMessage());
	}

	@DisplayName("login - 사용자가 로그인을 성공적으로 한다. - LoginResponse")
	@Test
	void login_loginResponse_success() {
		// Given
		String accessToken = "AccessToken";
		String refreshToken = "RefreshToken";
		UserEntity userEntity = UserFixture.createUserEntity();
		LoginRequest login = UserFixture.createLoginRequest(userEntity);

		given(userRepository.findByUserId(any(String.class))).willReturn(Optional.of(userEntity));
		given(passwordEncoder.matches(any(String.class), any(String.class))).willReturn(true);
		given(jwtService.generateRefreshToken(any(String.class))).willReturn(refreshToken);
		given(jwtService.generateAccessToken(any(String.class), any(String.class), any(Role.class)))
			.willReturn(accessToken);

		// When
		LoginResponse actual = userService.login(login);

		// Then
		assertThat(actual.accessToken()).isEqualTo(accessToken);
		assertThat(actual.refreshToken()).isEqualTo(refreshToken);
	}

	@DisplayName("login - 존재하지 않는 사용자 아이디로 로그인 요청한다. - NotFoundException")
	@Test
	void login_NotFoundException_fail() {
		// Given
		UserEntity userEntity = UserFixture.createUserEntity();
		LoginRequest login = UserFixture.createLoginRequest(userEntity);

		given(userRepository.findByUserId(any(String.class))).willReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> userService.login(login))
			.isInstanceOf(NotFoundException.class)
			.hasMessage(ErrorMessage.NOT_FOUND_USER_ID.getMessage());
	}

	@DisplayName("login - 잘못된 비밀번호로 로그인 요청을 한다. - BadRequestException")
	@Test
	void login_BadRequestException_fail() {
		// Given
		UserEntity userEntity = UserFixture.createUserEntity();
		LoginRequest login = UserFixture.createLoginRequest(userEntity);

		given(userRepository.findByUserId(any(String.class))).willReturn(Optional.of(userEntity));
		given(passwordEncoder.matches(any(String.class), any(String.class))).willReturn(false);

		// When & Then
		assertThatThrownBy(() -> userService.login(login))
			.isInstanceOf(BadRequestException.class)
			.hasMessage(ErrorMessage.INVALID_PASSWORD.getMessage());
	}
}
