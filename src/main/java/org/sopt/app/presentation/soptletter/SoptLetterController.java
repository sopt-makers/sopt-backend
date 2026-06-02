package org.sopt.app.presentation.soptletter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.facade.SoptLetterFacade;
import org.sopt.app.presentation.soptletter.dto.SoptLetterResponse.OnboardingProfileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/sopt-letter")
@SecurityRequirement(name = "Authorization")
public class SoptLetterController {

    private final SoptLetterFacade soptLetterFacade;
    private final SoptLetterResponseMapper soptLetterResponseMapper;

    @Operation(summary = "솝레터 온보딩 프로필 조회 (존재하지 않을 경우 생성)")
    @GetMapping("/onboarding")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "success"),
        @ApiResponse(responseCode = "409", description = "conflict"),
        @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    public ResponseEntity<OnboardingProfileResponse> getOrCreateOnboardingProfile(
        @AuthenticationPrincipal Long userId
    ) {
        val result = soptLetterFacade.getOrCreateOnboardingProfile(userId);
        return ResponseEntity.ok(soptLetterResponseMapper.of(result));
    }

    @Operation(summary = "솝레터 온보딩 완료 처리")
    @PostMapping("/onboarding/complete")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "success"),
        @ApiResponse(responseCode = "404", description = "not found", content = @Content),
        @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    public ResponseEntity<OnboardingProfileResponse> completeOnboardingProfile(
        @AuthenticationPrincipal Long userId
    ) {
        val result = soptLetterFacade.completeOnboardingProfile(userId);
        return ResponseEntity.ok(soptLetterResponseMapper.of(result));
    }
}
