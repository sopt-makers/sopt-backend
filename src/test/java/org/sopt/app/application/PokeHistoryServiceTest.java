package org.sopt.app.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sopt.app.application.poke.PokeHistoryService;
import org.sopt.app.application.poke.PokeInfo.PokeHistoryInfo;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.domain.entity.PokeHistory;
import org.sopt.app.interfaces.postgres.PokeHistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class PokeHistoryServiceTest {

    @Mock
    private PokeHistoryRepository pokeHistoryRepository;

    @InjectMocks
    private PokeHistoryService pokeHistoryService;

    @Test
    @DisplayName("SUCCESS_찌르기 전체 조회")
    void SUCCESS_getAllOfPokeBetween() {
        PokeHistory pokeHistory = PokeHistory.builder().id(1L).pokerId(1L).pokedId(2L).message("message").isReply(true)
                .build();

        when(pokeHistoryRepository.findAllWithFriendOrderByCreatedAtDesc(any(), any())).thenReturn(
                List.of(pokeHistory));

        List<PokeHistoryInfo> result = pokeHistoryService.getAllOfPokeBetween(1L, 2L);
        assertEquals(pokeHistory.getId(), result.get(0).getId());
    }

    @Test
    @DisplayName("SUCCESS_내가 찌르고 답장 없는 유저 리스트 조회")
    void SUCCESS_getPokedFriendIds() {
        PokeHistory pokeHistory = PokeHistory.builder().id(1L).pokerId(1L).pokedId(2L).message("message").isReply(false)
                .build();

        when(pokeHistoryRepository.findAllByPokerIdAndIsReply(any(), anyBoolean())).thenReturn(List.of(pokeHistory));

        List<Long> result = pokeHistoryService.getPokedFriendIds(1L);
        assertEquals(2L, result.get(0));
    }

    @Test
    @DisplayName("SUCCESS_내가 찔렸고 답장 없는 유저 리스트 조회")
    void SUCCESS_getPokeFriendIds() {
        PokeHistory pokeHistory = PokeHistory.builder().id(1L).pokerId(2L).pokedId(1L).message("message").isReply(false)
                .build();

        when(pokeHistoryRepository.findAllByPokedIdAndIsReply(any(), anyBoolean())).thenReturn(List.of(pokeHistory));

        List<Long> result = pokeHistoryService.getPokeFriendIds(1L);
        assertEquals(2L, result.get(0));
    }

    @Test
    @DisplayName("SUCCESS_나를 찌른 유저 리스트 조회")
    void SUCCESS_getPokeMeUserIds() {
        PokeHistory pokeHistory = PokeHistory.builder().id(1L).pokerId(2L).pokedId(1L).message("message").isReply(false)
                .build();

        when(pokeHistoryRepository.findAllByPokedId(any())).thenReturn(List.of(pokeHistory));

        List<Long> result = pokeHistoryService.getPokeMeUserIds(1L);
        assertEquals(2L, result.get(0));
    }

    @Test
    @DisplayName("SUCCESS_찌른 사람, 찔린 사람 아이디로 최신 콕찌르기 조회")
    void SUCCESS_getAllLatestPokeHistoryFromTo() {
        PokeHistory pokeHistory = PokeHistory.builder().id(1L).pokerId(2L).pokedId(1L).message("message").isReply(false)
                .build();

        when(pokeHistoryRepository.findAllByPokerIdAndPokedIdOrderByCreatedAtDesc(any(), any())).thenReturn(
                List.of(pokeHistory));

        List<PokeHistory> result = pokeHistoryService.getAllLatestPokeHistoryFromTo(1L, 2L);
        assertEquals(pokeHistory, result.get(0));
    }

    @Test
    @DisplayName("SUCCESS_콕찌르기 히스토리 아이디로 최신 콕찌르기 조회")
    void SUCCESS_getAllLatestPokeHistoryIn() {
        PokeHistory pokeHistory = PokeHistory.builder().id(1L).pokerId(2L).pokedId(1L).message("message").isReply(false)
                .build();
        Page pokeHistoryPage = new PageImpl<>(List.of(pokeHistory));
        Pageable pageable = Pageable.ofSize(1);

        when(pokeHistoryRepository.findAllByIdIsInOrderByCreatedAtDesc(any(), any())).thenReturn(pokeHistoryPage);

        Page<PokeHistory> result = pokeHistoryService.getAllLatestPokeHistoryIn(List.of(1L), pageable);
        assertEquals(pokeHistoryPage.getTotalPages(), result.getTotalPages());
        assertEquals(pokeHistoryPage.getSize(), result.getSize());
    }

    @Test
    @DisplayName("SUCCESS_콕찌르기 전체 조회")
    void SUCCESS_getAllPokeHistoryMap() {
        PokeHistory pokeHistory = PokeHistory.builder().id(1L).pokerId(2L).pokedId(1L).message("message").isReply(false)
                .build();
        HashMap<Long, Boolean> pokeHistoryMap = new HashMap<>();
        pokeHistoryMap.put(1L, false);

        when(pokeHistoryRepository.findAllByPokerIdAndIsReply(1L, false)).thenReturn(List.of(pokeHistory));

        Map<Long, Boolean> result = pokeHistoryService.getAllPokeHistoryMap(1L);
        assertEquals(pokeHistoryMap, result);
    }

    @Test
    @DisplayName("SUCCESS_찌르기 중복 체크")
    void SUCCESS_checkDuplicate() {
        when(pokeHistoryRepository.findAllByPokerIdAndPokedIdAndIsReplyIsFalse(any(), any())).thenReturn(
                List.of());

        pokeHistoryService.checkDuplicate(1L, 2L);
    }

    @Test
    @DisplayName("FAIL_찌르기 중복 시 BadRequestException")
    void FAIL_checkDuplicateBadRequestException() {
        PokeHistory pokeHistory = PokeHistory.builder().id(1L).pokerId(2L).pokedId(1L).message("message").isReply(false)
                .build();

        when(pokeHistoryRepository.findAllByPokerIdAndPokedIdAndIsReplyIsFalse(any(), any())).thenReturn(
                List.of(pokeHistory, pokeHistory, pokeHistory, pokeHistory, pokeHistory, pokeHistory, pokeHistory,
                        pokeHistory, pokeHistory, pokeHistory, pokeHistory));

        assertThrows(BadRequestException.class, () -> pokeHistoryService.checkDuplicate(1L, 2L));
    }
}
