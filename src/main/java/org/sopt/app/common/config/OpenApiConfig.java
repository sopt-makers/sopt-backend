package org.sopt.app.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!prod")
@OpenAPIDefinition(
        info = @Info(
                title = "SOPT APP Team API",
                version = "v2",
                description = "SOPT 공식 앱팀 API입니다."
        ),
        servers = {
                @Server(url = "${makers.app.base.url}")
        }
)
@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@Component
public class OpenApiConfig {

}