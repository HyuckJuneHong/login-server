package com.skills.api.application;

import static com.skills.global.util.Constant.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;

import com.skills.api.domain.entity.UserEntity;
import com.skills.api.domain.model.PrincipalUser;
import com.skills.api.domain.model.Role;
import com.skills.api.domain.repository.UserRepository;
import com.skills.global.error.exception.NotFoundException;
import com.skills.global.error.model.ErrorMessage;
import com.skills.support.fixture.JwtFixture;
import com.skills.support.fixture.UserFixture;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@SpringBootTest(classes = {JwtService.class})
@TestPropertySource(properties = {
	"jwt.iss=test",
	"jwt.secret.access-key=test_test_test_test_test_test_test_test_test_test_test",
	"jwt.access-expire=60000",
	"jwt.refresh-expire=86400000"
})
class JwtServiceTest {

	@Autowired
	JwtService jwtService;

	@MockBean
	UserRepository userRepository;

	Key secretKey;

	@BeforeEach
	void setUp() {
		String salt = "test_test_test_test_test_test_test_test_test_test_test";
		secretKey = Keys.hmacShaKeyFor(salt.getBytes(StandardCharsets.UTF_8));
	}

	@DisplayName("generateAccessToken - 엑세스 토큰 발급을 성공적으로 한다. - accessToken")
	@Test
	void generateAccessToken_accessToken_success() {
		// Given
		String userId = "hong12";
		String name = "홍도산";
		String role = "USER";

		// When
		String accessToken = jwtService.generateAccessToken(userId, name, Role.from(role));
		Jws<Claims> actual = Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build()
			.parseClaimsJws(accessToken);

		// Then
		assertThat(actual.getBody().get("userId", String.class)).isEqualTo(userId);
		assertThat(actual.getBody().get("name", String.class)).isEqualTo(name);
		assertThat(actual.getBody().get("role", String.class)).isEqualTo(role);
	}

	@DisplayName("generateRefreshToken - 리프레쉬 토큰 발급을 성공적으로 한다. - refreshToken")
	@Test
	void generateRefreshToken_refreshToken_success() {
		// Given
		String userId = "hong12";

		// When
		String refreshToken = jwtService.generateRefreshToken(userId);
		Jws<Claims> actual = Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build()
			.parseClaimsJws(refreshToken);

		// Then
		assertThat(actual.getBody().get("userId", String.class)).isEqualTo(userId);
	}

	@DisplayName("reGenerateToken - 리프레쉬 토큰을 이용해서 토큰 재발급을 성공적으로 한다. - accessToken")
	@Test
	void reGenerateToken_accessToken_success() {
		// Given
		UserEntity user = UserFixture.createUserEntity();
		String refreshToken = jwtService.generateRefreshToken(user.getUserId());
		MockHttpServletResponse response = new MockHttpServletResponse();

		user.updateRefreshToken(refreshToken);

		given(userRepository.findByUserId(any(String.class))).willReturn(Optional.of(user));

		// When
		String accessToken = jwtService.reGenerateToken(refreshToken, response);
		Jws<Claims> actual = Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build()
			.parseClaimsJws(accessToken);

		// Then
		assertThat(actual.getBody().get("userId", String.class)).isEqualTo(user.getUserId());
		assertThat(actual.getBody().get("name", String.class)).isEqualTo(user.getName());
		assertThat(actual.getBody().get("role", String.class)).isEqualTo(user.getRole().toString());
	}

