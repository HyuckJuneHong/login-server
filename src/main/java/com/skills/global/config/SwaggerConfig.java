package com.skills.global.config;

import static com.skills.global.util.Constant.*;
import static io.swagger.v3.oas.models.security.SecurityScheme.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	private static final String OPEN_API_TITLE = "APIs Spec";

	@Bean
	public OpenAPI openAPI() {
		final Info info = new Info().title(OPEN_API_TITLE);

		final SecurityRequirement securityRequirement = new SecurityRequirement().addList(ACCESS_TOKEN_HEADER);
		final SecurityScheme accessTokenScheme = getSecurityScheme();
		final Components components = new Components().addSecuritySchemes(ACCESS_TOKEN_HEADER, accessTokenScheme);

		return new OpenAPI()
			.info(info)
			.addSecurityItem(securityRequirement)
			.components(components);
	}

	private SecurityScheme getSecurityScheme() {
		return new SecurityScheme()
			.name(ACCESS_TOKEN_HEADER)
			.type(Type.HTTP)
			.scheme(BEARER)
			.in(In.HEADER)
			.description(JWT);
	}
}
