package org.sopt.app.presentation.auth;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.facade.AuthFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<AppAuthResponse.Token> playgroundLogin(
            @Valid @RequestBody AppAuthRequest.CodeRequest codeRequest
    ) {
        val response = authFacade.loginWithPlayground(codeRequest);
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
        val response = authFacade.getRefreshToken(refreshRequest);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}