package org.sopt.app.presentation.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.*;
import jakarta.validation.Valid;
import lombok.*;
import org.sopt.app.facade.AuthFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/auth")
public class AppAuthController {

    private final AuthFacade authFacade;

    @Operation(summary = "플그로 로그인/회원가입")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "401", description = "token error", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PostMapping(value = "/playground")
    public ResponseEntity<AppAuthResponse> playgroundLogin(
            @Valid @RequestBody AppAuthRequest.CodeRequest codeRequest
    ) {
        val response = authFacade.loginWithPlayground(codeRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "토큰 리프레시")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "401", description = "token error", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PatchMapping(value = "/refresh")
    public ResponseEntity<AppAuthResponse> refreshToken(
            @Valid @RequestBody AppAuthRequest.RefreshRequest refreshRequest
    ) {
        val response = authFacade.getRefreshToken(refreshRequest);
        return ResponseEntity.ok(response);
    }
}