package org.sopt.app.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.slack.SlackService;
import org.sopt.app.application.soptamp.SoptampPointInfo.Main;
import org.sopt.app.application.soptamp.SoptampPointInfo.Point;
import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.domain.entity.SoptampUser;
import org.sopt.app.interfaces.postgres.SoptampUserRepository;

@ExtendWith(MockitoExtension.class)
class SoptampUserServiceTest {

    @Mock
    private SoptampUserRepository soptampUserRepository;

    @Mock
    private SlackService slackService;

    @InjectMocks
    private SoptampUserService soptampUserService;

    @Test
    @DisplayName("SUCCESS_솝탬프 유저 정보 조회")
    void SUCCESS_getSoptampUserInfo() {
        //given
        final Long id = 1L;
        final Long anyUserId = anyLong();
        final String profileMessage = "profileMessage";
        final Long totalPoints = 100L;
        final String nickname = "nickname";

        Optional<SoptampUser> soptampUser = Optional.of(SoptampUser.builder()
                .id(id)
                .userId(anyUserId)
                .profileMessage(profileMessage)
                .totalPoints(totalPoints)
                .nickname(nickname)
                .build());

        //when
        SoptampUserInfo.SoptampUser expected = SoptampUserInfo.SoptampUser.builder()
                .id(id)
                .userId(anyUserId)
                .profileMessage(profileMessage)
                .totalPoints(totalPoints)
                .nickname(nickname)
                .build();

        Mockito.when(soptampUserRepository.findByUserId(anyUserId)).thenReturn(soptampUser);
        SoptampUserInfo.SoptampUser result = soptampUserService.getSoptampUserInfo(anyUserId);
        //then

        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("FAIL_솝탬프 유저 정보 조회")
    void FAIL_getSoptampUserInfo() {
        //given
        final Long anyUserId = anyLong();

        //when
        Mockito.when(soptampUserRepository.findByUserId(anyUserId)).thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(BadRequestException.class, () -> {
            soptampUserService.getSoptampUserInfo(anyUserId);
        });
    }

    @Test
    @DisplayName("SUCCESS_닉네임 중복 검사")
    void SUCCESS_checkUserNickname() {
        //given
        final String anyNickname = anyString();

        //when
        Mockito.when(soptampUserRepository.findUserByNickname(anyNickname)).thenReturn(Optional.empty());

        //then
        Assertions.assertDoesNotThrow(() -> {
            soptampUserService.checkUserNickname(anyNickname);
        });
    }

    @Test
    @DisplayName("FAIL_닉네임 중복 검사")
    void FAIL_checkUserNickname() {
        //given
        final String anyNickname = anyString();

        //when
        Mockito.when(soptampUserRepository.findUserByNickname(anyNickname)).thenReturn(Optional.of(new SoptampUser()));

        //then
        Assertions.assertThrows(BadRequestException.class, () -> {
            soptampUserService.checkUserNickname(anyNickname);
        });
    }

    @Test
    @DisplayName("SUCCESS_닉네임 변경")
    void SUCCESS_editNickname() {
        //given
        final String newNickname = "newNickname";
        SoptampUserInfo.SoptampUser soptampUser = SoptampUserInfo.SoptampUser.builder()
                .nickname("oldNickname")
                .build();

        //then
        Assertions.assertEquals(soptampUserService.editNickname(soptampUser, newNickname).getNickname(), newNickname);
    }

    @Test
    @DisplayName("SUCCESS_프로필 메시지 변경")
    void SUCCESS_editProfileMessage() {
        //given
        final String newProfileMessage = "newProfileMessage";
        SoptampUserInfo.SoptampUser soptampUser = SoptampUserInfo.SoptampUser.builder()
                .nickname("oldProfileMessage")
                .build();

        //then
        Assertions.assertEquals(
                soptampUserService.editProfileMessage(soptampUser, newProfileMessage).getProfileMessage(),
                newProfileMessage);
    }

    @Test
    @DisplayName("SUCCESS_등록된 유저이면 이름을 변경하지 않음")
    void SUCCESS_updateSoptampUser() {
        //given
        final Long id = 1L;
        final Long anyUserId = anyLong();
        final String newNickname = "newNickname";
        SoptampUser soptampUser = SoptampUser.builder()
                .id(id)
                .userId(anyUserId)
                .nickname("oldNickName")
                .build();

        //when
        Mockito.when(soptampUserRepository.findByUserId(anyUserId)).thenReturn(Optional.of(soptampUser));

        //then
        Assertions.assertEquals(soptampUserService.updateSoptampUser(newNickname, anyUserId), id);
    }

    @Test
    @DisplayName("SUCCESS_등록된 유저가 아니면 이름을 생성하여 변경")
    void FAIL_updateSoptampUser() {
        //given
        final Long id = 1L;
        final Long anyUserId = anyLong();
        final String newNickname = "newNickname";
        SoptampUser soptampUser = SoptampUser.builder()
                .id(id)
                .userId(anyUserId)
                .nickname(newNickname)
                .build();

        //when
        Mockito.when(soptampUserRepository.findByUserId(anyUserId)).thenReturn(Optional.empty());
        Mockito.when(soptampUserRepository.save(any(SoptampUser.class))).thenReturn(soptampUser);

        //then
        Assertions.assertEquals(soptampUserService.updateSoptampUser(newNickname, anyUserId), id);
    }

    @Test
    @DisplayName("SUCCESS_솝탬프 유저 리스트를 받아 랭크 조회")
    void SUCCESS_findRanks() {
        //given
        final SoptampUser soptampUser1 = SoptampUser.builder().nickname("1stUser").totalPoints(100L).build();
        final SoptampUser soptampUser2 = SoptampUser.builder().nickname("2stUser").totalPoints(200L).build();
        final SoptampUser soptampUser3 = SoptampUser.builder().nickname("3stUser").totalPoints(300L).build();
        final List<SoptampUser> soptampUserList = List.of(soptampUser1, soptampUser2, soptampUser3);

        //when
        List<Main> expected = List.of(
                Main.builder().rank(1).point(300L).nickname("3stUser").build(),
                Main.builder().rank(2).point(200L).nickname("2stUser").build(),
                Main.builder().rank(3).point(100L).nickname("1stUser").build()
        );

        Mockito.when(soptampUserRepository.findAll()).thenReturn(soptampUserList);
        List<Main> result = soptampUserService.findRanks();

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("SUCCESS_솝탬프 포인트 리스트를 받아 랭크를 조회")
    void SUCCESS_findCurrentRanks() {
        //given
        List<Point> soptampPointList = List.of(
                Point.of(1L, 1L, 1L, 100L),
                Point.of(2L, 1L, 2L, 200L),
                Point.of(3L, 1L, 3L, 300L)
        );

        List<Long> soptampUserIdList = List.of(1L, 2L, 3L);

        //when
        List<Main> expected = List.of(
                Main.builder().rank(1).point(300L).build(),
                Main.builder().rank(2).point(200L).build(),
                Main.builder().rank(3).point(100L).build()
        );

        Mockito.when(soptampUserRepository.findAllById(soptampUserIdList)).thenReturn(
                List.of(
                        SoptampUser.builder().id(1L).build(),
                        SoptampUser.builder().id(2L).build(),
                        SoptampUser.builder().id(3L).build()
                ));
        List<Main> result = soptampUserService.findCurrentRanks(soptampPointList);

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("FAIL_솝탬프 포인트 리스트를 받았을 때 유저를 찾지 못하면 slack 메시지 전송 후 찾지 못한 유저를 제외한 리스트 반환")
    void FAIL_findCurrentRanks() {
        //given
        final List<Point> soptampPointList = List.of(
                Point.of(1L, 1L, 1L, 100L),
                Point.of(2L, 1L, 2L, 200L),
                Point.of(3L, 1L, 3L, 300L)
        );
        final List<Long> soptampUserIdList = List.of(1L, 2L, 3L);
        given(soptampUserRepository.findAllById(soptampUserIdList)).willReturn(
                List.of(
                        SoptampUser.builder().id(1L).build(),
                        SoptampUser.builder().id(2L).build()
                )); // 솝탬프 포인트 리스트와 다르게 3번 유저가 없음

        //when
        List<Main> expected = List.of(
                Main.builder().rank(1).point(200L).build(),
                Main.builder().rank(2).point(100L).build()
        );
        List<Main> result = soptampUserService.findCurrentRanks(soptampPointList);

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
        then(slackService).should().sendSlackMessage(anyString(), anyString()); // 슬랙 알림 메서드가 실행되는지 검증
    }


    @Test
    @DisplayName("SUCCESS_닉네임으로 랭킹 조회")
    void SUCCESS_findRankByNickname() {
        // given
        final String anyNickname = anyString();
        final SoptampUser soptampUser = SoptampUser.builder().nickname(anyNickname).build();

        //when
        Mockito.when(soptampUserRepository.findUserByNickname(anyNickname)).thenReturn(Optional.of(soptampUser));
        Optional<SoptampUser> result = soptampUserRepository.findUserByNickname(anyNickname);

        //then
        assertThat(result).usingRecursiveComparison().isEqualTo(Optional.of(soptampUser));
    }

    @Test
    @DisplayName("FAIL_닉네임으로 랭킹 조회")
    void FAIL_findRankByNickname() {
        // given
        final String anyNickname = anyString();

        //when
        Mockito.when(soptampUserRepository.findUserByNickname(anyNickname)).thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(BadRequestException.class, () -> {
            soptampUserService.findRankByNickname(anyNickname);
        });

    }

    @Test
    @DisplayName("SUCCESS_미션 레벨별로 유저의 포인트 추가")
    void SUCCESS_addPoint() {
        //given
        final Long anyUserId = anyLong();
        final Integer level = 1;
        final Long soptampUserTotalPoints = 100L;
        final SoptampUser oldSoptampUser = SoptampUser.builder()
                .userId(anyUserId)
                .totalPoints(soptampUserTotalPoints)
                .build();
        final SoptampUser newSoptampUser = SoptampUser.builder()
                .userId(anyUserId)
                .totalPoints(soptampUserTotalPoints + level)
                .build();

        //when
        SoptampUserInfo.SoptampUser result = SoptampUserInfo.SoptampUser.builder()
                .id(newSoptampUser.getId())
                .userId(newSoptampUser.getUserId())
                .profileMessage(newSoptampUser.getProfileMessage())
                .totalPoints(newSoptampUser.getTotalPoints())
                .nickname(newSoptampUser.getNickname())
                .build();

        Mockito.when(soptampUserRepository.findByUserId(anyUserId)).thenReturn(Optional.of(oldSoptampUser));
        Mockito.when(soptampUserRepository.save(any(SoptampUser.class))).thenReturn(newSoptampUser);

        //then
        assertThat(soptampUserService.addPoint(anyUserId, level)).usingRecursiveComparison().isEqualTo(result);
    }

    @Test
    @DisplayName("FAIL_유저를 찾지 못하면 BadRequestException 발생")
    void FAIL_addPoint() {
        //given
        final Long anyUserId = anyLong();

        //when
        Mockito.when(soptampUserRepository.findByUserId(anyUserId)).thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(BadRequestException.class, () -> {
            soptampUserService.addPoint(anyUserId, 1);
        });
    }

    @Test
    @DisplayName("SUCCESS_미션 레벨별로 유저의 포인트 감소")
    void SUCCESS_subtractPoint() {
        //given
        final Long anyUserId = anyLong();
        final Integer level = 1;
        final Long soptampUserTotalPoints = 100L;
        final SoptampUser oldSoptampUser = SoptampUser.builder()
                .userId(anyUserId)
                .totalPoints(soptampUserTotalPoints)
                .build();
        final SoptampUser newSoptampUser = SoptampUser.builder()
                .userId(anyUserId)
                .totalPoints(soptampUserTotalPoints - level)
                .build();

        //when
        SoptampUserInfo.SoptampUser result = SoptampUserInfo.SoptampUser.builder()
                .id(newSoptampUser.getId())
                .userId(newSoptampUser.getUserId())
                .profileMessage(newSoptampUser.getProfileMessage())
                .totalPoints(newSoptampUser.getTotalPoints())
                .nickname(newSoptampUser.getNickname())
                .build();

        Mockito.when(soptampUserRepository.findByUserId(anyUserId)).thenReturn(Optional.of(oldSoptampUser));
        Mockito.when(soptampUserRepository.save(any(SoptampUser.class))).thenReturn(newSoptampUser);

        //then
        assertThat(soptampUserService.subtractPoint(anyUserId, level)).usingRecursiveComparison().isEqualTo(result);
    }

    @Test
    @DisplayName("FAIL_유저를 찾지 못하면 BadRequestException 발생")
    void FAIL_subtractPoint() {
        //given
        final Long anyUserId = anyLong();

        //when
        Mockito.when(soptampUserRepository.findByUserId(anyUserId)).thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(BadRequestException.class, () -> {
            soptampUserService.subtractPoint(anyUserId, 1);
        });
    }

    @Test
    @DisplayName("SUCCESS_닉네임으로 유저 조회")
    void SUCCESS_findByNickname() {
        //given
        final Long id = 1L;
        final Long userId = 1L;
        final String profileMessage = "profileMessage";
        final Long totalPoints = 100L;
        final String anyNickname = anyString();

        final SoptampUser soptampUser = SoptampUser.builder()
                .id(id)
                .userId(userId)
                .profileMessage(profileMessage)
                .totalPoints(totalPoints)
                .nickname(anyNickname)
                .build();
        //when
        Mockito.when(soptampUserRepository.findUserByNickname(anyNickname)).thenReturn(Optional.of(soptampUser));
        SoptampUserInfo.SoptampUser expected = SoptampUserInfo.SoptampUser.builder()
                .id(id)
                .userId(userId)
                .profileMessage(profileMessage)
                .totalPoints(totalPoints)
                .nickname(anyNickname)
                .build();

        //then
        assertThat(soptampUserService.findByNickname(anyNickname)).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("FAIL_닉네임으로 유저 조회 실패시 BadRequestException 발생")
    void FAIL_findByNickname() {
        //given
        final String anyNickname = anyString();

        //when
        Mockito.when(soptampUserRepository.findUserByNickname(anyNickname)).thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(BadRequestException.class, () -> {
            soptampUserService.findByNickname(anyNickname);
        });
    }

    @Test
    @DisplayName("SUCCESS_포인트 초기화")
    void SUCCESS_initPoint() {
        //given
        final Long anyUserId = anyLong();
        final SoptampUser soptampUser = SoptampUser.builder()
                .userId(anyUserId)
                .build();

        //when
        Mockito.when(soptampUserRepository.findByUserId(anyUserId)).thenReturn(Optional.of(soptampUser));
        Mockito.when(soptampUserRepository.save(any(SoptampUser.class))).thenReturn(soptampUser);

        //then
        Assertions.assertDoesNotThrow(() -> {
            soptampUserService.initPoint(anyUserId);
        });
    }

    @Test
    @DisplayName("FAIL_포인트 초기화")
    void FAIL_initPoint() {
        //given
        final Long anyUserId = anyLong();

        //when
        Mockito.when(soptampUserRepository.findByUserId(anyUserId)).thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(BadRequestException.class, () -> {
            soptampUserService.initPoint(anyUserId);
        });
    }

}