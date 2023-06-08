package org.sopt.app.presentation.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.user.UserService;
import org.sopt.app.domain.entity.User;
import org.sopt.app.presentation.user.UserRequest.UpdatePushTokenRequest;
import org.sopt.app.presentation.user.UserResponse.OptIn;
import org.sopt.app.presentation.user.UserResponse.PushToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/user")
@SecurityRequirement(name = "Authorization")
public class UserNotificationController {

    private final UserService userService;
    private final UserResponseMapper userResponseMapper;


    @Operation(summary = "푸시 토큰 등록")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PostMapping(value = "/push-token")
    public ResponseEntity<PushToken> updatePushToken(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UserRequest.UpdatePushTokenRequest updatePushTokenRequest
    ) {
        userService.updatePushToken(user, updatePushTokenRequest);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @Operation(summary = "푸시 토큰 해제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @DeleteMapping(value = "/push-token")
    public ResponseEntity<PushToken> deletePushToken(
            @AuthenticationPrincipal User user
    ) {
        userService.updatePushToken(user, new UpdatePushTokenRequest(""));
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @Operation(summary = "푸시 수신 동의 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping(value = "/opt-in")
    public ResponseEntity<OptIn> findUserOptIn(
            @AuthenticationPrincipal User user
    ) {
        val result = userResponseMapper.ofOptIn(user);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
