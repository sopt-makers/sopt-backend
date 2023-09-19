package org.sopt.app.presentation.description;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.auth.PlaygroundAuthService;
import org.springframework.http.HttpStatus;
import org.sopt.app.application.description.DescriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.sopt.app.domain.entity.User;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/description")
@SecurityRequirement(name = "Authorization")
public class DescriptionController {

    private final DescriptionService descriptionService;
    private final PlaygroundAuthService playgroundAuthService;

    @Operation(summary = "메인 문구 조회")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "success"),
        @ApiResponse(responseCode = "401", description = "token error", content = @Content),
        @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/main")
    public ResponseEntity<DescriptionResponse.MainDescription> getMainDescription(
        @AuthenticationPrincipal User user
    ) {
        val userActiveInfo = playgroundAuthService.getPlaygroundUserActiveInfo(user.getPlaygroundToken());
        val response = descriptionService.getMainDescription(userActiveInfo.getStatus());
        return ResponseEntity.status(HttpStatus.OK).body(
            DescriptionResponse.MainDescription.builder()
                .topDescription(response.getTopDescription())
                .bottomDescription(response.getBottomDescription())
                .build()
        );
    }
}
