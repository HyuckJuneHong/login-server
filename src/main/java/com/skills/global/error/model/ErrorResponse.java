package com.skills.global.error.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;

@Builder
public record ErrorResponse(
	String message,
	@JsonInclude(JsonInclude.Include.NON_EMPTY) Map<String, String> validation
) {

}
