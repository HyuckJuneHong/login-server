package com.skills.global.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constant {

	public static final String AES_ENCODING = "AES";
	public static final String AES_INSTANCE = "AES/CBC/PKCS5Padding";

	public static final String JWT = "JWT";
	public static final String BLANK = " ";
	public static final String BEARER = "Bearer";
	public static final String ACCESS_TOKEN_HEADER = "Authorization";
	public static final String REFRESH_TOKEN_HEADER = "Authorization_RefreshToken";
}
