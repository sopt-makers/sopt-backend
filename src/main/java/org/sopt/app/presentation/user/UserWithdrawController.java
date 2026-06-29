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
import org.sopt.app.application.user.UserWithdrawService;
import org.sopt.app.domain.entity.PushToken;
import org.sopt.app.domain.entity.User;
import org.sopt.app.facade.UserWithdrawFacade;
import org.sopt.app.presentation.notification.PushTokenRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/user")
@SecurityRequirement(name = "Authorization")
public class UserWithdrawController {

    private final UserWithdrawService userWithdrawService;
    private final UserWithdrawFacade userWithdrawFacade;
    private final PushTokenService pushTokenService;
    private final UserResponseMapper userResponseMapper;


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
        userWithdrawService.withdrawUser(user.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * 서버 레포 통합 이전, 정책 요구사항에 따라 탈퇴를 위한 임시 API를 개발함.
     * DB에 요청한 유저 ID를 저장한 후, 탈퇴 구글폼 링크를 반환
     */
    @Operation(
            summary = "탈퇴 요청하기 (임시)",
            description = "인증된 사용자의 탈퇴 요청을 기록하고, 상세 사유 입력용 구글 폼 URL을 응답으로 반환함. "
                    + "실제 데이터 삭제는 운영자가 각 서비스에서 수동으로 처리하므로, 200은 '탈퇴 완료'가 아니라 "
                    + "'요청 접수'를 의미. 폼 설정이 없으면 withdrawFormUrl 은 null 로 반환되며 요청 기록은 성공함."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "탈퇴 요청 접수됨 + 폼 URL 반환"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PostMapping(value = "/withdraw")
    public ResponseEntity<UserResponse.WithdrawFormResponse> requestWithdraw(
            @AuthenticationPrincipal Long userId
    ) {
        val result = userWithdrawFacade.requestWithdraw(userId);
        return ResponseEntity.ok(userResponseMapper.of(result));
    }

}
