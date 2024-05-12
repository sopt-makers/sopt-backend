package org.sopt.app.presentation.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.domain.entity.User;
import org.sopt.app.facade.AdminSoptampFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/admin/soptamp")
public class AdminSoptampController {

    private final AdminSoptampFacade adminSoptampFacade;

    @Operation(summary = "미션/스탬프/포인트 전체 초기화")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "401", description = "token error", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @DeleteMapping(value = "/point")
    public ResponseEntity initAllMissionAndStampAndPoints(
            @AuthenticationPrincipal User user
    ) {
        adminSoptampFacade.initAllMissionAndStampAndPoints(user);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @Operation(summary = "활동 기수 정보 전체 초기화")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "401", description = "token error", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @DeleteMapping(value = "/user")
    public ResponseEntity<AdminSoptampResponse.Rows> initCurrentGenerationInfo(
            @AuthenticationPrincipal User user
    ) {
        val rows = adminSoptampFacade.initCurrentGenerationInfo(user);
        return ResponseEntity.status(HttpStatus.OK).body(rows);
    }
}
