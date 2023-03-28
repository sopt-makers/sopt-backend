package org.sopt.app.presentation.rank;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.mission.MissionService;
import org.sopt.app.application.rank.RankService;
import org.sopt.app.domain.entity.User;
import org.sopt.app.presentation.BaseController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/rank")
public class RankController extends BaseController {

    private final RankService rankService;
    private final RankResponseMapper rankResponseMapper;
    private final MissionService missionService;

    @Operation(summary = "한마디 편집")
    @PostMapping("/profileMessage")
    public ResponseEntity<RankResponse.Profile> updateUserProfileMessage(
            @AuthenticationPrincipal User user,
            @RequestBody RankRequest.EditProfileMessageRequest editProfileMessageRequest
    ) {
        val result = rankService.updateProfileMessage(user.getId(), editProfileMessageRequest.getProfileMessage());
        val response = rankResponseMapper.of(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "랭킹 목록 조회")
    @GetMapping("")
    public ResponseEntity<List<RankResponse.Main>> findRanks() {
        val result = rankService.findRanks();
        val response = rankResponseMapper.of(result);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "랭킹 상세 조회")
    @GetMapping("/detail")
    public ResponseEntity<RankResponse.Detail> findRankById(@AuthenticationPrincipal User user) {
        val result = rankService.findRankById(user.getId());
        val missionList = missionService.getCompleteMission(user.getId());
        val response = rankResponseMapper.of(result, missionList);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
