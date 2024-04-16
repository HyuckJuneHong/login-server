package com.skills.api.dto.request;

import com.skills.global.util.RegExp;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
@Schema(description = "사용자 회원 가입 요청")
public record SignUpRequest(
	@NotBlank(message = "사용자 아이디를 입력하세요.")
	@Schema(description = "사용자 아이디", example = "userId")
	String userId,
	@NotBlank(message = "비밀번호를 입력하세요.")
	@Schema(description = "사용자 비밀번호", example = "123456")
	String password,
	@NotBlank(message = "이름을 입력하세요.")
	@Schema(description = "사용자 이름", example = "홍도산")
	String name,
	@NotBlank(message = "주민등록번호를 입력하세요.")
	@Pattern(message = "주민등록번호 형식에 맞게 입력하세요.", regexp = RegExp.REG_NO_PATTERN)
	@Schema(description = "사용자 주민등록번호", example = "980101-1234567")
	String regNo
) {

}
