package org.sopt.app.presentation.rank;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.facade.RankFacade;
import org.sopt.app.application.soptamp.SoptampPointInfo.PartRank;
import org.sopt.app.domain.enums.Part;
import org.sopt.app.facade.SoptampFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/rank")
@SecurityRequirement(name = "Authorization")
public class RankController {

    private final RankFacade rankFacade;
    private final SoptampFacade soptampFacade;
    private final RankResponseMapper rankResponseMapper;

    @Operation(summary = "현재 기수 랭킹 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/current")
    public ResponseEntity<List<RankResponse.RankMain>> findCurrentRanks() {
        val result = rankFacade.findCurrentRanks();
        return ResponseEntity.ok(rankResponseMapper.of(result));
    }

    @Operation(summary = "파트 별 현재 기수 랭킹 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "400", description = "part not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/current/part/{part}")
    public ResponseEntity<List<RankResponse.RankMain>> findCurrentRanksByPart(
            @PathVariable("part") Part part
    ) {
        val result = rankFacade.findCurrentRanksByPart(part);
        return ResponseEntity.ok(rankResponseMapper.of(result));
    }

    @Operation(summary = "유저 미션 정보 상세 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "400", description = "no user with the nickname", content = @Content),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/detail")
    public ResponseEntity<RankResponse.Detail> findSoptampUserAndCompletedMissionByNickname(
            @RequestParam(value = "nickname") String nickname) {

        return ResponseEntity.ok(soptampFacade.findSoptampUserAndCompletedMissionByNickname(nickname));
    }

    @Operation(summary = "파트끼리의 랭킹 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "success"),
            @ApiResponse(responseCode = "500", description = "server error", content = @Content)
    })
    @GetMapping("/part")
    public ResponseEntity<List<PartRank>> findPartRanks() {

        return ResponseEntity.ok((rankFacade.findAllPartRanks()));
    }
}
