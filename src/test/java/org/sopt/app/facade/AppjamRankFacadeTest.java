package org.sopt.app.facade;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.appjamrank.AppjamRankInfo;
import org.sopt.app.application.appjamrank.AppjamRankService;
import org.sopt.app.application.playground.PlaygroundAuthService;
import org.sopt.app.common.utils.CurrentDate;
import org.sopt.app.domain.entity.AppjamUser;
import org.sopt.app.domain.enums.TeamNumber;
import org.sopt.app.interfaces.postgres.StampRepositoryCustom;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AppjamRankFacadeTest {

	@Mock
	private PlaygroundAuthService playgroundAuthService;

	@Mock
	private AppjamRankService appjamRankService;

	@InjectMocks
	private AppjamRankFacade appjamRankFacade;

	@Test
	void 오늘_팀랭킹은_오늘점수는_미션레벨합으로_총점은_DB누적점수로_계산된다() {
		// given: 앱잼 유저(팀 멤버) 구성
		AppjamUser user1 = mock(AppjamUser.class);
		when(user1.getUserId()).thenReturn(1L);
		when(user1.getTeamNumber()).thenReturn(TeamNumber.FIRST);
		when(user1.getTeamName()).thenReturn("TeamA");

		AppjamUser user2 = mock(AppjamUser.class);
		when(user2.getUserId()).thenReturn(2L);
		when(user2.getTeamNumber()).thenReturn(TeamNumber.FIRST);
		when(user2.getTeamName()).thenReturn("TeamA");

		AppjamUser user3 = mock(AppjamUser.class);
		when(user3.getUserId()).thenReturn(3L);
		when(user3.getTeamNumber()).thenReturn(TeamNumber.SECOND);
		when(user3.getTeamName()).thenReturn("TeamB");

		List<AppjamUser> allAppjamUsers = List.of(user1, user2, user3);
		when(appjamRankService.findAllAppjamUsers()).thenReturn(allAppjamUsers);

		// given: 오늘 유저별 점수(= mission.level 합산 결과라고 가정)
		LocalDateTime t1 = CurrentDate.now().atStartOfDay().plusHours(1);
		LocalDateTime t2 = CurrentDate.now().atStartOfDay().plusHours(2);
		LocalDateTime t3 = CurrentDate.now().atStartOfDay().plusMinutes(30);

		List<StampRepositoryCustom.AppjamTodayRankSource> sources = List.of(
			new StampRepositoryCustom.AppjamTodayRankSource(1L, 5L, t1),
			new StampRepositoryCustom.AppjamTodayRankSource(2L, 3L, t2),
			new StampRepositoryCustom.AppjamTodayRankSource(3L, 10L, t3)
		);

		when(appjamRankService.findTodayUserRankSources(any(LocalDateTime.class), any(LocalDateTime.class)))
			.thenReturn(sources);

		// given: DB 기준 누적 총점
		when(appjamRankService.findTotalPointsByUserIds(any(Collection.class)))
			.thenReturn(Map.of(
				1L, 100L,
				2L, 200L,
				3L, 50L
			));

		// when
		AppjamRankInfo.TodayTeamRankList result = appjamRankFacade.findTodayTeamRanks(10);

		// then: 팀 랭킹은 2팀(FIRST, SECOND)만 존재
		assertThat(result).isNotNull();
		assertThat(result.getRanks()).hasSize(2);

		// teamB(today 10) > teamA(today 8)
		AppjamRankInfo.TodayTeamRank first = result.getRanks().get(0);
		assertThat(first.getTeamNumber()).isEqualTo(TeamNumber.SECOND);
		assertThat(first.getTeamName()).isEqualTo("TeamB");
		assertThat(first.getTodayPoints()).isEqualTo(10L);
		assertThat(first.getTotalPoints()).isEqualTo(50L);

		AppjamRankInfo.TodayTeamRank second = result.getRanks().get(1);
		assertThat(second.getTeamNumber()).isEqualTo(TeamNumber.FIRST);
		assertThat(second.getTeamName()).isEqualTo("TeamA");
		assertThat(second.getTodayPoints()).isEqualTo(8L);       // 5 + 3
		assertThat(second.getTotalPoints()).isEqualTo(300L);     // 100 + 200

		// 그리고 DB totalPoints 조회가 "앱잼 유저 전체 userId"로 호출되는지 확인
		@SuppressWarnings("unchecked")
		ArgumentCaptor<Collection<Long>> captor = ArgumentCaptor.forClass(Collection.class);
		verify(appjamRankService).findTotalPointsByUserIds(captor.capture());
		assertThat(captor.getValue()).containsExactlyInAnyOrder(1L, 2L, 3L);

		// 오늘 범위 호출도 1회 발생
		verify(appjamRankService, times(1))
			.findTodayUserRankSources(any(LocalDateTime.class), any(LocalDateTime.class));
	}
}
