package com.skills.support.fixture;

import static com.skills.global.util.Constant.*;

import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtFixture {

	public static String createExpiredToken(Key secretKey) {
		return Jwts.builder()
			.setExpiration(new Date(System.currentTimeMillis() - 3600 * 1000))
			.signWith(secretKey, SignatureAlgorithm.HS256)
			.setHeaderParam("alg", "HS256")
			.setHeaderParam("typ", JWT)
			.compact();
	}
}
