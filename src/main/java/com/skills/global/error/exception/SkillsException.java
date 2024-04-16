package com.skills.global.error.exception;

import com.skills.global.error.model.ErrorMessage;

public class SkillsException extends RuntimeException {

	public SkillsException(ErrorMessage errorMessage) {
		super(errorMessage.getMessage());
	}
}
