package org.sopt.app.presentation.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.sopt.app.common.exception.UnauthorizedException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.facade.UserFacade;
import org.sopt.app.presentation.user.UserRequest.CreateUserRequest;
import org.sopt.app.presentation.user.UserResponse.Create;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/api/v1/members")
@SecurityRequirement(name = "Authorization")
public class UserInternalController {

    private final UserFacade userFacade;
    private final UserResponseMapper userResponseMapper;

    @Operation(summary = "default 유저 생성")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "401", description = "token error", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Create> createDefaultUserProfile(
            @RequestBody CreateUserRequest request,
            @RequestHeader("apiKey") String apiKey,
            @Value("${internal.auth.api-key}") String internalApiKey
    ){
        validateInternalApiKey(apiKey, internalApiKey);

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
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("memberId") Long userId,
                                           @RequestHeader("apiKey") String apiKey,
                                           @Value("${internal.auth.api-key}") String internalApiKey) {
        validateInternalApiKey(apiKey, internalApiKey);

        userFacade.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    private void validateInternalApiKey(String requestApiKey, String configuredApiKey) {
        if (!configuredApiKey.equals(requestApiKey)) {
            throw new UnauthorizedException(ErrorCode.INVALID_INTERNAL_API_KEY);
        }
    }
}
