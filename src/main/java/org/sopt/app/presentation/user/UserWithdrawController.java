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
import org.springframework.web.bind.annotation.GetMapping;
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

    @Operation(summary = "탈퇴 폼 주소 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "404", description = "not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping(value = "/withdraw/form")
    public ResponseEntity<UserResponse.WithdrawFormResponse> getWithdrawForm() {
        val result = userWithdrawFacade.getWithdrawForm();
        return ResponseEntity.ok(userResponseMapper.of(result));
    }
}