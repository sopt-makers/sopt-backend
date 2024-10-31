package org.sopt.app.presentation.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.notification.PushTokenService;
import org.sopt.app.application.stamp.StampService;
import org.sopt.app.application.user.UserService;
import org.sopt.app.domain.entity.PushToken;
import org.sopt.app.domain.entity.User;
import org.sopt.app.presentation.notification.PushTokenRequest;
import org.sopt.app.presentation.notification.PushTokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/user")
@SecurityRequirement(name = "Authorization")
public class UserWithdrawController {

    private final UserService userService;
    private final StampService stampService;
    private final PushTokenService pushTokenService;


    @Operation(summary = "로그아웃하기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @DeleteMapping(value = "/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PushTokenRequest.DeleteRequest deleteRequest
    ) {
        if (pushTokenService.isExistDeviceToken(user.getId(), deleteRequest.getPushToken())) {
            PushToken targetPushToken = pushTokenService.getDeviceToken(
                    user.getId(), deleteRequest.getPushToken()
            );
            pushTokenService.deleteDeviceToken(targetPushToken);
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "탈퇴하기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @DeleteMapping(value = "")
    public ResponseEntity<Void> withdraw(@AuthenticationPrincipal User user) {
        userService.withdrawUser(user.getId());
        return ResponseEntity.ok().build();
    }
}