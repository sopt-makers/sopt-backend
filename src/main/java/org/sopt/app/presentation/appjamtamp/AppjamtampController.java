package org.sopt.app.presentation.appjamtamp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.val;
import org.sopt.app.domain.enums.TeamNumber;
import org.sopt.app.facade.AppjamtampFacade;
import org.sopt.app.facade.MissionFacade;
import org.sopt.app.presentation.appjamtamp.AppjamtampResponse.AppjamMissionResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v2/appjamtamp")
@SecurityRequirement(name = "Authorization")
public class AppjamtampController {

    private final MissionFacade missionFacade;
    private final AppjamtampFacade appjamtampFacade;
    private final AppjamtampResponseMapper appjamtampResponseMapper;

    @Operation(summary = "앱잼탬프 미션 목록 조회하기")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "success"),
        @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/mission")
    public ResponseEntity<AppjamMissionResponses> getMissions(
        @RequestParam TeamNumber teamNumber,
        @RequestParam(required = false) Boolean isCompleted
    ) {
        val result = missionFacade.getTeamMissions(teamNumber, Optional.ofNullable(isCompleted));
        val response = appjamtampResponseMapper.of(result);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "앱잼탬프 스탬프 조회하기")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "success"),
        @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/stamp")
    public ResponseEntity<AppjamtampResponse.AppjamtampView> getMissions(
        @AuthenticationPrincipal Long userId,
        @Valid @ModelAttribute AppjamtampRequest.FindStampRequest findStampRequest
    ) {
        val result = appjamtampFacade.getAppjamtamps(userId, findStampRequest.getMissionId(),
            findStampRequest.getNickname());
        val response = appjamtampResponseMapper.of(result);
        return ResponseEntity.ok(response);
    }
}