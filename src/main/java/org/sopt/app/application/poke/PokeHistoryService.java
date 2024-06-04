package org.sopt.app.application.poke;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.poke.PokeInfo.PokeHistoryInfo;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.PokeHistory;
import org.sopt.app.interfaces.postgres.PokeHistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PokeHistoryService {

    private final PokeHistoryRepository pokeHistoryRepository;

    public List<PokeHistoryInfo> getAllOfPokeBetween(Long userId, Long friendId) {
        val pokeHistoryList = pokeHistoryRepository.findAllWithFriendOrderByCreatedAtDesc(userId, friendId);

        return pokeHistoryList.stream()
                .map(pokeHistory -> PokeHistoryInfo.from(pokeHistory))
                .sorted(Comparator.comparing(PokeHistoryInfo::getCreatedAt).reversed())
                .toList();
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
        return pokeHistoryRepository.findAllByIdIsInOrderByCreatedAtDesc(targetHistoryIds, pageable);
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
