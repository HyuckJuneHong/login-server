package com.skills.global.util;

import static com.skills.global.util.Constant.*;
import static java.nio.charset.StandardCharsets.*;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.skills.global.error.exception.SkillsException;
import com.skills.global.error.model.ErrorMessage;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Aes128Util {

	@Value("${aes.secret.key}")
	private String secretKey;

	@Value("${aes.iv}")
	private String iv;

	private Cipher cipher;
	private SecretKeySpec secretKeySpec;
	private IvParameterSpec ivParameterSpec;

	@PostConstruct
	private void init() {
		try {
			ivParameterSpec = new IvParameterSpec(iv.getBytes(UTF_8));
			secretKeySpec = new SecretKeySpec(secretKey.getBytes(UTF_8), AES_ENCODING);
			cipher = Cipher.getInstance(AES_INSTANCE);
		} catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
			log.error("======= Failed AES init =======");
			throw new SkillsException(ErrorMessage.FAILED_AES_128);
		}
	}

	public String encryptAes(String value) {
		try {
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
			byte[] encrypted = cipher.doFinal(value.getBytes(UTF_8));
			byte[] encoded = Base64.getEncoder().encode(encrypted);

			return new String(encoded, UTF_8);
		} catch (Exception e) {
			log.error("======= Failed AES encrypt =======");
			throw new SkillsException(ErrorMessage.FAILED_AES_ENCRYPT);
		}
	}

	public String decryptAes(String value) {
		try {
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
			byte[] decoded = Base64.getDecoder().decode(value.getBytes(UTF_8));

			return new String(cipher.doFinal(decoded), UTF_8);
		} catch (Exception e) {
			log.error("======= Failed AES decrypt =======");
			throw new SkillsException(ErrorMessage.FAILED_AES_DECRYPT);
		}
	}
}
