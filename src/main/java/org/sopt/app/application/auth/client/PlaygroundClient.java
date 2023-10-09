package org.sopt.app.application.auth.client;

import org.sopt.app.presentation.auth.AppAuthRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "playgroundAuthClient",
        url = "${makers.playground.server}"
)
public interface PlaygroundClient {

    @GetMapping(
            value = "/api/v1/idp/sso/auth",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    AppAuthRequest.AccessTokenRequest getAccessToken(AppAuthRequest.CodeRequest codeRequest);
}
