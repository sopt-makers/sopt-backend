package org.sopt.app.presentation.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.facade.AdminSoptampFacade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/admin/soptamp")
@SecurityRequirement(name = "Authorization")
public class AdminSoptampController {

    private final AdminSoptampFacade adminSoptampFacade;
    @Value("${makers.app.admin.password}")
    private String adminPassword;

    @Operation(summary = "Stamp / Soptamp 데이터 초기화",
        description = "쿼리스트링을 통해 선택적으로 삭제 가능. Stamp 삭제 시 해당 이미지, clap 데이터도 함께 삭제됨")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "401", description = "token error", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @DeleteMapping(value = "/clear")
    public ResponseEntity<Void> clearSoptampData(
            @RequestParam(name = "password") String password,
            @RequestParam(name = "stamp", defaultValue = "true") boolean stamp,
            @RequestParam(name = "soptampUser", defaultValue = "true") boolean soptampUser
    ) {
        validateAdmin(password);
        adminSoptampFacade.clearSoptampData(stamp, soptampUser);
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
