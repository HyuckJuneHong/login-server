package com.skills.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 성공 응답 - JWT")
public record LoginResponse(
	@Schema(description = "엑세스 토큰 - JWT")
	String accessToken,
	@Schema(description = "리프레쉬 토큰 - JWT")
	String refreshToken
) {

}
