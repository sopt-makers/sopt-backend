package org.sopt.app.presentation.soptletter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.facade.SoptLetterFacade;
import org.sopt.app.presentation.soptletter.dto.SoptLetterResponse.GeneratedNicknameResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/sopt-letter")
@SecurityRequirement(name = "Authorization")
public class SoptLetterController {

    private final SoptLetterFacade soptLetterFacade;
    private final SoptLetterResponseMapper soptLetterResponseMapper;

    @Operation(summary = "온보딩 닉네임 생성/조회")
    @GetMapping("/onboarding/nickname")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "success"),
        @ApiResponse(responseCode = "409", description = "conflict"),
        @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    public ResponseEntity<GeneratedNicknameResponse> getOnboardingNickname(
        @AuthenticationPrincipal Long userId
    ) {
        val result = soptLetterFacade.generateProfileNickname(userId);
        return ResponseEntity.ok(soptLetterResponseMapper.of(result));
    }
}
