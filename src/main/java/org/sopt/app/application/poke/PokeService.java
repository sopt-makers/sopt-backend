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

import java.util.List;


@Service
@RequiredArgsConstructor
public class PokeService {

    private final UserRepository userRepository;
    private final PokeHistoryRepository historyRepository;

    @Transactional(readOnly = true)
    public PokeInfo.PokeDetail getPokeDetail(Long pokeHistoryId) {
        PokeHistory latestPokeHistory = historyRepository.findById(pokeHistoryId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POKE_HISTORY_NOT_FOUND.getMessage()));
        return PokeInfo.PokeDetail.builder()
                .id(latestPokeHistory.getId())
                .pokerId(latestPokeHistory.getPokerId())
                .pokedId(latestPokeHistory.getPokedId())
                .message(latestPokeHistory.getMessage())
                .build();
    }

    @Transactional
    public PokeHistory poke(Long pokerUserId, Long pokedUserId, String pokeMessage) {
        User pokedUser = userRepository.findUserById(pokedUserId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()));

        PokeHistory pokeByApplyingReply = createPokeByApplyingReply(pokerUserId, pokedUserId, pokeMessage);

        Events.raise(PokeEvent.of(pokedUser.getPlaygroundId()));
        return pokeByApplyingReply;
    }

    private PokeHistory createPokeByApplyingReply(Long pokerUserId, Long pokedUserId, String pokeMessage) {
        boolean currentPokeReply = false;
        List<PokeHistory> latestPokeFromPokedIsReplyFalse
                = historyRepository.findAllByPokerIdAndPokedIdAndIsReplyIsFalse(pokedUserId, pokerUserId);
        if (!latestPokeFromPokedIsReplyFalse.isEmpty()){
            latestPokeFromPokedIsReplyFalse.get(0).activateReply();
        }
        PokeHistory createdPoke = PokeHistory.builder()
                .pokerId(pokerUserId)
                .pokedId(pokedUserId)
                .message(pokeMessage)
                .isReply(currentPokeReply)
                .build();
        return historyRepository.save(createdPoke);
    }
}
