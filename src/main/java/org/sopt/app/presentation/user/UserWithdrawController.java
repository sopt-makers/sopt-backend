package org.sopt.app.presentation.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.notification.PushTokenService;
import org.sopt.app.application.stamp.StampService;
import org.sopt.app.application.user.UserService;
import org.sopt.app.domain.entity.PushToken;
import org.sopt.app.domain.entity.PushTokenPK;
import org.sopt.app.domain.entity.User;
import org.sopt.app.presentation.notification.PushTokenRequest;
import org.sopt.app.presentation.notification.PushTokenResponse;
import org.sopt.app.presentation.notification.PushTokenResponseMapper;
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

    private final PushTokenResponseMapper pushTokenResponseMapper;

    @Operation(summary = "로그아웃하기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @DeleteMapping(value = "/logout")
    public ResponseEntity<PushTokenResponse.StatusResponse> logout(
            @AuthenticationPrincipal User user,
            @RequestHeader(name = "platform") String platform,
            @Valid @RequestBody PushTokenRequest.DeleteRequest deleteRequest
    ) {
        // 정책 : 로그아웃은 로그아웃한 기기에서만 알림을 못 받도록 설정 => User 필드의 수신 동의 항목은 건드리지 말기(다른 기기에서는 그대로 알림을 받아야함.)

        // 현재 로그인한 기기와 유저에 대한 PushToken 조회
        PushToken targetPushToken = pushTokenService.getDeviceTokenFromLocal(
                PushTokenPK.of(user.getPlaygroundId(), deleteRequest.getPushToken())
        );
        val result = pushTokenService.deleteDeviceToken(targetPushToken, platform);

        val response = pushTokenResponseMapper.ofStatus(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
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
        stampService.deleteAllStamps(user);
        // 푸시 토큰 일괄 삭제
        pushTokenService.deleteAllDeviceTokenOf(user);
        // 유저 정보 삭제
        userService.deleteUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}