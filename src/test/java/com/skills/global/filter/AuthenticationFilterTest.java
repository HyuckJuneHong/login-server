package com.skills.global.filter;

import static com.skills.global.util.Constant.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.skills.api.application.JwtService;
import com.skills.api.domain.model.PrincipalUser;
import com.skills.global.error.exception.NotFoundException;
import com.skills.support.fixture.UserFixture;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class AuthenticationFilterTest {

	@InjectMocks
	AuthenticationFilter authenticationFilter;

	@Mock
	JwtService jwtService;

	@Mock
	HandlerExceptionResolver handlerExceptionResolver;

	MockHttpServletRequest request;
	MockHttpServletResponse response;
	MockFilterChain filterChain;

	@BeforeEach
	void setUp() {
		authenticationFilter = new AuthenticationFilter(jwtService, handlerExceptionResolver);

		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		filterChain = new MockFilterChain();
	}

	@DisplayName("doFilterInternal - AccessToken이 유효한 토큰으로 인증/인가 필터가 성공적으로 동작한다. - Void")
	@Test
	void doFilterInternal_accessToken_void_success() {
		// Given
		String accessToken = "accessToken";
		PrincipalUser principalUser = UserFixture.createPrincipalUser();

		given(jwtService.extractToken(eq(ACCESS_TOKEN_HEADER), any(HttpServletRequest.class))).willReturn(accessToken);
		given(jwtService.isUsable(accessToken)).willReturn(true);
		given(jwtService.extractPrincipalUserByAccessToken(accessToken)).willReturn(principalUser);

		// When
		authenticationFilter.doFilterInternal(request, response, filterChain);
		Authentication actual = SecurityContextHolder.getContext().getAuthentication();

		// Then
		verify(jwtService, times(1)).isUsable(accessToken);
		verify(jwtService, times(1)).extractPrincipalUserByAccessToken(accessToken);

		assertThat(actual.getPrincipal()).isEqualTo(principalUser);
	}

	@DisplayName("doFilterInternal - RefreshToken이 유효한 토큰으로 인증/인가 필터가 성공적으로 동작한다. - Void")
	@Test
	void doFilterInternal_refreshToken_void_success() {
		// Given
		String accessToken = "accessToken";
		String newAccessToken = "newAccessToken";
		String refreshToken = "refreshToken";
		PrincipalUser principalUser = UserFixture.createPrincipalUser();

		given(jwtService.extractToken(eq(ACCESS_TOKEN_HEADER), any(HttpServletRequest.class))).willReturn(accessToken);
		given(jwtService.extractToken(eq(REFRESH_TOKEN_HEADER), any(HttpServletRequest.class)))
			.willReturn(refreshToken);
		given(jwtService.isUsable(accessToken)).willReturn(false);
		given(jwtService.isUsable(refreshToken)).willReturn(true);
		given(jwtService.reGenerateToken(refreshToken, response)).willReturn(newAccessToken);
		given(jwtService.extractPrincipalUserByAccessToken(newAccessToken)).willReturn(principalUser);

		// When
		authenticationFilter.doFilterInternal(request, response, filterChain);
		Authentication actual = SecurityContextHolder.getContext().getAuthentication();

		// Then
		verify(jwtService, times(2)).isUsable(any(String.class));
		verify(jwtService, times(1)).extractPrincipalUserByAccessToken(newAccessToken);

		assertThat(actual.getPrincipal()).isEqualTo(principalUser);
	}

	@DisplayName("doFilterInternal - Access/Refresh 토큰 모두 만료된 토큰이다. - NotFoundException (Expired)")
	@Test
	void doFilterInternal_expired_NotFoundException_fail() {
		// Given
		String token = "Access-Refresh Token";

		given(jwtService.extractToken(any(String.class), any(HttpServletRequest.class))).willReturn(token);
		given(jwtService.isUsable(any(String.class))).willReturn(false);

		// When
		authenticationFilter.doFilterInternal(request, response, filterChain);

		// Then
		verify(handlerExceptionResolver)
			.resolveException(eq(request), eq(response), isNull(), any(NotFoundException.class));
	}
}
