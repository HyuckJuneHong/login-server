package com.skills.global.error.exception;

import com.skills.global.error.model.ErrorMessage;

public class BadRequestException extends SkillsException {

	public BadRequestException(ErrorMessage errorMessage) {
		super(errorMessage);
	}
}
