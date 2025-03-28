package org.sopt.app.presentation.fortune;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.sopt.app.application.fortune.FortuneService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.sopt.app.domain.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/fortune")
@SecurityRequirement(name = "Authorization")
public class FortuneController {

    private final FortuneService fortuneService;

    @Operation(summary = "오늘의 솝마디 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "401", description = "token error", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/word")
    public ResponseEntity<FortuneResponse> getFortune(
            @AuthenticationPrincipal User user,
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            @RequestParam(name = "todayDate") LocalDate todayDate
    ) {
        return ResponseEntity.ok(
                FortuneResponse.of(
                        fortuneService.getTodayFortuneWordByUserId(user.getId(), todayDate),
                        user.getUsername()
                )
        );
    }

    @Operation(summary = "오늘의 운세 카드 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "401", description = "token error", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/card/today")
    public ResponseEntity<FortuneCardResponse> getFortuneCard(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(
                FortuneCardResponse.of(
                        fortuneService.getTodayFortuneCardByUserId(user.getId())
                )
        );
    }
}
