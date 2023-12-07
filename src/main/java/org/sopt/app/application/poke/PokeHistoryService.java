package org.sopt.app.application.poke;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.domain.entity.PokeHistory;
import org.sopt.app.interfaces.postgres.PokeHistoryRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PokeHistoryService {

    private final PokeHistoryRepository pokeHistoryRepository;

    public Boolean isNewPoker(Long userId) {
        return pokeHistoryRepository.findAllByPokerId(userId).isEmpty();
    }

    public Boolean isUserPokeBeforeFriend(Long userId, Long friendId) {
        return pokeHistoryRepository.findAllByPokerIdAndPokedId(userId, friendId).isEmpty();
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
}
