package com.skills.global.error.handler;

import static com.skills.global.error.model.ErrorMessage.*;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.skills.global.error.exception.BadRequestException;
import com.skills.global.error.exception.ConflictException;
import com.skills.global.error.exception.NotFoundException;
import com.skills.global.error.exception.SkillsException;
import com.skills.global.error.model.ErrorResponse;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GlobalExceptionHandler {

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	protected ErrorResponse handleException(Exception e) {
		log.error("======= Unknown Error =======", e);

		return new ErrorResponse(FAILED_UNKNOWN_SZS.getMessage(), null);
	}

	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(SkillsException.class)
	protected ErrorResponse handleSzsException(SkillsException e) {
		log.error("======= Server Error =======", e);

		return new ErrorResponse(e.getMessage(), null);
	}

	@ResponseStatus(HttpStatus.CONFLICT)
	@ExceptionHandler(ConflictException.class)
	protected ErrorResponse handleConflictException(SkillsException e) {
		log.warn("======= Conflict Error =======", e);

		return new ErrorResponse(e.getMessage(), null);
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NotFoundException.class)
	protected ErrorResponse handleNotFoundException(SkillsException e) {
		log.warn("======= Not Found Error =======", e);

		return new ErrorResponse(e.getMessage(), null);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(BadRequestException.class)
	protected ErrorResponse handleBadRequestException(SkillsException e) {
		log.warn("======= Bad Request Error =======", e);

		return new ErrorResponse(e.getMessage(), null);
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ErrorResponse handleException(MethodArgumentNotValidException e) {
		log.warn("======= Bad Request Error =======", e);

		Map<String, String> validation = new HashMap<>();

		e.getBindingResult()
			.getFieldErrors()
			.forEach(fieldError -> validation.put(fieldError.getField(), fieldError.getDefaultMessage()));

		return new ErrorResponse(INVALID_REQUEST_FIELD.getMessage(), validation);
	}
}
