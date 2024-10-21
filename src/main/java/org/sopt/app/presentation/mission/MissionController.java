package org.sopt.app.presentation.mission;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.val;
import org.sopt.app.application.mission.MissionService;
import org.sopt.app.domain.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v2/mission")
@SecurityRequirement(name = "Authorization")
public class MissionController {

    private final MissionService missionService;
    private final MissionResponseMapper missionResponseMapper;


    @Operation(summary = "미션 전체 조회하기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping(value = "/all")
    public ResponseEntity<List<MissionResponse.Completeness>> findAllMission(@AuthenticationPrincipal User user) {
        val result = missionService.findAllMission(user.getId());
        val response = missionResponseMapper.ofCompleteness(result);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "[SERVER 편의 메서드] 미션 생성하기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @PostMapping("")
    public ResponseEntity<MissionResponse.MissionId> registerMission(
            @Valid @RequestBody MissionRequest.RegisterMissionRequest registerMissionRequest) {
        val mission = missionService.uploadMission(registerMissionRequest);
        val response = missionResponseMapper.of(mission.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "완료 미션만 조회하기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("complete")
    public ResponseEntity<List<MissionResponse.MissionMain>> findCompleteMission(@AuthenticationPrincipal User user) {
        val result = missionService.getCompleteMission(user.getId());
        val response = missionResponseMapper.of(result);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "미완료 미션만 조회하기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("incomplete")
    public ResponseEntity<List<MissionResponse.MissionMain>> findInCompleteMission(@AuthenticationPrincipal User user) {
        val result = missionService.getIncompleteMission(user.getId());
        val response = missionResponseMapper.of(result);
        return ResponseEntity.ok(response);
    }
}
