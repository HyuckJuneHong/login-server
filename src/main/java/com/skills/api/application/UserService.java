package com.skills.api.application;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skills.api.domain.entity.UserEntity;
import com.skills.api.domain.repository.UserRepository;
import com.skills.api.dto.request.LoginRequest;
import com.skills.api.dto.request.SignUpRequest;
import com.skills.api.dto.response.LoginResponse;
import com.skills.global.error.exception.BadRequestException;
import com.skills.global.error.exception.ConflictException;
import com.skills.global.error.exception.NotFoundException;
import com.skills.global.error.model.ErrorMessage;
import com.skills.global.util.Aes128Util;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

	private final Aes128Util aes128Util;
	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public void signUp(SignUpRequest signUp) {
		final String encodedRegNo = aes128Util.encryptAes(signUp.regNo());

		validateUserIdConflict(signUp.userId());
		validateRegNoConflict(encodedRegNo);

		final String encodedPassword = passwordEncoder.encode(signUp.password());
		final UserEntity userEntity = UserEntity.create(signUp.userId(), encodedPassword, signUp.name(), encodedRegNo);

		userRepository.save(userEntity);
	}

	@Transactional
	public LoginResponse login(LoginRequest login) {
		final UserEntity user = getByUserId(login.userId());
		validatePassword(login.password(), user.getPassword());

		final String accessToken = jwtService.generateAccessToken(user.getUserId(), user.getName(), user.getRole());
		final String refreshToken = jwtService.generateRefreshToken(user.getUserId());
		user.updateRefreshToken(refreshToken);

		return new LoginResponse(accessToken, refreshToken);
	}

	public UserEntity getByUserId(String userId) {
		return userRepository.findByUserId(userId)
			.orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_FOUND_USER_ID));
	}

	private void validatePassword(String password, String encodedPassword) {
		if (!passwordEncoder.matches(password, encodedPassword)) {
			throw new BadRequestException(ErrorMessage.INVALID_PASSWORD);
		}
	}

	private void validateUserIdConflict(String userId) {
		if (userRepository.existsByUserId(userId)) {
			throw new ConflictException(ErrorMessage.CONFLICT_USER_ID);
		}
	}

	private void validateRegNoConflict(String regNo) {
		if (userRepository.existsByRegNo(regNo)) {
			throw new ConflictException(ErrorMessage.CONFLICT_REG_NO);
		}
	}
}
