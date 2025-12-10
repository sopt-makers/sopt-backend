package org.sopt.app.presentation.mission;

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
import org.sopt.app.presentation.mission.MissionResponse.TeamMissionResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v2/team-mission")
@SecurityRequirement(name = "Authorization")
public class TeamMissionController {

    private final MissionFacade missionFacade;
    private final MissionResponseMapper missionResponseMapper;


    @Operation(summary = "팀 미션 목록 조회하기")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "success"),
        @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping()
    public ResponseEntity<TeamMissionResponses> getMissions(
        @RequestParam TeamNumber teamNumber,
        @RequestParam(required = false) Boolean isCompleted
    ) {
        val result = missionFacade.getMissions(teamNumber, Optional.ofNullable(isCompleted));
        val response = missionResponseMapper.of(result);
        return ResponseEntity.ok(response);
    }
}
