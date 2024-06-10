package org.sopt.app.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.poke.PokeInfo.PokeDetail;
import org.sopt.app.application.poke.PokeService;
import org.sopt.app.common.exception.NotFoundException;
import org.sopt.app.domain.entity.PokeHistory;
import org.sopt.app.domain.entity.User;
import org.sopt.app.interfaces.postgres.PokeHistoryRepository;
import org.sopt.app.interfaces.postgres.UserRepository;

@ExtendWith(MockitoExtension.class)
class PokeServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PokeHistoryRepository pokeHistoryRepository;

    @InjectMocks
    private PokeService pokeService;


    @Test
    @DisplayName("SUCCESS_찌르기 디테일 조회, 있을 때")
    void SUCCESS_getPokeDetail() {
        PokeDetail pokeDetail = PokeDetail.builder().id(1L).pokerId(1L).pokedId(2L).message("message").build();
        PokeHistory pokeHistory = PokeHistory.builder().id(1L).pokerId(1L).pokedId(2L).message("message").isReply(true)
                .build();

        when(pokeHistoryRepository.findById(any())).thenReturn(Optional.of(pokeHistory));

        PokeDetail result = pokeService.getPokeDetail(1L);
        assertEquals(pokeDetail.getId(), result.getId());
    }

    @Test
    @DisplayName("FAIL_찌르기 디테일 조회, 없을 때 NotFoundException 발생")
    void FAIL_getPokeDetailNotFoundException() {
        when(pokeHistoryRepository.findById(any())).thenReturn(Optional.ofNullable(null));

        assertThrows(NotFoundException.class, () -> pokeService.getPokeDetail(1L));
    }

    @Test
    @DisplayName("SUCCESS_찌르기")
    void SUCCESS_pokeWithUser() {
        User user = User.builder().id(1L).build();
        List<PokeHistory> pokeHistoryList = List.of(
                PokeHistory.builder().id(1L).pokedId(1L).pokerId(2L).isReply(false).build());

        when(userRepository.findUserById(any())).thenReturn(Optional.of(user));
        when(pokeHistoryRepository.findAllByPokerIdAndPokedIdAndIsReplyIsFalse(1L, 2L)).thenReturn(pokeHistoryList);
        when(pokeHistoryRepository.save(any())).thenReturn(pokeHistoryList.get(0));

        PokeHistory result = pokeService.poke(2L, 1L, "message", false);
        assertEquals(pokeHistoryList.get(0).getId(), result.getId());
    }

    @Test
    @DisplayName("SUCCESS_찌르기, empty")
    void SUCCESS_pokeWithUserEmpty() {
        User user = User.builder().id(1L).build();

        when(userRepository.findUserById(any())).thenReturn(Optional.of(user));
        when(pokeHistoryRepository.findAllByPokerIdAndPokedIdAndIsReplyIsFalse(1L, 2L)).thenReturn(List.of());

        PokeHistory result = pokeService.poke(2L, 1L, "message", false);
        assertNull(result);
    }

    @Test
    @DisplayName("FAIL_찌르기 유저 없을 때 NotFoundException 발생")
    void FAIL_pokeWithoutUserNotFoundException() {
        when(userRepository.findUserById(any())).thenReturn(Optional.ofNullable(null));

        assertThrows(NotFoundException.class, () -> pokeService.poke(2L, 1L, "message", false));
    }
}
