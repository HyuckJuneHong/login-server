package com.skills.global.util;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {Aes128Util.class})
class Aes128UtilTest {

	@Autowired
	Aes128Util aes128Util;

	@DisplayName("encryptAes - AES 암호화를 성공적으로 진행한다. - String")
	@Test
	void encryptAes_string_success() {
		// Given
		String regNo = "980101-1234567";
		String encodedRegNo = "KqnDzu6g7V6H2P/eokkFeQ==";

		// When
		String actual = aes128Util.encryptAes(regNo);

		// Then
		assertThat(actual).isEqualTo(encodedRegNo);
	}

	@DisplayName("decryptAes - AES 복호화를 성공적으로 진행한다. - String")
	@Test
	void decryptAes_string_success() {
		// Given
		String regNo = "980101-1234567";
		String encodedRegNo = "KqnDzu6g7V6H2P/eokkFeQ==";

		// When
		String actual = aes128Util.decryptAes(encodedRegNo);

		// Then
		assertThat(actual).isEqualTo(regNo);
	}
}
