package org.sopt.app.presentation.home;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.sopt.app.domain.entity.User;
import org.sopt.app.facade.HomeFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/home")
@SecurityRequirement(name = "Authorization")
public class HomeController {

    private final HomeFacade homeFacade;

    @Operation(summary = "홈 메인 문구 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "401", description = "token error", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/description")
    public ResponseEntity<HomeDescriptionResponse> getHomeMainDescription(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(
                homeFacade.getHomeMainDescription(user)
        );
    }
}
