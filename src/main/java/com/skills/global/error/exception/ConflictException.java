package com.skills.global.error.exception;

import com.skills.global.error.model.ErrorMessage;

public class ConflictException extends SkillsException {

	public ConflictException(ErrorMessage errorMessage) {
		super(errorMessage);
	}
}
