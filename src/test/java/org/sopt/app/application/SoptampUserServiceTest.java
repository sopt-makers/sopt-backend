package org.sopt.app.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.sopt.app.common.fixtures.SoptampUserFixture.PLATFORM_PART_NAME_ANDROID;
import static org.sopt.app.common.fixtures.SoptampUserFixture.PLATFORM_PART_NAME_IOS;
import static org.sopt.app.common.fixtures.SoptampUserFixture.PLATFORM_PART_NAME_SERVER;
import static org.sopt.app.common.fixtures.SoptampUserFixture.getSoptampUser;
import static org.sopt.app.common.fixtures.SoptampUserFixture.getSoptampUserWithTotalPoint;

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
import org.sopt.app.application.platform.dto.PlatformUserInfoResponse.SoptActivities;
import org.sopt.app.application.rank.RankCacheService;
import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.fixtures.SoptampUserFixture;
import org.sopt.app.common.response.ErrorCode;
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

		// 앱잼 시즌: 개인 랭킹 캐시 사용 안 함
		verifyNoInteractions(rankCacheService);
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

		// 앱잼 시즌: 개인 랭킹 캐시 사용 안 함
		verifyNoInteractions(rankCacheService);
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

		// 앱잼 시즌: 개인 랭킹 캐시 사용 안 함 (닉네임 마이그레이션도 캐시 갱신 불필요)
		verifyNoInteractions(rankCacheService);
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

		verifyNoInteractions(rankCacheService);
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

		// baseNickname = "비트김솝트"
		when(soptampUserRepository.existsByNicknameAndUserIdNot(eq("비트김솝트"), eq(userId)))
			.thenReturn(true);
		when(soptampUserRepository.existsByNicknameAndUserIdNot(eq("비트김솝트A"), eq(userId)))
			.thenReturn(false);

		// when
		soptampUserService.upsertSoptampUser(profile, userId);

		// then
		assertThat(existing.getNickname()).isEqualTo("비트김솝트A");
		assertThat(existing.getTotalPoints()).isZero();

		verifyNoInteractions(rankCacheService);
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

		when(soptampUserRepository.existsByNickname(baseNickname)).thenReturn(true);
		when(soptampUserRepository.existsByNickname(baseNickname + "A")).thenReturn(false);

		ArgumentCaptor<SoptampUser> captor = ArgumentCaptor.forClass(SoptampUser.class);

		// when
		soptampUserService.upsertSoptampUser(profile, userId);

		// then
		verify(soptampUserRepository).save(captor.capture());
		SoptampUser saved = captor.getValue();

		assertThat(saved.getNickname()).isEqualTo(baseNickname + "A");
	}

    @Test
    @DisplayName("SUCCESS_솝탬프 유저 정보 조회")
    void SUCCESS_getSoptampUserInfo() {
        //given
        final Long userId = 1L;

        SoptampUser soptampUser = SoptampUserFixture.SOPTAMP_USER_1;


        when(soptampUserRepository.findByUserId(userId)).thenReturn(Optional.of(soptampUser));

        //when
        SoptampUserInfo result = soptampUserService.getSoptampUserInfo(userId);

        //then
        SoptampUserInfo expected = SoptampUserInfo.of(soptampUser);

        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("FAIL_솝탬프 유저 정보 조회 시 유저를 찾지 못한 경우 정상적으로 예외 발생")
    void FAIL_getSoptampUserInfo_whenUserNotFound() {
        //given
        Long userId = -1L;

        when(soptampUserRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> soptampUserService.getSoptampUserInfo(userId))
            .isInstanceOf(BadRequestException.class)
            .satisfies(e -> {
                BadRequestException exception = (BadRequestException) e;
                assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
            });
    }

    @Test
    @DisplayName("SUCCESS_프로필 메시지를 정상적으로 변경")
    void SUCCESS_editProfileMessage() {
        //given
        final Long soptampUserId = 10L;
        final Long userId = 1L;
        final String newProfileMessage = "newProfileMessage";

        SoptampUser soptampUser = getSoptampUser(soptampUserId, userId);
        when(soptampUserRepository.findByUserId(userId)).thenReturn(Optional.of(soptampUser));

        // when
        SoptampUserInfo result = soptampUserService.editProfileMessage(userId, newProfileMessage);

        //then
        assertThat(result.getProfileMessage())
            .isEqualTo(newProfileMessage);
    }

    @Test
    @DisplayName("FAIL_존재하지 않는 유저의 프로필 메시지 수정 시 예외 발생")
    void FAIL_editProfileMessage_whenUserNotFound() {
        //given
        final Long userId = -1L;
        final String newProfileMessage = "newProfileMessage";

        when(soptampUserRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> soptampUserService.editProfileMessage(userId, newProfileMessage))
            .isInstanceOf(BadRequestException.class)
            .satisfies(e -> {
                BadRequestException exception = (BadRequestException) e;
                assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
            });
    }

    @Test
    @DisplayName("SUCCESS_기존 SoptampUser 가 존재하지 않을 경우를 정상적으로 생성함")
    void SUCCESS_upsertSoptampUser_whenSoptampUserNotExist() {
        //given
        final Long userId = 1L;
        final Long generation = 37L;
        final int platformUserId = userId.intValue();

        final SoptActivities activities =
            SoptampUserFixture.getSoptActivities(generation.intValue(), PLATFORM_PART_NAME_SERVER);

        final PlatformUserInfoResponse platformUserInfoResponse =
            SoptampUserFixture.getPlatformUserInfoResponse(platformUserId, List.of(activities));

        when(soptampUserRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

        // when
        soptampUserService.upsertSoptampUser(platformUserInfoResponse, userId);

        // then
        ArgumentCaptor<SoptampUser> soptampUserArgumentCaptor = ArgumentCaptor.forClass(SoptampUser.class);
        verify(soptampUserRepository).save(soptampUserArgumentCaptor.capture());
        SoptampUser capturedSoptampUser = soptampUserArgumentCaptor.getValue();

        assertThat(capturedSoptampUser)
            .extracting(SoptampUser::getUserId, SoptampUser::getNickname, SoptampUser::getGeneration)
            .contains(userId, PLATFORM_PART_NAME_SERVER + platformUserInfoResponse.name(), generation);
        verify(rankCacheService, times(1)).createNewRank(userId);
    }

    @Test
    @DisplayName("SUCCESS_ IOS 파트의 닉네임을 <아요 + 이름>으로 생성함")
    void SUCCESS_upsertSoptampUser_whenIOSUser() {
        //given
        final Long userId = 1L;
        final Long generation = 37L;
        final int platformUserId = userId.intValue();

        final SoptActivities activities =
            SoptampUserFixture.getSoptActivities(generation.intValue(), PLATFORM_PART_NAME_IOS);

        final PlatformUserInfoResponse platformUserInfoResponse =
            SoptampUserFixture.getPlatformUserInfoResponse(platformUserId, List.of(activities));

        when(soptampUserRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

        // when
        soptampUserService.upsertSoptampUser(platformUserInfoResponse, userId);

        // then
        ArgumentCaptor<SoptampUser> soptampUserArgumentCaptor = ArgumentCaptor.forClass(SoptampUser.class);
        verify(soptampUserRepository).save(soptampUserArgumentCaptor.capture());
        SoptampUser capturedSoptampUser = soptampUserArgumentCaptor.getValue();

        assertThat(capturedSoptampUser)
            .extracting(SoptampUser::getUserId, SoptampUser::getNickname, SoptampUser::getGeneration)
            .contains(userId, "아요" + platformUserInfoResponse.name(), generation);
        verify(rankCacheService, times(1)).createNewRank(userId);
    }

    @Test
    @DisplayName("SUCCESS_Android 파트의 닉네임을 <안드 + 이름>으로 생성함")
    void SUCCESS_upsertSoptampUser_whenAndroidUser() {
        //given
        final Long userId = 1L;
        final Long generation = 37L;
        final int platformUserId = userId.intValue();

        final SoptActivities activities =
            SoptampUserFixture.getSoptActivities(generation.intValue(), PLATFORM_PART_NAME_ANDROID);

        final PlatformUserInfoResponse platformUserInfoResponse =
            SoptampUserFixture.getPlatformUserInfoResponse(platformUserId, List.of(activities));

        when(soptampUserRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

        // when
        soptampUserService.upsertSoptampUser(platformUserInfoResponse, userId);

        // then
        ArgumentCaptor<SoptampUser> soptampUserArgumentCaptor = ArgumentCaptor.forClass(SoptampUser.class);
        verify(soptampUserRepository).save(soptampUserArgumentCaptor.capture());
        SoptampUser capturedSoptampUser = soptampUserArgumentCaptor.getValue();

        assertThat(capturedSoptampUser)
            .extracting(SoptampUser::getUserId, SoptampUser::getNickname, SoptampUser::getGeneration)
            .contains(userId, "안드" + platformUserInfoResponse.name(), generation);
        verify(rankCacheService, times(1)).createNewRank(userId);
    }

    @Test
    @DisplayName("SUCCESS_ 동일한 파트, 이름의 유저가 존재하는 경우 닉네임을 <파트 + 이름 + 알파벳>으로 생성함")
    void SUCCESS_upsertSoptampUser_whenDuplicatied() {
        //given
        final Long userId = 1L;
        final Long generation = 37L;
        final int platformUserId = userId.intValue();

        final SoptActivities activities1 =
            SoptampUserFixture.getSoptActivities(generation.intValue(), PLATFORM_PART_NAME_SERVER);

        final PlatformUserInfoResponse platformUserInfoResponse =
            SoptampUserFixture.getPlatformUserInfoResponse(platformUserId, List.of(activities1));

        final String alreadyExistNickName1 = PLATFORM_PART_NAME_SERVER + platformUserInfoResponse.name();
        final String alreadyExistNickName2 = alreadyExistNickName1 + "A";
        final String expectedNickName = alreadyExistNickName1 + "B";

        when(soptampUserRepository.findByUserId(anyLong())).thenReturn(Optional.empty());
        when(soptampUserRepository.existsByNickname(alreadyExistNickName1)).thenReturn(true);
        when(soptampUserRepository.existsByNickname(alreadyExistNickName2)).thenReturn(true);

        // when
        soptampUserService.upsertSoptampUser(platformUserInfoResponse, userId);

        // then
        ArgumentCaptor<SoptampUser> soptampUserArgumentCaptor = ArgumentCaptor.forClass(SoptampUser.class);
        verify(soptampUserRepository).save(soptampUserArgumentCaptor.capture());
        SoptampUser capturedSoptampUser = soptampUserArgumentCaptor.getValue();

        assertThat(capturedSoptampUser)
            .extracting(SoptampUser::getUserId, SoptampUser::getNickname, SoptampUser::getGeneration)
            .contains(userId, expectedNickName, generation);
        verify(rankCacheService, times(1)).createNewRank(userId);
    }

    @Test
    @DisplayName("SUCCESS_soptampUser 가 이미 존재하는 경우 totalPoint를 초기화하고 기수와 파트를 최신으로 업데이트 함.")
    void SUCCESS_upsertSoptampUser_whenAlreadyExist() {
        //given
        final Long userId = 1L;
        final Long generation = 37L;
        final Long soptampUserId = 1L;
        final int platformUserId = userId.intValue();

        final SoptActivities activities1 =
            SoptampUserFixture.getSoptActivities(generation.intValue(), PLATFORM_PART_NAME_SERVER);

        final PlatformUserInfoResponse platformUserInfoResponse =
            SoptampUserFixture.getPlatformUserInfoResponse(platformUserId, List.of(activities1));

        final SoptampUser soptampUser = SoptampUser.builder()
            .id(soptampUserId)
            .userId(userId)
            .nickname("oldNickName")
            .totalPoints(10L)
            .generation(30L)
            .part(SoptPart.IOS)
            .build();

        when(soptampUserRepository.findByUserId(userId)).thenReturn(Optional.of(soptampUser));

        // when
        soptampUserService.upsertSoptampUser(platformUserInfoResponse, userId);

        // then
        assertThat(soptampUser)
            .extracting(SoptampUser::getUserId, SoptampUser::getGeneration, SoptampUser::getTotalPoints, SoptampUser::getPart)
            .contains(userId, generation, 0L, SoptPart.SERVER);
        verify(rankCacheService, times(1)).removeRank(userId);
        verify(rankCacheService, times(1)).createNewRank(userId);
    }

    @Test
    @DisplayName("SUCCESS_soptampUser 가 이미 존재하고 기수가 변경되지 않았다면 업데이트하지 않음.")
    void SUCCESS_upsertSoptampUser_whenAlreadyExistAndSameGeneration() {
        //given
        final Long userId = 1L;
        final Long generation = 37L;
        final int platformUserId = userId.intValue();

        final SoptActivities activities1 =
            SoptampUserFixture.getSoptActivities(generation.intValue(), PLATFORM_PART_NAME_SERVER);

        final PlatformUserInfoResponse platformUserInfoResponse =
            SoptampUserFixture.getPlatformUserInfoResponse(platformUserId, List.of(activities1));

        SoptampUser existingUser = mock(SoptampUser.class);

        when(soptampUserRepository.findByUserId(userId)).thenReturn(Optional.of(existingUser));
        when(existingUser.getGeneration()).thenReturn(generation);

        //when
        soptampUserService.upsertSoptampUser(platformUserInfoResponse, userId);

        //then
        verify(soptampUserRepository, times(1)).findByUserId(userId);
        verify(existingUser, never()).updateChangedGenerationInfo(anyLong(), any(), anyString());
        verify(soptampUserRepository, never()).save(any(SoptampUser.class));
        verify(rankCacheService, never()).removeRank(userId);
        verify(rankCacheService, never()).createNewRank(userId);
    }

    @Test
    @DisplayName("SUCCESS_미션 레벨에 맞게 유저의 포인트가 증가함")
    void SUCCESS_addPointByLevel() {
        //given
        final Long soptampUserId = 10L;
        final Long userId = 1L;
        final Long initialTotalPoints = 100L;

        final Integer level = 2;

        final SoptampUser soptampUser = SoptampUserFixture.getSoptampUserWithTotalPoint(soptampUserId, userId, initialTotalPoints);
        when(soptampUserRepository.findByUserId(userId)).thenReturn(Optional.of(soptampUser));

        //when
        soptampUserService.addPointByLevel(userId, level);

        //then
        assertThat(soptampUser.getTotalPoints())
            .isEqualTo(initialTotalPoints + level);
    }

    @Test
    @DisplayName("FAIL_찾을 수 없는 유저의 포인트를 증가시키려는 경우 예외 발생")
    void FAIL_addPointByLevel_whenUserNotFound() {
        //given
        final Long userId = -1L;
        final Integer level = 2;

        //when
        when(soptampUserRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> soptampUserService.addPointByLevel(userId, level))
            .isInstanceOf(BadRequestException.class)
            .satisfies(e -> {
                BadRequestException exception = (BadRequestException) e;
                assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
            });
    }

    @Test
    @DisplayName("SUCCESS_미션 레벨에 맞게 유저의 포인트가 감소함")
    void SUCCESS_subtractPointByLevel() {
        //given
        final Long soptampUserId = 3L;
        final Long userId = 1L;
        final Integer level = 2;
        final Long initialTotalPoints = 100L;
        final SoptampUser soptampUser = getSoptampUserWithTotalPoint(soptampUserId, userId, initialTotalPoints);

        when(soptampUserRepository.findByUserId(userId)).thenReturn(Optional.of(soptampUser));

        //when
        soptampUserService.subtractPointByLevel(userId, level);

        //then
        assertThat(soptampUser.getTotalPoints())
            .isEqualTo(initialTotalPoints - level);
    }

    @Test
    @DisplayName("FAIL_찾을 수 없는 유저의 포인트를 감소시키려는 경우 예외 발생")
    void FAIL_subtractPointByLevel_whenUserNotFound() {
        //given
        final Long userId = -1L;

        when(soptampUserRepository.findByUserId(userId)).thenReturn(Optional.empty());

        //when & then

        assertThatThrownBy(() -> soptampUserService.subtractPointByLevel(userId, 2))
            .isInstanceOf(BadRequestException.class)
            .satisfies(e -> {
                BadRequestException exception = (BadRequestException) e;
                assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
            });
    }

    @Test
    @DisplayName("SUCCESS_정상적으로 유저의 포인트를 초기화함")
    void SUCCESS_initPoint() {
        //given
        final Long soptampUserId = 1L;
        final Long userId = 1L;
        SoptampUser soptampUser = getSoptampUser(soptampUserId, userId);

        when(soptampUserRepository.findByUserId(userId)).thenReturn(Optional.of(soptampUser));
        when(soptampUserRepository.save(soptampUser)).thenReturn(soptampUser);

        //when
        soptampUserService.initPoint(userId);

        //then
        assertThat(soptampUser.getTotalPoints())
            .isZero();
    }

    @Test
    @DisplayName("FAIL_포인트를 초기화하려는 유저를 찾지 못할 경우 예외가 발생함")
    void FAIL_initPoint() {
        //given
        final Long userId = -1L;

        //when
        when(soptampUserRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

        //then
        assertThrows(BadRequestException.class, () -> soptampUserService.initPoint(userId));
        assertThatThrownBy(() -> soptampUserService.initPoint(userId))
            .isInstanceOf(BadRequestException.class)
            .satisfies(e -> {
                BadRequestException exception = (BadRequestException) e;
                assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
            });
    }

    @Test
    @DisplayName("SUCCESS_모든 soptampUser 의 포인트를 0으로 초기화함")
    void SUCCESS_initAllSoptampUserPoints() {
        //given
        final Long userId = -1L;

        //when
        when(soptampUserRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

        //then
        assertThrows(BadRequestException.class, () -> soptampUserService.initPoint(userId));
        assertThatThrownBy(() -> soptampUserService.initPoint(userId))
            .isInstanceOf(BadRequestException.class)
            .satisfies(e -> {
                BadRequestException exception = (BadRequestException) e;
                assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND);
            });
    }

}
