package org.sopt.app.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.platform.dto.PlatformUserInfoResponse;
import org.sopt.app.application.rank.RankCacheService;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.domain.entity.AppjamUser;
import org.sopt.app.domain.entity.soptamp.SoptampUser;
import org.sopt.app.domain.enums.SoptPart;
import org.sopt.app.domain.enums.TeamNumber;
import org.sopt.app.interfaces.postgres.AppjamUserRepository;
import org.sopt.app.interfaces.postgres.SoptampUserRepository;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SoptampUserServiceTest {

	@Mock
	SoptampUserRepository soptampUserRepository;

	@Mock
	AppjamUserRepository appjamUserRepository;

	@Mock
	RankCacheService rankCacheService;

	@InjectMocks
	SoptampUserService soptampUserService;

	private PlatformUserInfoResponse buildProfile(String name, int lastGeneration, String part) {
		PlatformUserInfoResponse.SoptActivities latest =
			new PlatformUserInfoResponse.SoptActivities(
				1,                // activityId
				lastGeneration,   // generation
				part,             // part
				"아무팀"            // team (여기선 안 씀)
			);

		return new PlatformUserInfoResponse(
			1,                  // userId
			name,
			null, null, null, null,
			lastGeneration,
			List.of(latest)
		);
	}

	@BeforeEach
	void setUp() {
		// 기본은 NORMAL 모드로 두고, 테스트에서 필요할 때 변경
		ReflectionTestUtils.setField(soptampUserService, "appjamMode", false);
	}

	/* ==================== NORMAL 모드 테스트 ==================== */

	@Test
	@DisplayName("NORMAL 모드 - 프로필이 null이면 아무 동작도 하지 않는다")
	void 일반모드_프로필널이면_동작없음() {
		// given
		long userId = 1L;

		// when
		soptampUserService.upsertSoptampUser(null, userId);

		// then
		verifyNoInteractions(soptampUserRepository, appjamUserRepository, rankCacheService);
	}

	@Test
	@DisplayName("NORMAL 모드 - 활동 내역이 없으면 아무 동작도 하지 않는다")
	void 일반모드_활동내역없으면_동작없음() {
		// given
		long userId = 1L;

		PlatformUserInfoResponse profile = new PlatformUserInfoResponse(
			1,
			"김솝트",
			null, null, null, null,
			37,
			Collections.emptyList() // soptActivities 비어있음 → getLatestActivity() = null
		);

		// when
		soptampUserService.upsertSoptampUser(profile, userId);

		// then
		verifyNoInteractions(soptampUserRepository, appjamUserRepository, rankCacheService);
	}

	@Test
	@DisplayName("NORMAL 모드 - SoptampUser가 없으면 파트+이름 기반 닉네임으로 새 유저를 생성한다")
	void 일반모드_신규유저면_파트기반닉네임으로_생성() {
		// given
		ReflectionTestUtils.setField(soptampUserService, "appjamMode", false);

		long userId = 1L;
		PlatformUserInfoResponse profile = buildProfile("김솝트", 37, "서버");

		when(soptampUserRepository.findByUserId(userId)).thenReturn(Optional.empty());
		when(soptampUserRepository.existsByNickname(anyString())).thenReturn(false);

		ArgumentCaptor<SoptampUser> captor = ArgumentCaptor.forClass(SoptampUser.class);

		// when
		soptampUserService.upsertSoptampUser(profile, userId);

		// then
		verify(soptampUserRepository).save(captor.capture());
		SoptampUser saved = captor.getValue();

		assertThat(saved.getUserId()).isEqualTo(userId);
		assertThat(saved.getGeneration()).isEqualTo(37L);
		assertThat(saved.getNickname()).contains("김솝트");
		assertThat(saved.getNickname())
			.startsWith(SoptPart.findSoptPartByPartName("서버").getShortedPartName());
		assertThat(saved.getTotalPoints()).isZero();

		verify(rankCacheService).createNewRank(userId);
	}

	@Test
	@DisplayName("NORMAL 모드 - 동일 기수라면 닉네임과 포인트는 변경되지 않는다")
	void 일반모드_기수변경없으면_업데이트안함() {
		// given
		ReflectionTestUtils.setField(soptampUserService, "appjamMode", false);

		long userId = 1L;
		PlatformUserInfoResponse profile = buildProfile("김솝트", 37, "서버");

		SoptampUser existing = SoptampUser.builder()
			.id(10L)
			.userId(userId)
			.nickname("서버김솝트")
			.generation(37L)
			.part(SoptPart.findSoptPartByPartName("서버"))
			.totalPoints(100L)
			.profileMessage("")
			.build();

		when(soptampUserRepository.findByUserId(userId)).thenReturn(Optional.of(existing));

		// when
		soptampUserService.upsertSoptampUser(profile, userId);

		// then
		assertThat(existing.getNickname()).isEqualTo("서버김솝트");
		assertThat(existing.getGeneration()).isEqualTo(37L);
		assertThat(existing.getTotalPoints()).isEqualTo(100L);

		verify(rankCacheService, never()).removeRank(anyLong());
		verify(rankCacheService, never()).createNewRank(anyLong());
	}

	@Test
	@DisplayName("NORMAL 모드 - 기수가 변경되면 닉네임을 재생성하고 포인트를 초기화한다")
	void 일반모드_기수변경되면_닉네임재생성과_포인트리셋() {
		// given
		ReflectionTestUtils.setField(soptampUserService, "appjamMode", false);

		long userId = 1L;
		PlatformUserInfoResponse profile = buildProfile("김솝트", 38, "서버");

		SoptampUser existing = SoptampUser.builder()
			.id(10L)
			.userId(userId)
			.nickname("서버김솝트")
			.generation(37L)
			.part(SoptPart.findSoptPartByPartName("서버"))
			.totalPoints(120L)
			.profileMessage("")
			.build();

		when(soptampUserRepository.findByUserId(userId)).thenReturn(Optional.of(existing));
		when(soptampUserRepository.existsByNicknameAndUserIdNot(anyString(), anyLong()))
			.thenReturn(false);

		// when
		soptampUserService.upsertSoptampUser(profile, userId);

		// then
		assertThat(existing.getGeneration()).isEqualTo(38L);
		assertThat(existing.getNickname()).contains("김솝트");
		assertThat(existing.getNickname())
			.startsWith(SoptPart.findSoptPartByPartName("서버").getShortedPartName());
		assertThat(existing.getTotalPoints()).isZero();

		verify(rankCacheService).removeRank(userId);
		verify(rankCacheService).createNewRank(userId);
	}

	/* ==================== APPJAM 모드 테스트 ==================== */

	@Test
	@DisplayName("APPJAM 모드 - SoptampUser가 없고 AppjamUser가 있으면 팀명+이름으로 앱잼 유저 생성")
	void 앱잼모드_신규유저_AppjamUser있으면_팀명닉네임으로생성() {
		// given
		ReflectionTestUtils.setField(soptampUserService, "appjamMode", true);

		long userId = 1L;
		PlatformUserInfoResponse profile = buildProfile("김솝트", 37, "서버");

		when(soptampUserRepository.findByUserId(userId)).thenReturn(Optional.empty());

		AppjamUser appjamUser = new AppjamUser(
			100L,
			userId,
			"비트",
			TeamNumber.FIRST
		);
		when(appjamUserRepository.findByUserId(userId)).thenReturn(Optional.of(appjamUser));

		when(soptampUserRepository.existsByNickname(anyString())).thenReturn(false);

		ArgumentCaptor<SoptampUser> captor = ArgumentCaptor.forClass(SoptampUser.class);

		// when
		soptampUserService.upsertSoptampUser(profile, userId);

		// then
		verify(soptampUserRepository).save(captor.capture());
		SoptampUser saved = captor.getValue();

		assertThat(saved.getNickname()).startsWith("비트");
		assertThat(saved.getNickname()).contains("김솝트");
		assertThat(saved.getTotalPoints()).isZero();
		assertThat(saved.getGeneration()).isEqualTo(37L);

		verify(rankCacheService).createNewRank(userId);
	}

	@Test
	@DisplayName("APPJAM 모드 - SoptampUser와 AppjamUser가 모두 없으면 기수+기+이름으로 앱잼 유저 생성")
	void 앱잼모드_신규유저_AppjamUser없으면_기수닉네임으로생성() {
		// given
		ReflectionTestUtils.setField(soptampUserService, "appjamMode", true);

		long userId = 1L;
		PlatformUserInfoResponse profile = buildProfile("김솝트", 37, "서버");

		when(soptampUserRepository.findByUserId(userId)).thenReturn(Optional.empty());
		when(appjamUserRepository.findByUserId(userId)).thenReturn(Optional.empty());
		when(soptampUserRepository.existsByNickname(anyString())).thenReturn(false);

		ArgumentCaptor<SoptampUser> captor = ArgumentCaptor.forClass(SoptampUser.class);

		// when
		soptampUserService.upsertSoptampUser(profile, userId);

		// then
		verify(soptampUserRepository).save(captor.capture());
		SoptampUser saved = captor.getValue();

		assertThat(saved.getNickname()).startsWith("37기");
		assertThat(saved.getNickname()).contains("김솝트");
		assertThat(saved.getTotalPoints()).isZero();

		verify(rankCacheService).createNewRank(userId);
	}

	@Test
	@DisplayName("APPJAM 모드 - 기존 닉네임이 파트 기반이면 앱잼 닉네임으로 1회 마이그레이션 후 포인트 초기화")
	void 앱잼모드_파트닉네임이면_앱잼닉네임으로변환_포인트초기화() {
		// given
		ReflectionTestUtils.setField(soptampUserService, "appjamMode", true);

		long userId = 1L;
		PlatformUserInfoResponse profile = buildProfile("김솝트", 37, "서버");

		String partPrefix = SoptPart.findSoptPartByPartName("서버").getShortedPartName();

		SoptampUser existing = SoptampUser.builder()
			.id(10L)
			.userId(userId)
			.nickname(partPrefix + "김솝트") // "서버김솝트"
			.generation(37L)
			.part(SoptPart.findSoptPartByPartName("서버"))
			.totalPoints(50L)
			.profileMessage("")
			.build();

		when(soptampUserRepository.findByUserId(userId)).thenReturn(Optional.of(existing));

		AppjamUser appjamUser = new AppjamUser(
			100L,
			userId,
			"비트",
			TeamNumber.FIRST
		);
		when(appjamUserRepository.findByUserId(userId)).thenReturn(Optional.of(appjamUser));

		when(soptampUserRepository.existsByNicknameAndUserIdNot(anyString(), anyLong()))
			.thenReturn(false);

		// when
		soptampUserService.upsertSoptampUser(profile, userId);

		// then
		assertThat(existing.getNickname()).startsWith("비트");
		assertThat(existing.getNickname()).contains("김솝트");
		assertThat(existing.getTotalPoints()).isZero();
		assertThat(existing.getGeneration()).isEqualTo(37L);

		verify(rankCacheService).updateCachedUserInfo(eq(userId), any());
	}

	@Test
	@DisplayName("APPJAM 모드 - 기존 닉네임이 이미 앱잼 스타일이면 아무 업데이트도 하지 않는다")
	void 앱잼모드_이미앱잼닉이면_업데이트안함() {
		// given
		ReflectionTestUtils.setField(soptampUserService, "appjamMode", true);

		long userId = 1L;
		PlatformUserInfoResponse profile = buildProfile("김솝트", 37, "서버");

		SoptampUser existing = SoptampUser.builder()
			.id(10L)
			.userId(userId)
			.nickname("비트김솝트") // 이미 앱잼 규칙
			.generation(37L)
			.part(SoptPart.findSoptPartByPartName("서버"))
			.totalPoints(30L)
			.profileMessage("")
			.build();

		when(soptampUserRepository.findByUserId(userId)).thenReturn(Optional.of(existing));

		// when
		soptampUserService.upsertSoptampUser(profile, userId);

		// then
		assertThat(existing.getNickname()).isEqualTo("비트김솝트");
		assertThat(existing.getTotalPoints()).isEqualTo(30L);

		verify(rankCacheService, never()).updateCachedUserInfo(anyLong(), any());
	}

	@Test
	@DisplayName("APPJAM 모드 - 다른 유저가 같은 앱잼 닉네임을 쓰고 있으면 접미사 A를 붙여 유니크하게 만든다")
	void 앱잼모드_닉네임충돌시_접미사A추가() {
		// given
		ReflectionTestUtils.setField(soptampUserService, "appjamMode", true);

		long userId = 1L;
		PlatformUserInfoResponse profile = buildProfile("김솝트", 37, "서버");

		String partPrefix = SoptPart.findSoptPartByPartName("서버").getShortedPartName();

		SoptampUser existing = SoptampUser.builder()
			.id(10L)
			.userId(userId)
			.nickname(partPrefix + "김솝트") // "서버김솝트"
			.generation(37L)
			.part(SoptPart.findSoptPartByPartName("서버"))
			.totalPoints(20L)
			.profileMessage("")
			.build();

		when(soptampUserRepository.findByUserId(userId)).thenReturn(Optional.of(existing));

		AppjamUser appjamUser = new AppjamUser(
			100L,
			userId,
			"비트",
			TeamNumber.FIRST
		);
		when(appjamUserRepository.findByUserId(userId)).thenReturn(Optional.of(appjamUser));

		// baseNickname = "비트김솝트" 라고 가정
		// 다른 유저가 이미 baseNickname을 쓰고 있다 → true
		when(soptampUserRepository.existsByNicknameAndUserIdNot(eq("비트김솝트"), eq(userId)))
			.thenReturn(true);
		// "비트김솝트A"는 아직 아무도 안 씀 → false (stub 없으면 기본 false)
		when(soptampUserRepository.existsByNicknameAndUserIdNot(eq("비트김솝트A"), eq(userId)))
			.thenReturn(false);

		// when
		soptampUserService.upsertSoptampUser(profile, userId);

		// then
		assertThat(existing.getNickname()).isEqualTo("비트김솝트A");
		assertThat(existing.getTotalPoints()).isZero();
	}

	@Test
	@DisplayName("NORMAL 모드 - 다른 유저가 같은 파트 기반 닉네임을 쓰고 있으면 접미사 A를 붙인다")
	void 일반모드_닉네임충돌시_접미사A추가() {
		// given
		ReflectionTestUtils.setField(soptampUserService, "appjamMode", false);

		long userId = 1L;
		PlatformUserInfoResponse profile = buildProfile("김솝트", 37, "서버");

		when(soptampUserRepository.findByUserId(userId)).thenReturn(Optional.empty());

		String partPrefix = SoptPart.findSoptPartByPartName("서버").getShortedPartName();
		String baseNickname = partPrefix + "김솝트";

		// 다른 유저가 baseNickname 사용 중
		when(soptampUserRepository.existsByNickname(baseNickname)).thenReturn(true);
		// baseNicknameA는 사용 안 함
		when(soptampUserRepository.existsByNickname(baseNickname + "A")).thenReturn(false);

		ArgumentCaptor<SoptampUser> captor = ArgumentCaptor.forClass(SoptampUser.class);

		// when
		soptampUserService.upsertSoptampUser(profile, userId);

		// then
		verify(soptampUserRepository).save(captor.capture());
		SoptampUser saved = captor.getValue();

		assertThat(saved.getNickname()).isEqualTo(baseNickname + "A");
	}
}
