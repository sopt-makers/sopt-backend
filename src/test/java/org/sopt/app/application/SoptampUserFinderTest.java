package org.sopt.app.application;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.sopt.app.application.soptamp.SoptampUserFinder;
import org.sopt.app.application.soptamp.SoptampUserInfo;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.domain.entity.soptamp.SoptampUser;
import org.sopt.app.interfaces.postgres.SoptampUserRepository;

@ExtendWith(MockitoExtension.class)
class SoptampUserFinderTest {

    @Mock
    private SoptampUserRepository soptampUserRepository;

    @InjectMocks
    private SoptampUserFinder soptampUserFinder;

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
        assertThat(soptampUserFinder.findByNickname(anyNickname)).usingRecursiveComparison().isEqualTo(expected);
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
            soptampUserFinder.findByNickname(anyNickname);
        });
    }


}
