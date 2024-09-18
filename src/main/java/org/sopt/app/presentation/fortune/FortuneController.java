package org.sopt.app.presentation.fortune;

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
public class FortuneController {

    @Operation(summary = "오늘의 솝마디 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "401", description = "token error", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/word")
    public ResponseEntity<FortuneResponse> getFortune(
            @AuthenticationPrincipal User user,
            @RequestParam(name = "todayDate") LocalDate todayDate
    ) {
        return ResponseEntity.ok(
                new FortuneResponse("홍길동", "단순하게 생각하면 일이 술술 풀리겠솝!")
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
            new FortuneCardResponse("홍길동", "오늘은 좋은 일이 많이 생기겠솝!", "url")
        );
    }
}
