package org.sopt.app.presentation.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.playground.PlaygroundAuthService;
import org.sopt.app.application.auth.dto.PlaygroundPostInfo.PlaygroundPost;
import org.sopt.app.domain.entity.User;
import org.sopt.app.facade.UserFacade;
import org.sopt.app.presentation.user.UserResponse.AppService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/user")
@SecurityRequirement(name = "Authorization")
public class UserOriginalController {

    private final PlaygroundAuthService playgroundAuthService;
    private final UserResponseMapper userResponseMapper;

    private final UserFacade userFacade;

    @Operation(summary = "메인 뷰 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "400", description = "no playground, operation profile", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping(value = "/main")
    public ResponseEntity<UserResponse.MainView> getMainViewInfo(
            @AuthenticationPrincipal User user
    ) {
        val response = userFacade.getMainViewInfo(user);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "유저 기수 정보 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "400", description = "no playground, operation profile", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping(value = "/generation")
    public ResponseEntity<UserResponse.Generation> getGenerationInfo(
            @AuthenticationPrincipal User user
    ) {
        val generationUser = playgroundAuthService.getPlaygroundUserActiveInfo(
                user.getPlaygroundToken(), user.getPlaygroundId());
        val response = userResponseMapper.ofGeneration(generationUser);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "앱 서비스 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "400", description = "no playground, operation profile", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping(value = "/app-service")
    public ResponseEntity<List<AppService>> getAppServiceInfo() {
        val response = userFacade.getAppServiceInfo();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "플레이그라운드 인기 게시글 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping(value = "/playground/hot-post")
    public ResponseEntity<PlaygroundPost> getPlaygroundHotPost(
            @AuthenticationPrincipal User user
    ) {
        val response = playgroundAuthService.getPlaygroundHotPost(user.getPlaygroundToken());
        return ResponseEntity.ok(response);
    }
}
