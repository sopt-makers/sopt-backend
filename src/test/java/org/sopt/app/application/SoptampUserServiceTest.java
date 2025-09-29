package org.sopt.app.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.sopt.app.common.fixtures.SoptampUserFixture.PLATFORM_PART_NAME_ANDROID;
import static org.sopt.app.common.fixtures.SoptampUserFixture.PLATFORM_PART_NAME_IOS;
import static org.sopt.app.common.fixtures.SoptampUserFixture.PLATFORM_PART_NAME_SERVER;
import static org.sopt.app.common.fixtures.SoptampUserFixture.SOPTAMP_USER_1;
import static org.sopt.app.common.fixtures.SoptampUserFixture.getSoptampUser;
import static org.sopt.app.common.fixtures.SoptampUserFixture.getSoptampUserWithTotalPoint;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.platform.dto.PlatformUserInfoResponse;
import org.sopt.app.application.platform.dto.PlatformUserInfoResponse.SoptActivities;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.ActivityCardinalInfo;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.PlaygroundProfile;
import org.sopt.app.application.rank.RankCacheService;
import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.application.stamp.StampDeletedEvent;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.fixtures.SoptampFixture;
import org.sopt.app.common.fixtures.SoptampUserFixture;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.User;
import org.sopt.app.domain.enums.SoptPart;
import org.sopt.app.interfaces.postgres.SoptampUserRepository;
import org.sopt.app.domain.entity.soptamp.SoptampUser;

@ExtendWith(MockitoExtension.class)
class SoptampUserServiceTest {

    @Mock
    private SoptampUserRepository soptampUserRepository;

    @Mock
    private RankCacheService rankCacheService;

    @InjectMocks
    private SoptampUserService soptampUserService;

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
        given(soptampUserRepository.findByUserId(userId)).willReturn(Optional.of(soptampUser));

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

        given(soptampUserRepository.findByUserId(anyLong())).willReturn(Optional.empty());

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
            .extracting("userId", "nickname", "generation")
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
            .extracting("userId", "nickname", "generation")
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
            .extracting("userId", "nickname", "generation")
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
            .extracting("userId", "nickname", "generation")
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
            .extracting("userId", "generation", "totalPoints", "part")
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

        given(soptampUserRepository.findByUserId(userId)).willReturn(Optional.of(existingUser));
        given(existingUser.getGeneration()).willReturn(generation);

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