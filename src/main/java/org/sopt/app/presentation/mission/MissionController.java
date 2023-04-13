package org.sopt.app.presentation.mission;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.val;
import org.sopt.app.application.mission.MissionService;
import org.sopt.app.application.s3.S3Service;
import org.sopt.app.domain.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v2/mission")
@SecurityRequirement(name = "Authorization")
public class MissionController {

    private final MissionService missionService;
    private final S3Service s3Service;
    private final MissionResponseMapper missionResponseMapper;


    @Operation(summary = "미션 전체 조회하기")
    @GetMapping(value = "/all")
    public ResponseEntity<List<MissionResponse.Completeness>> findAllMission(@AuthenticationPrincipal User user) {
        val result = missionService.findAllMission(user.getId());
        val response = missionResponseMapper.ofCompleteness(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @Operation(summary = "미션 생성하기")
    @PostMapping("")
    public ResponseEntity<MissionResponse.Id> registerMission(
            @RequestBody MissionRequest.RegisterMissionRequest registerMissionRequest) {
        val mission = missionService.uploadMission(registerMissionRequest);
        val response = missionResponseMapper.of(mission.getId());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "완료 미션만 조회하기")
    @GetMapping("complete")
    public ResponseEntity<List<MissionResponse.Main>> findCompleteMission(@AuthenticationPrincipal User user) {
        val result = missionService.getCompleteMission(user.getId());
        val response = missionResponseMapper.of(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "미완료 미션만 조회하기")
    @GetMapping("incomplete")
    public ResponseEntity<List<MissionResponse.Main>> findInCompleteMission(@AuthenticationPrincipal User user) {
        val result = missionService.getIncompleteMission(user.getId());
        val response = missionResponseMapper.of(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
