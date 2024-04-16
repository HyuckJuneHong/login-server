package com.skills.api.application;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skills.api.domain.entity.UserEntity;
import com.skills.api.domain.model.PrincipalUser;
import com.skills.api.domain.model.Role;
import com.skills.api.domain.repository.UserRepository;
import com.skills.global.error.exception.NotFoundException;
import com.skills.global.error.model.ErrorMessage;
import com.skills.global.util.Constant;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

	private static final String USER_ID = "userId";
	private static final String NAME = "name";
	private static final String ROLE = "role";

	@Value("${jwt.iss}")
	private String iss;

	@Value("${jwt.secret.access-key}")
	private String salt;

	@Value("${jwt.access-expire}")
	private long accessExpire;

	@Value("${jwt.refresh-expire}")
	private long refreshExpire;

	private Key secretKey;
	private final UserRepository userRepository;

	@PostConstruct
	private void init() {
		secretKey = Keys.hmacShaKeyFor(salt.getBytes(StandardCharsets.UTF_8));
	}

	public String generateAccessToken(String userId, String name, Role role) {
		final Date issuedDate = new Date();
		final Date expiredDate = new Date(issuedDate.getTime() + accessExpire);

		return buildJwt(issuedDate, expiredDate)
			.claim(USER_ID, userId)
			.claim(NAME, name)
			.claim(ROLE, role.name())
			.compact();
	}

	public String generateRefreshToken(String userId) {
		final Date issuedDate = new Date();
		final Date expiredDate = new Date(issuedDate.getTime() + refreshExpire);

		return buildJwt(issuedDate, expiredDate)
			.claim(USER_ID, userId)
			.compact();
	}

	@Transactional
	public String reGenerateToken(String refreshToken, HttpServletResponse response) {
		final Claims claims = getClaimsByToken(refreshToken);
		final String userId = claims.get(USER_ID, String.class);
		final UserEntity user = userRepository.findByUserId(userId)
			.orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_FOUND_USER_ID));

		validateRefreshToken(refreshToken, user.getRefreshToken());

		final String newAccessToken = generateAccessToken(user.getUserId(), user.getName(), user.getRole());
		final String newRefreshToken = generateRefreshToken(user.getUserId());

		user.updateRefreshToken(newRefreshToken);
		response.setHeader(Constant.ACCESS_TOKEN_HEADER, newAccessToken);
		response.setHeader(Constant.REFRESH_TOKEN_HEADER, newRefreshToken);

		return newAccessToken;
	}

	public String extractToken(String header, HttpServletRequest request) {
		String token = request.getHeader(header);

		if (token == null || !token.startsWith(Constant.BEARER)) {
			log.warn("====== {} is null or not bearer =======", header);
			return null;
		}

		return token
			.replaceFirst(Constant.BEARER, "")
			.trim();
	}

	public PrincipalUser extractPrincipalUserByAccessToken(String token) {
		final Claims claims = getClaimsByToken(token);
		final String userId = claims.get(USER_ID, String.class);
		final String name = claims.get(NAME, String.class);
		final Role role = Role.from(claims.get(ROLE, String.class));

		return PrincipalUser.create(userId, name, role);
	}

	public boolean isUsable(String token) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(token);

			return true;
		} catch (ExpiredJwtException e) {
			log.warn("======= Expired JWT =======");
		} catch (IllegalArgumentException e) {
			log.warn("======= Emptied JWT =======");
			throw new NotFoundException(ErrorMessage.NOT_FOUND_JWT);
		} catch (Exception e) {
			log.warn("======= Invalid JWT =======");
			throw new NotFoundException(ErrorMessage.INVALID_JWT);
		}

		return false;
	}

	private void validateRefreshToken(String currentRefreshToken, String savedRefreshToken) {
		if (!currentRefreshToken.equals(savedRefreshToken)) {
			log.warn("======= Invalid RefreshToken =======");
			throw new NotFoundException(ErrorMessage.INVALID_JWT);
		}
	}

	private JwtBuilder buildJwt(Date issuedDate, Date expiredDate) {
		return Jwts.builder()
			.setIssuer(iss)
			.setIssuedAt(issuedDate)
			.setExpiration(expiredDate)
			.signWith(secretKey, SignatureAlgorithm.HS256)
			.setHeaderParam("alg", "HS256")
			.setHeaderParam("typ", Constant.JWT);
	}

	private Claims getClaimsByToken(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build()
			.parseClaimsJws(token)
			.getBody();
	}
}
