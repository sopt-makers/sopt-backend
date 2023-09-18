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
import org.sopt.app.domain.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/user")
@SecurityRequirement(name = "Authorization")
public class UserWithdrawController {

    private final UserService userService;
    private final StampService stampService;
    private final PushTokenService pushTokenService;

    private final UserResponseMapper userResponseMapper;

    @Operation(summary = "로그아웃하기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @DeleteMapping(value = "/logout")
    public ResponseEntity<UserResponse.AppUser> logout(@AuthenticationPrincipal User user) {
        // 정책 : 알림 수신 여부만 비활성화 & 기존 상세 알림 설정은 유지
        // 강제 삭제를 위한 Request 객체 생성
        UserRequest.EditIsOptInRequest editIsOptInRequest = new UserRequest.EditIsOptInRequest();
        editIsOptInRequest.setIsOptIn(false);
        userService.updateIsOptIn(user, editIsOptInRequest);
        pushTokenService.deleteAllDeviceTokenOf(user);
        val response = userResponseMapper.ofAppUser(user);
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
        // TODO: 알림 서버 FCM Token 삭제 요청
        stampService.deleteAllStamps(user);
        pushTokenService.deleteAllDeviceTokenOf(user);
        userService.deleteUser(user);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}