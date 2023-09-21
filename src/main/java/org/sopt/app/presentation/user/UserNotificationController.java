package org.sopt.app.presentation.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.notification.NotificationOptionService;
import org.sopt.app.application.notification.PushTokenService;
import org.sopt.app.application.user.UserService;
import org.sopt.app.domain.entity.PushToken;
import org.sopt.app.domain.entity.PushTokenPK;
import org.sopt.app.domain.entity.User;
import org.sopt.app.presentation.notification.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/user")
@SecurityRequirement(name = "Authorization")
public class UserNotificationController {

    private final UserService userService;
    private final PushTokenService pushTokenService;
    private final NotificationOptionService notificationOptionService;
    private final UserResponseMapper userResponseMapper;
    private final PushTokenResponseMapper pushTokenResponseMapper;
    private final OptionResponseMapper optionResponseMapper;


    @Operation(summary = "푸시 토큰 등록/재등록")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PostMapping(value = "/push-token")
    public ResponseEntity<PushTokenResponse.StatusResponse> updatePushToken(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PushTokenRequest.EditRequest updatePushTokenRequest
    ) {
        val pushToken = PushToken.builder()
                .playgroundId(user.getPlaygroundId())
                .token(updatePushTokenRequest.getPushToken())
                .build();
        val result = pushTokenService.registerDeviceToken(
                pushToken,
                updatePushTokenRequest.getPlatform()
        );
        val response = pushTokenResponseMapper.ofStatus(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "푸시 토큰 해제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @DeleteMapping(value = "/push-token")
    public ResponseEntity<PushTokenResponse.StatusResponse> deletePushToken(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PushTokenRequest.DeleteRequest deletePushTokenRequest
    ) {
        PushToken targetPushToken = pushTokenService.getDeviceTokenFromLocal(
                PushTokenPK.of(user.getPlaygroundId(), deletePushTokenRequest.getPushToken())
        );
        val result = pushTokenService.deleteDeviceToken(
                targetPushToken,
                deletePushTokenRequest.getPlatform()
        );
        val response = pushTokenResponseMapper.ofStatus(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "푸시 수신 여부 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping(value = "/opt-in")
    public ResponseEntity<UserResponse.IsOptIn> findUserOptIn(
            @AuthenticationPrincipal User user
    ) {
        val response = userResponseMapper.ofIsOptIn(user);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "푸시 수신 여부 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PatchMapping(value = "/opt-in")
    public ResponseEntity<UserResponse.IsOptIn> updateUserOptIn(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UserRequest.EditIsOptInRequest editOptInRequest
    ) {
        val result = userService.updateIsOptIn(user, editOptInRequest);
        val response = userResponseMapper.ofIsOptIn(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "푸시 수신 동의 상세 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping(value = "/opt-in/detail")
    public ResponseEntity<OptionResponse.OptIn> findUserOptInDetail(
            @AuthenticationPrincipal User user
    ) {
        val targetOption = notificationOptionService.getOption(user);
        val response = optionResponseMapper.ofOptIn(targetOption);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "푸시 수신 동의 상세 변경")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PatchMapping(value = "/opt-in/detail")
    public ResponseEntity<OptionResponse.OptIn> updateUserOptInDetail(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody OptionRequest.EditOptInRequest editOptInRequest
    ) {
        val targetOption = notificationOptionService.getOption(user);
        val result = notificationOptionService.updateOptIn(targetOption, editOptInRequest);
        val response = optionResponseMapper.ofOptIn(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
