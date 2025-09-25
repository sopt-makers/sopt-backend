package org.sopt.app.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.sopt.app.common.fixtures.SoptampUserFixture.SOPTAMP_USER_1;
import static org.sopt.app.common.fixtures.SoptampUserFixture.getSoptampUser;

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
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.ActivityCardinalInfo;
import org.sopt.app.application.playground.dto.PlaygroundProfileInfo.PlaygroundProfile;
import org.sopt.app.application.rank.RankCacheService;
import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.fixtures.SoptampFixture;
import org.sopt.app.common.fixtures.SoptampUserFixture;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.User;
import org.sopt.app.domain.enums.PlaygroundPart;
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
        final Long stampId = 10L;
        final Long userId = 1L;
        final String newProfileMessage = "newProfileMessage";

        SoptampUser soptampUser = getSoptampUser(stampId, userId);
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

//    @Test
//    @DisplayName("SUCCESS_기존 솝탬프 유저가 없다면 새로 생성")
//    void SUCCESS_upsertSoptampUserIfEmpty() {
//        //given
//        given(soptampUserRepository.findByUserId(anyLong())).willReturn(Optional.empty());
//        PlaygroundProfile profile = PlaygroundProfile.builder()
//                .name("name")
//                .activities(List.of(new ActivityCardinalInfo("35,서버")))
//                .build();
//        Long userId = 1L;
//        //when
//        soptampUserService.upsertSoptampUser(profile, userId);
//        String expectedNickname = profile.getLatestActivity().getPlaygroundPart().getShortedPartName()+ profile.getName();
//
//        //then
//        ArgumentCaptor<SoptampUser> captor = ArgumentCaptor.forClass(SoptampUser.class);
//        verify(soptampUserRepository, times(1)).existsByNickname(anyString());
//        verify(soptampUserRepository, times(1)).save(captor.capture());
//        assertThat(captor.getValue().getUserId()).isEqualTo(userId);
//        assertThat(captor.getValue().getNickname()).isEqualTo(expectedNickname);
//        assertThat(captor.getValue().getPart().getPartName())
//                .isEqualTo(profile.getLatestActivity().getPlaygroundPart().getPartName());
//        assertThat(captor.getValue().getGeneration()).isEqualTo(profile.getLatestActivity().getGeneration());
//    }
//
//    @Test
//    void 기존_솝탬프_유저가_없다면_새로_생성_닉네임_중복시_suffix_추가() {
//        //given
//        Long userId = 1L;
//        PlaygroundProfile profile = PlaygroundProfile.builder()
//                .name("name")
//                .activities(List.of(new ActivityCardinalInfo("35,서버")))
//                .build();
//        given(soptampUserRepository.findByUserId(anyLong())).willReturn(Optional.empty());
//        given(soptampUserRepository.existsByNickname(
//                profile.getLatestActivity().getPlaygroundPart().getShortedPartName() + profile.getName()))
//                .willReturn(true);
//        given(soptampUserRepository.existsByNickname(
//                profile.getLatestActivity().getPlaygroundPart().getShortedPartName() + profile.getName() + 'A'))
//                .willReturn(true);
//
//        //when
//        soptampUserService.upsertSoptampUser(profile, userId);
//        String expectedNickname =
//                profile.getLatestActivity().getPlaygroundPart().getShortedPartName() + profile.getName() + 'B';
//
//        //then
//        ArgumentCaptor<SoptampUser> captor = ArgumentCaptor.forClass(SoptampUser.class);
//        verify(soptampUserRepository, times(3)).existsByNickname(anyString());
//        verify(soptampUserRepository, times(1)).save(captor.capture());
//        assertThat(captor.getValue().getUserId()).isEqualTo(userId);
//        assertThat(captor.getValue().getNickname()).isEqualTo(expectedNickname);
//        assertThat(captor.getValue().getPart().getPartName())
//                .isEqualTo(profile.getLatestActivity().getPlaygroundPart().getPartName());
//        assertThat(captor.getValue().getGeneration()).isEqualTo(profile.getLatestActivity().getGeneration());
//    }
//
//    @Test
//    void 기존_솝탬프_유저가_없다면_새로_생성_닉네임_중복시_suffix_추가_모든_suffix_사용시_에러() {
//        //given
//        Long userId = 1L;
//        PlaygroundProfile profile = PlaygroundProfile.builder()
//                .name("name")
//                .activities(List.of(new ActivityCardinalInfo("35,서버")))
//                .build();
//        given(soptampUserRepository.findByUserId(userId)).willReturn(Optional.empty());
//        given(soptampUserRepository.existsByNickname(anyString())).willReturn(true);
//
//        //when & then
//        assertThrows(BadRequestException.class, () -> soptampUserService.upsertSoptampUser(profile, userId));
//    }
//
//    @Test
//    void 기존_솝탬프_유저가_있고_기수가_변경되었다면_업데이트() {
//        //given
//        Long userId = 1L;
//        PlaygroundProfile profile = PlaygroundProfile.builder()
//                .name("name")
//                .activities(List.of(new ActivityCardinalInfo("36,아요"))) // 기수와 파트가 변경됨
//                .build();
//        SoptampUser existingUser = mock(SoptampUser.class);
//        given(soptampUserRepository.findByUserId(anyLong())).willReturn(Optional.of(existingUser));
//
//        //when
//        soptampUserService.upsertSoptampUser(profile, userId);
//
//        //then
//        verify(existingUser, times(1)).updateChangedGenerationInfo(anyLong(), any(), anyString());
//    }
//
//    @Test
//    void 기존_솝탬프_유저가_있고_기수가_변경되지_않았다면_변경하지_않음() {
//        //given
//        SoptampUser existingUser = mock(SoptampUser.class);
//        Long userId = 1L;
//        PlaygroundProfile profile = PlaygroundProfile.builder()
//                .name("name")
//                .activities(List.of(new ActivityCardinalInfo("36,아요")))
//                .build();
//        given(soptampUserRepository.findByUserId(userId)).willReturn(Optional.of(existingUser));
//        given(existingUser.getGeneration()).willReturn(36L);
//
//        //when
//        soptampUserService.upsertSoptampUser(profile, userId);
//
//        //then
//        verify(soptampUserRepository, times(1)).findByUserId(userId);
//        verify(existingUser, never()).updateChangedGenerationInfo(anyLong(), any(), anyString());
//        verify(soptampUserRepository, never()).save(any(SoptampUser.class));
//    }
//
    @Test
    @DisplayName("SUCCESS_미션 레벨에 맞게 유저의 포인트가 증가함")
    void SUCCESS_addPointByLevel() {
        //given
        final Long stampId = 10L;
        final Long userId = 1L;
        final Long initialTotalPoints = 100L;

        final Integer level = 2;

        final SoptampUser soptampUser = SoptampUserFixture.getSoptampUserWithTotalPoint(stampId, userId, initialTotalPoints);
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
//
//    @Test
//    @DisplayName("SUCCESS_미션 레벨별로 유저의 포인트 감소")
//    void SUCCESS_subtractPointByLevel() {
//        //given
//        final Long anyUserId = anyLong();
//        final Integer level = 1;
//        final Long soptampUserTotalPoints = 100L;
//        final SoptampUser oldSoptampUser = SoptampUser.builder()
//                .userId(anyUserId)
//                .totalPoints(soptampUserTotalPoints)
//                .build();
//
//        //when
//        when(soptampUserRepository.findByUserId(anyUserId)).thenReturn(Optional.of(oldSoptampUser));
//
//        //then
//        assertDoesNotThrow(()-> soptampUserService.subtractPointByLevel(anyUserId, level));
//    }
//
//    @Test
//    @DisplayName("FAIL_유저를 찾지 못하면 BadRequestException 발생")
//    void FAIL_subtractPointByLevel() {
//        //given
//        final Long anyUserId = anyLong();
//
//        //when
//        when(soptampUserRepository.findByUserId(anyUserId)).thenReturn(Optional.empty());
//
//        //then
//        assertThrows(BadRequestException.class, () -> {
//            soptampUserService.subtractPointByLevel(anyUserId, 1);
//        });
//    }
//
//    @Test
//    @DisplayName("SUCCESS_포인트 초기화")
//    void SUCCESS_initPoint() {
//        //given
//        final Long anyUserId = anyLong();
//        final SoptampUser soptampUser = SoptampUser.builder()
//                .userId(anyUserId)
//                .build();
//
//        //when
//        when(soptampUserRepository.findByUserId(anyUserId)).thenReturn(Optional.of(soptampUser));
//        when(soptampUserRepository.save(any(SoptampUser.class))).thenReturn(soptampUser);
//
//        //then
//        assertDoesNotThrow(() -> {
//            soptampUserService.initPoint(anyUserId);
//        });
//    }
//
//    @Test
//    @DisplayName("FAIL_포인트 초기화")
//    void FAIL_initPoint() {
//        //given
//        final Long anyUserId = anyLong();
//
//        //when
//        when(soptampUserRepository.findByUserId(anyUserId)).thenReturn(Optional.empty());
//
//        //then
//        assertThrows(BadRequestException.class, () -> soptampUserService.initPoint(anyUserId));
//    }

}