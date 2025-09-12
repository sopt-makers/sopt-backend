package org.sopt.app.presentation.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.val;

import org.sopt.app.application.platform.PlatformService;
import org.sopt.app.application.playground.PlaygroundAuthService;
import org.sopt.app.common.exception.UnauthorizedException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.facade.UserFacade;
import org.sopt.app.presentation.user.UserRequest.CreateUserRequest;
import org.sopt.app.presentation.user.UserResponse.Create;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/user")
@SecurityRequirement(name = "Authorization")
public class UserOriginalController {

    private final PlaygroundAuthService playgroundAuthService;
    private final PlatformService platformService;
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
            @AuthenticationPrincipal Long userId
    ) {
        val response = userFacade.getMainViewInfo(userId);
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
            @AuthenticationPrincipal Long userId
    ) {
        return ResponseEntity.ok(
            userResponseMapper.ofGeneration(
                platformService.getUserActiveInfo(userId)));
    }

    @Operation
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "success"),
        @ApiResponse(responseCode = "401", description = "token error", content = @Content),
        @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<Create> createDefaultUserProfile(
        @RequestBody CreateUserRequest request,
        @RequestHeader("apiKey") String apiKey,
        @Value("${internal.auth.api-key}") String internalApiKey
    ){
        if(!internalApiKey.equals(apiKey)){
            throw new UnauthorizedException(ErrorCode.INVALID_INTERNAL_API_KEY);
        }

        return ResponseEntity.ok(
            userResponseMapper.ofCreate(
                userFacade.createUser(request.getUserId())));
    }

    @Operation(summary = "default 유저 레코드 삭제(rollback)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "401", description = "token error", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @DeleteMapping("/rollback/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId,
                                                    @RequestHeader("apiKey") String apiKey,
                                                    @Value("${internal.auth.api-key}") String internalApiKey) {
        if (!internalApiKey.equals(apiKey)) {
            throw new UnauthorizedException(ErrorCode.INVALID_INTERNAL_API_KEY);
        }

        userFacade.deleteUser(userId);
        return ResponseEntity.ok().build();
    }
}
