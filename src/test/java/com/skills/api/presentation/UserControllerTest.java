package com.skills.api.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skills.api.application.UserService;
import com.skills.api.dto.request.LoginRequest;
import com.skills.api.dto.request.SignUpRequest;
import com.skills.global.error.model.ErrorMessage;
import com.skills.support.fixture.UserFixture;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	UserService userService;

	@DisplayName("POST /signup - 사용자의 회원가입 요청이 성공한다. - Void")
	@Test
	void signUp_void_success() throws Exception {
		// Given
		SignUpRequest request = UserFixture.createSignUpRequest();

		// When & Then
		mockMvc.perform(post("/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated());
	}

	@DisplayName("POST /signup - 사용자가 잘못된 형식의 회원가입 요청을 보낸다. - MethodArgumentNotValidException")
	@Test
	void signUp_MethodArgumentNotValidException_fail() throws Exception {
		// Given
		SignUpRequest request = UserFixture.createSignUpRequest(null, null, null, "12345-1234567");

		// When & Then
		mockMvc.perform(post("/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.message").value(ErrorMessage.INVALID_REQUEST_FIELD.getMessage()))
			.andExpect(jsonPath("$.validation.size()").value(4));
	}

	@DisplayName("POST /signup - 사용자가 중복된 아이디로 회원가입 요청한다. - ConflictException")
	@Test
	void signUp_userId_ConflictException_fail() throws Exception {
		// Given
		SignUpRequest request = UserFixture.createSignUpRequest();

		userService.signUp(request);

		// When & Then
		mockMvc.perform(post("/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isConflict())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.message").value(ErrorMessage.CONFLICT_USER_ID.getMessage()));
	}

	@DisplayName("POST /signup - 사용자가 중복된 주민등록번호로 회원가입 요청한다. - ConflictException")
	@Test
	void signUp_regNo_ConflictException_fail() throws Exception {
		// Given
		SignUpRequest request1 = UserFixture.createSignUpRequest();
		SignUpRequest request2 = UserFixture.createSignUpRequest("not-conflict", request1.regNo());

		userService.signUp(request2);

		// When & Then
		mockMvc.perform(post("/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request1)))
			.andExpect(status().isConflict())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.message").value(ErrorMessage.CONFLICT_REG_NO.getMessage()));
	}

	@DisplayName("POST /login - 사용자의 로그인 요청이 성공한다. - LoginResponse")
	@Test
	void login_loginResponse_success() throws Exception {
		// Given
		SignUpRequest signUp = UserFixture.createSignUpRequest();
		LoginRequest request = UserFixture.createLoginRequest(signUp);

		userService.signUp(signUp);

		// When & Then
		mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.accessToken").isString())
			.andExpect(jsonPath("$.refreshToken").isString());
	}

	@DisplayName("POST /login - 사용자가 잘못된 형식의 로그인 요청을 보낸다. - MethodArgumentNotValidException")
	@Test
	void login_MethodArgumentNotValidException_fail() throws Exception {
		// Given
		LoginRequest request = UserFixture.createLoginRequest("", " ");

		// When & Then
		mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.message").value(ErrorMessage.INVALID_REQUEST_FIELD.getMessage()))
			.andExpect(jsonPath("$.validation.size()").value(2));
	}

	@DisplayName("POST /login - 회원가입되지 않은 사용자가 로그인 요청을 한다. - NotFoundException")
	@Test
	void login_NotFoundException_fail() throws Exception {
		// Given
		LoginRequest request = UserFixture.createLoginRequest("hong12", "1234567");

		// When & Then
		mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isNotFound())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.message").value(ErrorMessage.NOT_FOUND_USER_ID.getMessage()));
	}

	@DisplayName("POST /login - 사용자가 잘못된 비밀번호로 로그인 요청을 한다. - BadRequestException")
	@Test
	void login_BadRequestException_fail() throws Exception {
		// Given
		SignUpRequest signUp = UserFixture.createSignUpRequest();
		LoginRequest request = UserFixture.createLoginRequest(signUp.userId(), "invalid-password");

		userService.signUp(signUp);

		// When & Then
		mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isBadRequest())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.message").value(ErrorMessage.INVALID_PASSWORD.getMessage()));
	}
}
