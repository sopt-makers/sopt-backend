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
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v2/appjamrank")
@SecurityRequirement(name = "Authorization")
public class AppjamRankController {

	private final AppjamRankFacade appjamRankFacade;

	private final AppjamtampResponseMapper appjamtampResponseMapper;

	@Operation(summary = "앱잼팀 랭킹 최근 인증한 미션 TOP3 조회하기")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "success"),
		@ApiResponse(responseCode = "500", description = "server error")
	})
	@GetMapping("/recent")
	public ResponseEntity<AppjamRankResponse.AppjamtampRankListResponse> getRecentTeamRanks() {
		AppjamRankInfo.RankList appjamRankList = appjamRankFacade.findRecentTeamRanks();
		AppjamRankResponse.AppjamtampRankListResponse response = appjamtampResponseMapper.of(appjamRankList);

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "앱잼팀 오늘의 득점 랭킹 TOP10 조회하기")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "success"),
		@ApiResponse(responseCode = "500", description = "server error")
	})
	@GetMapping("/today")
	public ResponseEntity<AppjamRankResponse.AppjamTodayRankListResponse> getTodayTeamRanks() {
		AppjamRankInfo.TodayTeamRankList appjamTodayTeamRankList = appjamRankFacade.findTodayTeamRanks();
		AppjamRankResponse.AppjamTodayRankListResponse response = appjamtampResponseMapper.of(appjamTodayTeamRankList);
		return ResponseEntity.ok(response);
	}
}
