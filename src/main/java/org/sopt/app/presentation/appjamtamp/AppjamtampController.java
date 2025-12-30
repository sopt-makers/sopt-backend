package org.sopt.app.presentation.appjamtamp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.PostMapping;
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
    private final AppjamtampFacade appjamtampFacade;
    private final AppjamtampResponseMapper appjamtampResponseMapper;

    @Operation(summary = "앱잼탬프 미션 목록 조회하기")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "success"),
        @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/mission")
    public ResponseEntity<AppjamMissionResponses> getMissions(
        @AuthenticationPrincipal Long userId,
        @RequestParam(required = false) TeamNumber teamNumber,
        @RequestParam(required = false) Boolean isCompleted
    ) {
        val result = missionFacade.getTeamMissions(userId, teamNumber, isCompleted);
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

    @Operation(summary = "앱잼탬프 스탬프 제출하기")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "success"),
        @ApiResponse(responseCode = "401", description = "duplicate stamp"),
        @ApiResponse(responseCode = "403", description = "no team", content = @Content),
        @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PostMapping("/stamp")
    public ResponseEntity<AppjamtampResponse.StampMain> getMissions(
        @AuthenticationPrincipal Long userId,
        @Valid @RequestBody AppjamtampRequest.RegisterStampRequest request
    ) {
        val result = appjamtampFacade.uploadStamp(userId, request);
        val response = appjamtampResponseMapper.of(result);
        return ResponseEntity.ok(response);
    }
}