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
import org.sopt.app.domain.entity.User;
import org.sopt.app.presentation.notification.PushTokenRequest;
import org.sopt.app.presentation.notification.PushTokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    public ResponseEntity<PushTokenResponse.StatusResponse> logout(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PushTokenRequest.DeleteRequest deleteRequest
    ) {
        if (pushTokenService.isExistDeviceToken(user.getId(), deleteRequest.getPushToken())) {
            PushToken targetPushToken = pushTokenService.getDeviceToken(
                    user.getId(), deleteRequest.getPushToken()
            );
            pushTokenService.deleteDeviceToken(targetPushToken);
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @Operation(summary = "탈퇴하기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @DeleteMapping(value = "")
    public ResponseEntity<UserResponse.AppUser> withdraw(@AuthenticationPrincipal User user) {
        // TODO: S3 이미지 삭제
        // TODO: 알림 서버 FCM Token 삭제 요청 => pushTokenService#deleteAllDeviceTokenOf 구현이 완료되어야함.
        // 스탬프 정보 삭제
        stampService.deleteAllStamps(user.getId());
        // 푸시 토큰 일괄 삭제
        pushTokenService.deleteAllDeviceTokenOf(user);
        // 유저 정보 삭제
        userService.deleteUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}