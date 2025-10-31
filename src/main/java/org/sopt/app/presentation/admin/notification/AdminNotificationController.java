package org.sopt.app.presentation.admin.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.sopt.app.common.exception.UnauthorizedException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.facade.AdminNotificationFacade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/admin/notification")
@SecurityRequirement(name = "Authorization")
public class AdminNotificationController {

    private final AdminNotificationFacade adminNotificationFacade;

    @Operation(summary = "솝탬프 유저들에게 특정 솝탬프에 대한 알림 전송")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "success"),
        @ApiResponse(responseCode = "401", description = "token error", content = @Content),
        @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PostMapping("/soptamp/showcase")
    public ResponseEntity<Void> createDefaultUserProfile(
        @RequestBody AdminNotificationRequest.SoptampShowcase request,
        @RequestHeader("apiKey") String apiKey,
        @Value("${internal.notification.api-key}") String internalApiKey
    ){
        validateExternalApiKey(apiKey, internalApiKey);

        adminNotificationFacade.sendSoptampShowcase(request.getMissionId(), request.getNickname(),
            request.getNotificationTitle(), request.getNotificationContent());

        return ResponseEntity.ok().build();
    }


    private void validateExternalApiKey(String requestApiKey, String configuredApiKey) {
        if (!configuredApiKey.equals(requestApiKey)) {
            throw new UnauthorizedException(ErrorCode.INVALID_INTERNAL_API_KEY);
        }
    }

}
