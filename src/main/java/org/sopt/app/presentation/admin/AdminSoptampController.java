package org.sopt.app.presentation.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.sopt.app.facade.AdminSoptampFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @DeleteMapping(value = "/mission")
    public ResponseEntity deleteAllMissionAndStamp(
    ) {
        adminSoptampFacade.deleteAllMissionAndStamp();
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
