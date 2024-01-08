package org.sopt.app.application.poke;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.domain.entity.PokeHistory;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.interfaces.postgres.PokeHistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PokeHistoryService {

    private final PokeHistoryRepository pokeHistoryRepository;

    public Boolean isNewPoker(Long userId) {
        return pokeHistoryRepository.findAllByPokerId(userId).isEmpty();
    }

    public List<PokeHistory> getAllOfPokeBetween(Long userId, Long friendId) {
        return pokeHistoryRepository.findAllWithFriendOrderByCreatedAtDesc(userId, friendId);
    }

    public List<Long> getPokedFriendIds(Long userId) {
        val friends = pokeHistoryRepository.findAllByPokerIdAndIsReply(userId, false);
        return friends.stream()
                .map(PokeHistory::getPokedId)
                .toList();
    }

    public List<Long> getPokeFriendIds(Long userId) {
        val friends = pokeHistoryRepository.findAllByPokedIdAndIsReply(userId, false);
        return friends.stream()
                .map(PokeHistory::getPokerId)
                .toList();
    }

    public List<Long> getPokeMeUserIds(Long userId) {
        return pokeHistoryRepository.findAllByPokedId(userId).stream()
                .map(PokeHistory::getPokerId)
                .distinct()
                .toList();
    }

    public List<PokeHistory> getAllLatestPokeHistoryFromTo(Long pokerId, Long pokedId) {
        return pokeHistoryRepository.findAllByPokerIdAndPokedIdOrderByCreatedAtDesc(pokerId, pokedId);
    }

    public Page<PokeHistory> getAllLatestPokeHistoryIn(List<Long> targetHistoryIds, Pageable pageable) {
        return pokeHistoryRepository.findAllByIdIsInOrderByCreatedAt(targetHistoryIds, pageable);
    }

    public void checkUserOverDailyPokeLimit(Long userId) {
        LocalDateTime startDatetime = LocalDateTime.of(LocalDate.now(), LocalTime.of(0,0,0));
        LocalDateTime endDatetime = LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59));
        List<PokeHistory> allByPokerIdAndCreatedAtDate = pokeHistoryRepository.findAllByPokerIdAndCreatedAtBetween(userId, startDatetime, endDatetime);
        // TODO: Prod 배포 이전에 10 으로 변경
        if (allByPokerIdAndCreatedAtDate.size() >= 100) {
            throw new BadRequestException(ErrorCode.OVER_DAILY_POKE_LIMIT.getMessage());
        }
    }

    public void checkDuplicate(Long pokerUserId, Long pokedUserId) {
        val pokeHistory = pokeHistoryRepository.findAllByPokerIdAndPokedIdAndIsReplyIsFalse(pokerUserId, pokedUserId);
        if (pokeHistory.size() >= 1) {
            throw new BadRequestException(ErrorCode.DUPLICATE_POKE.getMessage());
        }
    }

    public HashMap<Long, Boolean> getAllPokeHistoryMap(Long userId) {
        val pokeHistories = pokeHistoryRepository.findAllByPokerIdAndIsReply(userId, false);
        HashMap<Long, Boolean> pokeHistoryMap = new HashMap<>();
        for (PokeHistory pokeHistory : pokeHistories) {
            pokeHistoryMap.put(pokeHistory.getPokedId(), pokeHistory.getIsReply());
        }
        return pokeHistoryMap;
    }
}
