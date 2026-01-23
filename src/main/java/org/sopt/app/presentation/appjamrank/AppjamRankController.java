package org.sopt.app.presentation.appjamrank;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.sopt.app.application.appjamrank.AppjamRankInfo;
import org.sopt.app.facade.AppjamRankFacade;
import org.sopt.app.presentation.appjamtamp.AppjamtampResponseMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v2/appjamrank")
@SecurityRequirement(name = "Authorization")
public class AppjamRankController {

	private final AppjamRankFacade appjamRankFacade;

	private final AppjamtampResponseMapper appjamtampResponseMapper;

	@Operation(summary = "앱잼팀 랭킹 최근 인증한 미션 TOP 조회하기")
	@GetMapping("/recent")
	public ResponseEntity<AppjamRankResponse.AppjamtampRankListResponse> getRecentTeamRanks(
		@RequestParam(defaultValue = "3") @Min(1) int size
	) {
		AppjamRankInfo.RankList appjamRankList = appjamRankFacade.findRecentTeamRanks(size);
		AppjamRankResponse.AppjamtampRankListResponse response = appjamtampResponseMapper.of(appjamRankList);

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "앱잼팀 오늘의 득점 랭킹 조회(전체 팀)")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "success"),
		@ApiResponse(responseCode = "500", description = "server error")
	})
	@GetMapping("/today")
	public ResponseEntity<AppjamRankResponse.AppjamTodayRankListResponse> getTodayTeamRanks(
		@RequestParam(defaultValue = "11") @Min(1) int size
	) {
		AppjamRankInfo.TodayTeamRankList appjamTodayTeamRankList = appjamRankFacade.findTodayTeamRanks(size);
		AppjamRankResponse.AppjamTodayRankListResponse response = appjamtampResponseMapper.of(appjamTodayTeamRankList);

		return ResponseEntity.ok(response);
	}
}
