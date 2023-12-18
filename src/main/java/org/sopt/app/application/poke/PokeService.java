package org.sopt.app.application.poke;

import lombok.RequiredArgsConstructor;
import org.sopt.app.common.event.Events;
import org.sopt.app.common.exception.NotFoundException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.PokeHistory;
import org.sopt.app.domain.entity.User;
import org.sopt.app.interfaces.postgres.PokeHistoryRepository;
import org.sopt.app.interfaces.postgres.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PokeService {

    private final UserRepository userRepository;
    private final PokeHistoryRepository historyRepository;

    @Transactional(readOnly = true)
    public PokeInfo.PokeDetail getPokeDetail(Long pokerId, Long pokedId) {
        Optional<PokeHistory> latestPokeHistory = historyRepository.findAllByPokerIdAndPokedIdOrderByCreatedAtDesc(pokerId, pokedId).stream()
                .findFirst();
        return latestPokeHistory.map(history -> PokeInfo.PokeDetail.builder()
                .id(history.getId())
                .message(history.getMessage())
                .isReply(history.getIsReply())
                .build())
                .orElse(null);
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
        Optional<PokeHistory> latestPokeFromPokedIsReplyFalse
                = historyRepository.findByPokerIdAndPokedIdAndIsReplyIsFalse(pokedUserId, pokerUserId);
        if (latestPokeFromPokedIsReplyFalse.isPresent()) {
            currentPokeReply = true;
            latestPokeFromPokedIsReplyFalse.get().activateReply();
        }
        PokeHistory createdPoke = PokeHistory.builder()
                .pokerId(pokerUserId)
                .pokedId(pokedUserId)
                .message(pokeMessage)
                .isReply(currentPokeReply)
                .build();
        historyRepository.save(createdPoke);
    }



}
