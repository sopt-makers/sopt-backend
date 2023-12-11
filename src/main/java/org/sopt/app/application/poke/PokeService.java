package org.sopt.app.application.poke;

import lombok.RequiredArgsConstructor;
import org.sopt.app.common.event.Events;
import org.sopt.app.common.exception.NotFoundException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.Friend;
import org.sopt.app.domain.entity.PokeHistory;
import org.sopt.app.domain.entity.User;
import org.sopt.app.interfaces.postgres.FriendRepository;
import org.sopt.app.interfaces.postgres.PokeHistoryRepository;
import org.sopt.app.interfaces.postgres.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PokeService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final PokeHistoryRepository historyRepository;

    @Transactional(readOnly = true)
    public Boolean isReplyPoke(Long pokerId, Long pokedId) {
        Optional<PokeHistory> latestPokeFromPokedIsReplyFalse = historyRepository.findByPokerIdAndPokedIdAndIsReplyIsFalse(pokedId, pokerId);
        return latestPokeFromPokedIsReplyFalse.isPresent();
    }

    @Transactional(readOnly = true)
    public PokeInfo.PokeDetail getPokeDetail(Long pokerId, Long pokedId) {
        PokeHistory latestPokeHistory = historyRepository.findAllByPokerIdAndPokedIdOrderByCreatedAtDesc(pokerId, pokedId).stream()
                .distinct()
                .findFirst()
                .orElseThrow(() -> new NotFoundException(ErrorCode.POKE_HISTORY_NOT_FOUND.getMessage()));
        return PokeInfo.PokeDetail.builder()
                .id(latestPokeHistory.getId())
                .message(latestPokeHistory.getMessage())
                .isReply(latestPokeHistory.getIsReply())
                .build();
    }

    @Transactional
    public void poke(Long pokerUserId, Long pokedUserId, String pokeMessage) {
        User pokedUser = userRepository.findUserById(pokedUserId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()));

        createPokeByApplyingReply(pokerUserId, pokedUserId, pokeMessage);

        Events.raise(PokeEvent.of(pokedUser.getPlaygroundId()));
    }

    private void createPokeByApplyingReply(Long pokerUserId, Long pokedUserId, String pokeMessage) {
        boolean currentPokeReply = false;
        if (isReplyPoke(pokerUserId, pokedUserId)) {
            currentPokeReply = true;
            PokeHistory recentPokeFromPokedUser = historyRepository.findByPokerIdAndPokedIdAndIsReplyIsFalse(pokerUserId, pokedUserId).get();
            recentPokeFromPokedUser.activateReply();
            applyPokeCountBoth(pokerUserId, pokedUserId);
        }
        PokeHistory createdPoke = PokeHistory.builder()
                .pokerId(pokerUserId)
                .pokedId(pokedUserId)
                .message(pokeMessage)
                .isReply(currentPokeReply)
                .build();
        historyRepository.save(createdPoke);
    }

    private void applyPokeCountBoth(Long pokerId, Long pokedId) {
        Friend pokerToPoked = friendRepository.findByUserIdAndFriendUserId(pokerId, pokedId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.FRIENDSHIP_NOT_FOUND.getMessage()));
        Friend pokedToPoker = friendRepository.findByUserIdAndFriendUserId(pokerId, pokedId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.FRIENDSHIP_NOT_FOUND.getMessage()));
        pokerToPoked.addPokeCount();
        pokedToPoker.addPokeCount();
    }

}
