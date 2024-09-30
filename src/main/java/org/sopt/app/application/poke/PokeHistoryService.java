package org.sopt.app.application.poke;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.application.poke.PokeInfo.PokeHistoryInfo;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.poke.PokeHistory;
import org.sopt.app.interfaces.postgres.PokeHistoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PokeHistoryService {

    private final PokeHistoryRepository pokeHistoryRepository;

    public List<PokeHistoryInfo> getAllOfPokeBetween(Long userId, Long friendId) {
        val pokeHistoryList = pokeHistoryRepository.findAllWithFriendOrderByCreatedAtDescIsReplyFalse(userId, friendId);

        return pokeHistoryList.stream()
                .map(PokeHistoryInfo::from)
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
        if (!pokeHistory.isEmpty()) {
            throw new BadRequestException(ErrorCode.DUPLICATE_POKE);
        }
    }

    public Map<Long, Boolean> getAllPokeHistoryMap(Long userId) {
        val pokeHistories = pokeHistoryRepository.findAllByPokerIdAndIsReply(userId, false);
        HashMap<Long, Boolean> pokeHistoryMap = new HashMap<>();
        for (PokeHistory pokeHistory : pokeHistories) {
            pokeHistoryMap.put(pokeHistory.getPokedId(), pokeHistory.getIsReply());
        }
        return pokeHistoryMap;
    }

    // isReply 여부에 관계 없이 전부 조회
    public List<PokeHistoryInfo> getAllPokeHistoryByUsers(Long userId, Long friendUserId) {
        val pokeHistories = pokeHistoryRepository.findAllPokeHistoryByUsers(userId, friendUserId);
        return pokeHistories.stream().map(PokeHistoryInfo::from).toList();
    }
}
