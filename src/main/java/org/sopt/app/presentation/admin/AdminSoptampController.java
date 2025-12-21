package org.sopt.app.presentation.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.*;
import lombok.RequiredArgsConstructor;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.User;
import org.sopt.app.facade.AdminSoptampFacade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/admin/soptamp")
public class AdminSoptampController {

    private final AdminSoptampFacade adminSoptampFacade;
    @Value("${makers.app.admin.password}")
    private String adminPassword;

    @Operation(summary = "스탬프/포인트 전체 초기화")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "401", description = "token error", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @DeleteMapping(value = "/stamp")
    public ResponseEntity<Void> initAllMissionAndStampAndPoints(
            @RequestParam(name = "password") String password
    ) {
        validateAdmin(password);
        adminSoptampFacade.initAllMissionAndStampAndPoints();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "포인트 전체 초기화")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "success"),
        @ApiResponse(responseCode = "401", description = "token error", content = @Content),
        @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @DeleteMapping(value = "/point")
    public ResponseEntity<Void> initPoints(
        @RequestParam(name = "password") String password
    ) {
        validateAdmin(password);
        adminSoptampFacade.initPoints();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "랭킹 캐시 초기화")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "success"),
        @ApiResponse(responseCode = "401", description = "token error", content = @Content),
        @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @DeleteMapping(value = "/cache")
    public ResponseEntity<Void> initRankingCache(
        @RequestParam(name = "password") String password
    ) {
        validateAdmin(password);
        adminSoptampFacade.initRankCache();
        return ResponseEntity.ok().build();
    }

    private void validateAdmin(String password) {
        if (!password.equals(adminPassword)) {
            throw new BadRequestException(ErrorCode.INVALID_APP_ADMIN_PASSWORD);
        }
    }
}
