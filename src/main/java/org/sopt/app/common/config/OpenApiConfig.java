package org.sopt.app.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!prod")
@OpenAPIDefinition(
        servers = {
                @Server(url = "http://ec2-43-200-170-187.ap-northeast-2.compute.amazonaws.com:8080", description = "개발 환경"),
                @Server(url = "http://localhost:8080", description = "로컬 환경")
        },
        info = @Info(
                title = "SOPT APP Team API",
                version = "v2",
                description = "SOPT 공식 앱팀 API입니다."
        )
)
@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER
)
@Component
public class OpenApiConfig {

}