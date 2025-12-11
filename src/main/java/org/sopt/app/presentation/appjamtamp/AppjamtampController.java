package org.sopt.app.presentation.appjamtamp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.val;
import org.sopt.app.domain.enums.TeamNumber;
import org.sopt.app.facade.MissionFacade;
import org.sopt.app.facade.SoptampFacade;
import org.sopt.app.presentation.appjamtamp.AppjamtampResponse.AppjamMissionResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v2/appjamtamp")
@SecurityRequirement(name = "Authorization")
public class AppjamtampController {

    private final MissionFacade missionFacade;
    private final SoptampFacade soptampFacade;
    private final AppjamtampResponseMapper appjamtampResponseMapper;

    @Operation(summary = "앱잼탬프 미션 목록 조회하기")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "success"),
        @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/mission")
    public ResponseEntity<TeamMissionResponses> getMissions(
        @RequestParam TeamNumber teamNumber,
        @RequestParam(required = false) Boolean isCompleted
    ) {
        val result = missionFacade.getTeamMissions(teamNumber, Optional.ofNullable(isCompleted));
        val response = appjamtampResponseMapper.of(result);
        return ResponseEntity.ok(response);
    }
}