package org.sopt.app.application.soptamp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;

import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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

    /* TODO: Implement test cases
    @Test
    void checkUserNickname() {
    }

    @Test
    void editNickname() {
    }

    @Test
    void editProfileMessage() {
    }

    @Test
    void updateSoptampUser() {
    }

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