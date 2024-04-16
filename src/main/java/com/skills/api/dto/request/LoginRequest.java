package com.skills.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 요청")
public record LoginRequest(
	@NotBlank(message = "사용자 아이디를 입력하세요.")
	@Schema(description = "사용자 아이디", example = "userId")
	String userId,
	@NotBlank(message = "사용자 비밀번호를 입력하세요.")
	@Schema(description = "사용자 비밀번호", example = "123456")
	String password
) {

}
