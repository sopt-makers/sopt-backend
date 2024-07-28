package org.sopt.app.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.sopt.app.common.fixtures.SoptampUserFixture.SOPTAMP_USER_SERVER_1;

import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.sopt.app.application.soptamp.SoptampUserService;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.domain.entity.SoptampUser;
import org.sopt.app.interfaces.postgres.SoptampUserRepository;

@ExtendWith(MockitoExtension.class)
class SoptampUserServiceTest {

    @Mock
    private SoptampUserRepository soptampUserRepository;

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
        SoptampUserInfo expected = SoptampUserInfo.builder()
                .id(id)
                .userId(anyUserId)
                .profileMessage(profileMessage)
                .totalPoints(totalPoints)
                .nickname(nickname)
                .build();

        Mockito.when(soptampUserRepository.findByUserId(anyUserId)).thenReturn(soptampUser);
        SoptampUserInfo result = soptampUserService.getSoptampUserInfo(anyUserId);
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
        SoptampUserInfo soptampUser = SoptampUserInfo.builder()
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
        final SoptampUser editedSoptampUser = SoptampUser.builder()
                .id(SOPTAMP_USER_SERVER_1.getId())
                .userId(SOPTAMP_USER_SERVER_1.getUserId())
                .nickname(SOPTAMP_USER_SERVER_1.getNickname())
                .totalPoints(SOPTAMP_USER_SERVER_1.getTotalPoints())
                .profileMessage(newProfileMessage)
                .build();
        final SoptampUserInfo editedSoptampUserInfo = SoptampUserInfo.builder()
                .id(SOPTAMP_USER_SERVER_1.getId())
                .userId(SOPTAMP_USER_SERVER_1.getUserId())
                .nickname(SOPTAMP_USER_SERVER_1.getNickname())
                .totalPoints(SOPTAMP_USER_SERVER_1.getTotalPoints())
                .profileMessage(newProfileMessage)
                .build();
        given(soptampUserRepository.save(any(SoptampUser.class))).willReturn(editedSoptampUser);

        // when
        String result = soptampUserService.editProfileMessage(editedSoptampUserInfo, newProfileMessage)
                .getProfileMessage();

        //then
        Assertions.assertEquals(newProfileMessage, result);
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
    @DisplayName("SUCCESS_닉네임으로 랭킹 조회")
    void SUCCESS_findSoptampUserByNickname() {
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
    void FAIL_findSoptampUserByNickname() {
        // given
        final String anyNickname = anyString();

        //when
        Mockito.when(soptampUserRepository.findUserByNickname(anyNickname)).thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(BadRequestException.class, () -> {
            soptampUserService.findSoptampUserByNickname(anyNickname);
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
        SoptampUserInfo result = SoptampUserInfo.builder()
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
        SoptampUserInfo result = SoptampUserInfo.builder()
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
        SoptampUserInfo expected = SoptampUserInfo.builder()
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