package org.sopt.app.presentation.config;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.sopt.app.presentation.config.ConfigResponse.Availability;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/config")
public class ConfigController {

    @Value("${makers.app.url.is-available}")
    private Boolean isAvailable;

    @Operation(summary = "앱 메인 뷰 분기 처리")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping(value = "/availability")
    public ResponseEntity<ConfigResponse.Availability> getUserInfo() {
        return ResponseEntity.ok(Availability.builder().isAvailable(isAvailable).build());
    }
}
