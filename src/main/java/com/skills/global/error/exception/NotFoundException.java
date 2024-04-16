package com.skills.global.error.exception;

import com.skills.global.error.model.ErrorMessage;

public class NotFoundException extends SkillsException {

	public NotFoundException(ErrorMessage errorMessage) {
		super(errorMessage);
	}
}
