package org.sopt.app.presentation.auth;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.auth.JwtTokenService;
import org.sopt.app.application.auth.PlaygroundAuthService;
import org.sopt.app.application.notification.NotificationOptionService;
import org.sopt.app.application.soptamp.SoptampPointService;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.application.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/auth")
public class AppAuthController {

    private final PlaygroundAuthService playgroundAuthService;
    private final UserService userService;
    private final NotificationOptionService optionService;
    private final SoptampUserService soptampUserService;
    private final SoptampPointService soptampPointService;

    private final JwtTokenService jwtTokenService;

    private final AppAuthResponseMapper authResponseMapper;

    @Operation(summary = "플그로 로그인/회원가입")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "401", description = "token error", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PostMapping(value = "/playground")
    public ResponseEntity<AppAuthResponse.Token> playgroundLogin(
            @Valid @RequestBody AppAuthRequest.CodeRequest codeRequest
    ) {
        // 1. PlayGround SSO Auth 를 통해 accessToken 받아옴
        val temporaryToken = playgroundAuthService.getPlaygroundAccessToken(codeRequest);
        // 2. PlayGround Auth Access Token 받옴
        val playgroundToken = playgroundAuthService.refreshPlaygroundToken(temporaryToken);
        // 3. PlayGround User Info 받아옴
        val playgroundMember = playgroundAuthService.getPlaygroundInfo(playgroundToken.getAccessToken());

        val userId = userService.loginWithUserPlaygroundId(playgroundMember);
        val soptampUserId = soptampUserService.updateSoptampUser(playgroundMember.getName(), userId.getId());
        soptampPointService.upsertSoptampPoint(soptampUserId);

        // 4. 기본 알림 설정 저지
        optionService.registerOptIn(userId.getId());

        // 5. Response 할 Body 생성
        val appToken = jwtTokenService.issueNewTokens(userId, playgroundMember);
        val response = authResponseMapper.of(
                appToken.getAccessToken()
                , appToken.getRefreshToken()
                , playgroundMember.getAccessToken()
                , playgroundMember.getStatus()
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "토큰 리프레시")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "401", description = "token error", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PatchMapping(value = "/refresh")
    public ResponseEntity<AppAuthResponse.Token> refreshToken(
            @Valid @RequestBody AppAuthRequest.RefreshRequest refreshRequest
    ) {
        val userId = jwtTokenService.getUserIdFromJwtToken(refreshRequest.getRefreshToken());
        val existingToken = userService.getPlaygroundToken(userId);
        val playgroundToken = playgroundAuthService.refreshPlaygroundToken(existingToken);
        val playgroundMember = playgroundAuthService.getPlaygroundInfo(playgroundToken.getAccessToken());

        val appToken = jwtTokenService.issueNewTokens(userId, playgroundMember);
        val response = authResponseMapper.of(appToken.getAccessToken(), appToken.getRefreshToken(),
                playgroundToken.getAccessToken(), playgroundMember.getStatus());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}