	@DisplayName("reGenerateToken - 리프레쉬 토큰 정보에 해당하는 사용자가 없다. - NotFoundException")
	@Test
	void reGenerateToken_user_NotFoundException_fail() {
		// Given
		UserEntity user = UserFixture.createUserEntity();
		String refreshToken = jwtService.generateRefreshToken(user.getUserId());
		MockHttpServletResponse response = new MockHttpServletResponse();

		user.updateRefreshToken(refreshToken);

		given(userRepository.findByUserId(any(String.class))).willReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> jwtService.reGenerateToken(refreshToken, response))
			.isInstanceOf(NotFoundException.class)
			.hasMessage(ErrorMessage.NOT_FOUND_USER_ID.getMessage());
	}

	@DisplayName("reGenerateToken - 해당 리프레쉬 토큰은 이미 재발급에 사용된 토큰이다. - NotFoundException")
	@Test
	void reGenerateToken_jwt_NotFoundException_fail() {
		// Given
		UserEntity user = UserFixture.createUserEntity();
		String refreshToken = jwtService.generateRefreshToken(user.getUserId());
		MockHttpServletResponse response = new MockHttpServletResponse();

		user.updateRefreshToken("used" + refreshToken);

		given(userRepository.findByUserId(any(String.class))).willReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> jwtService.reGenerateToken(refreshToken, response))
			.isInstanceOf(NotFoundException.class)
			.hasMessage(ErrorMessage.NOT_FOUND_USER_ID.getMessage());
	}

	@DisplayName("extractToken - 토큰 추출을 성공적으로 한다 - Token")
	@Test
	void extractToken_token_success() {
		// Given
		String accessToken = "accessToken";

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(ACCESS_TOKEN_HEADER, BEARER + BLANK + accessToken);

		// When
		String actual = jwtService.extractToken(ACCESS_TOKEN_HEADER, request);

		// Then
		assertThat(actual).isEqualTo(accessToken);
	}

	@DisplayName("extractToken - BEAREAR 타입이 아닌 토큰을 추출한다. - Null")
	@Test
	void extractToken_null_fail() {
		// Given
		String accessToken = "accessToken";

		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader(ACCESS_TOKEN_HEADER, accessToken);

		// When
		String actual = jwtService.extractToken(ACCESS_TOKEN_HEADER, request);

		// Then
		assertThat(actual).isNull();
	}

	@DisplayName("extractAuthUserByAccessToken - 엑세스 토큰 정보를 추출해서 PrincipalUser를 생성한다. - PrincipalUser")
	@Test
	void extractAuthUserByAccessToken_PrincipalUser_success() {
		// Given
		String userId = "hong12";
		String name = "홍도산";
		String role = "USER";
		String accessToken = jwtService.generateAccessToken(userId, name, Role.from(role));

		// When
		PrincipalUser actual = jwtService.extractPrincipalUserByAccessToken(accessToken);

		// Then
		assertThat(actual.userId()).isEqualTo(userId);
		assertThat(actual.name()).isEqualTo(name);
		assertThat(actual.role()).isEqualTo(Role.from(role));
	}

	@DisplayName("isUsable - 토큰이 유효하다 - true")
	@Test
	void isUsable_true_success() {
		// Given
		String userId = "hong12";
		String name = "홍도산";
		String role = "USER";
		String accessToken = jwtService.generateAccessToken(userId, name, Role.from(role));

		// When
		boolean actual = jwtService.isUsable(accessToken);

		// Then
		assertThat(actual).isTrue();
	}

	@DisplayName("isUsable - 만료된 토큰이다. - expired(false)")
	@Test
	void isUsable_expired_false_fail() {
		// Given
		String accessToken = JwtFixture.createExpiredToken(secretKey);

		// When
		boolean actual = jwtService.isUsable(accessToken);

		// Then
		assertThat(actual).isFalse();
	}

	@DisplayName("isUsable - 토큰이 빈값이다. - NotFoundException (Empty)")
	@Test
	void isUsable_emptied_NotFoundException_fail() {
		// When & Then
		assertThatThrownBy(() -> jwtService.isUsable(""))
			.isInstanceOf(NotFoundException.class)
			.hasMessage(ErrorMessage.NOT_FOUND_JWT.getMessage());
	}

	@DisplayName("isUsable - 잘못된 토큰이다. - NotFoundException (Invalid)")
	@Test
	void isUsable_invalid_NotFoundException_fail() {
		// When & Then
		assertThatThrownBy(() -> jwtService.isUsable("invalid token"))
			.isInstanceOf(NotFoundException.class)
			.hasMessage(ErrorMessage.INVALID_JWT.getMessage());
	}
}
