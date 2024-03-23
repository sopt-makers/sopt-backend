package org.sopt.app.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

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
                .nickname(generateNickname(newNickname))
                .build();

        //when
        Mockito.when(soptampUserRepository.findByUserId(anyUserId)).thenReturn(Optional.empty());
        Mockito.when(soptampUserRepository.save(any(SoptampUser.class))).thenReturn(soptampUser);

        //then
        Assertions.assertEquals(soptampUserService.updateSoptampUser(newNickname, anyUserId), id);
    }

    private String generateNickname(String username) {
        return username + Math.round(Math.random() * 10000);
    }

    /* TODO: Implement test cases
    @Test
    void findRanks() {
    }

    @Test
    void findCurrentRanks() {
    }

    @Test
    void findRankByNickname() {
    }

    @Test
    void addPoint() {
    }

    @Test
    void subtractPoint() {
    }

    @Test
    void findByNickname() {
    }

    @Test
    void initPoint() {
    }

    */
}