package org.sopt.app.presentation.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.notification.PushTokenService;
import org.sopt.app.domain.entity.PushToken;
import org.sopt.app.domain.entity.User;
import org.sopt.app.presentation.notification.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/user")
@SecurityRequirement(name = "Authorization")
public class UserPushTokenController {

    private final PushTokenService pushTokenService;
    private final PushTokenResponseMapper pushTokenResponseMapper;


    @Operation(summary = "푸시 토큰 등록/재등록")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PostMapping(value = "/push-token")
    public ResponseEntity<PushTokenResponse.StatusResponse> updatePushToken(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PushTokenRequest.EditRequest pushTokenRequest
    ) {
        val result = pushTokenService.registerDeviceToken(
                userId,
                pushTokenRequest.getPushToken(),
                pushTokenRequest.getPlatform()
        );
        val response = pushTokenResponseMapper.ofStatus(result);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "푸시 토큰 해제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @DeleteMapping(value = "/push-token")
    public ResponseEntity<PushTokenResponse.StatusResponse> deletePushToken(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PushTokenRequest.DeleteRequest deletePushTokenRequest
    ) {
        if (pushTokenService.isExistDeviceToken(userId, deletePushTokenRequest.getPushToken())) {
            PushToken targetPushToken = pushTokenService.getDeviceToken(
                    userId, deletePushTokenRequest.getPushToken()
            );
            pushTokenService.deleteDeviceToken(targetPushToken);
        }
        return ResponseEntity.ok().build();
    }

}
