package com.skills.api.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.skills.api.application.UserService;
import com.skills.api.dto.request.LoginRequest;
import com.skills.api.dto.request.SignUpRequest;
import com.skills.api.dto.response.LoginResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
@Tag(name = "사용자 APIs", description = "회원가입 및 로그인")
public class UserController {

	private final UserService userService;

	@PostMapping("/signup")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "회원가입 API", description = "회원 자격을 가진 사용자만 회원가입을 진행할 수 있습니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "성공 - 회원가입"),
		@ApiResponse(responseCode = "400", description = "실패 - 형식에 맞지 않는 요청"),
		@ApiResponse(responseCode = "404", description = "실패 - 회원 자격이 없는 사용자 회원가입 요청 (이름, 주민등록번호)"),
		@ApiResponse(responseCode = "409", description = "실패 - 이미 가입된 사용자 정보 요청 (아이디, 주민등록번호)"),
		@ApiResponse(responseCode = "500", description = "실패 - 알 수 없는 서버 에러 혹은 AES-128 에러")
	})
	public void signUp(@RequestBody @Valid SignUpRequest request) {
		userService.signUp(request);
	}

	@PostMapping("/login")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "로그인 API", description = "로그인할 아이디와 비밀번호를 입력해주세요.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "성공 - 로그인 (JWT 토큰 응답)"),
		@ApiResponse(responseCode = "400", description = "실패 - 올바르지 않은 사용자 비밀번호 요청"),
		@ApiResponse(responseCode = "404", description = "실패 - 존재하지 않는 사용자 아이디 요청"),
		@ApiResponse(responseCode = "500", description = "실패 - 알 수 없는 서버 에러"),
	})
	public LoginResponse login(@RequestBody @Valid LoginRequest request) {
		return userService.login(request);
	}
}
