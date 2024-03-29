package org.sopt.app.presentation.rank;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.mission.MissionService;
import org.sopt.app.application.soptamp.SoptampPointService;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.domain.enums.Part;
import org.sopt.app.presentation.stamp.StampRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/rank")
@SecurityRequirement(name = "Authorization")
public class RankController {

    private final SoptampPointService soptampPointService;
    private final SoptampUserService soptampUserService;
    private final RankResponseMapper rankResponseMapper;
    private final MissionService missionService;

    @Operation(summary = "랭킹 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("")
    public ResponseEntity<List<RankResponse.RankMain>> findRanks() {
        val result = soptampUserService.findRanks();
        val response = rankResponseMapper.of(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "현재 기수 랭킹 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/current")
    public ResponseEntity<List<RankResponse.RankMain>> findCurrentRanks() {
        val soptampPointList = soptampPointService.findCurrentPointList();
        val result = soptampUserService.findCurrentRanks(soptampPointList);
        val response = rankResponseMapper.of(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "파트 별 현재 기수 랭킹 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "400", description = "part not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/current")
    public ResponseEntity<List<RankResponse.RankMain>> findCurrentRanksByPart(
            @Valid @ModelAttribute Part part
    ) {
        val soptampPointList = soptampPointService.findCurrentPointListByPart();
        val result = soptampUserService.findCurrentRanks(soptampPointList);
        val response = rankResponseMapper.of(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "랭킹 상세 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "400", description = "no user with the nickname", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/detail")
    public ResponseEntity<RankResponse.Detail> findRankByNickname(@RequestParam(value = "nickname") String nickname) {
        val result = soptampUserService.findRankByNickname(nickname);
        val missionList = missionService.getCompleteMission(result.getUserId());
        val response = rankResponseMapper.of(result, missionList);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
