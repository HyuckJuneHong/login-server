package com.skills.global.filter;

import static com.skills.global.util.Constant.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.skills.api.application.JwtService;
import com.skills.api.domain.model.PrincipalUser;
import com.skills.global.error.exception.NotFoundException;
import com.skills.global.error.model.ErrorMessage;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final HandlerExceptionResolver handlerExceptionResolver;

	public AuthenticationFilter(
		JwtService jwtService,
		@Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver
	) {
		this.jwtService = jwtService;
		this.handlerExceptionResolver = handlerExceptionResolver;
	}

	@Override
	protected void doFilterInternal(
		@NonNull HttpServletRequest request,
		@NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain
	) {
		String accessToken = jwtService.extractToken(ACCESS_TOKEN_HEADER, request);
		String refreshToken = jwtService.extractToken(REFRESH_TOKEN_HEADER, request);

		try {
			if (jwtService.isUsable(accessToken)) {
				authenticate(accessToken);
				filterChain.doFilter(request, response);

				return;
			}

			if (jwtService.isUsable(refreshToken)) {
				accessToken = jwtService.reGenerateToken(refreshToken, response);
				authenticate(accessToken);
				filterChain.doFilter(request, response);

				return;
			}

			throw new NotFoundException(ErrorMessage.EXPIRED_JWT);
		} catch (Exception e) {
			log.warn("======= JWT Error Description =======");
			handlerExceptionResolver.resolveException(request, response, null, e);
		}
	}

	private void authenticate(String accessToken) {
		final PrincipalUser principalUser = jwtService.extractPrincipalUserByAccessToken(accessToken);
		final List<SimpleGrantedAuthority> roles = List.of(new SimpleGrantedAuthority(principalUser.role().name()));
		final Authentication authentication = new UsernamePasswordAuthenticationToken(principalUser, BLANK, roles);

		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
