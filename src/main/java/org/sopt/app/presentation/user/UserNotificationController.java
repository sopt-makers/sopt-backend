package org.sopt.app.presentation.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.user.UserService;
import org.sopt.app.domain.entity.User;
import org.sopt.app.presentation.user.UserResponse.UpdatePushToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<UpdatePushToken> updatePushToken(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UserRequest.UpdatePushTokenRequest updatePushTokenRequest
    ) {
        userService.updatePushToken(user, updatePushTokenRequest);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}
