package org.sopt.app.application.poke;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.app.common.event.EventPublisher;
import org.sopt.app.common.exception.NotFoundException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.poke.PokeHistory;
import org.sopt.app.domain.entity.User;
import org.sopt.app.interfaces.postgres.PokeHistoryRepository;
import org.sopt.app.interfaces.postgres.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PokeService {

    private final UserRepository userRepository;
    private final PokeHistoryRepository historyRepository;
    private final EventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public PokeInfo.PokeDetail getPokeDetail(Long pokeHistoryId) {
        PokeHistory latestPokeHistory = historyRepository.findById(pokeHistoryId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POKE_HISTORY_NOT_FOUND));
        return PokeInfo.PokeDetail.builder()
                .id(latestPokeHistory.getId())
                .pokerId(latestPokeHistory.getPokerId())
                .pokedId(latestPokeHistory.getPokedId())
                .message(latestPokeHistory.getMessage())
                .build();
    }

    @Transactional
    public PokeHistory poke(Long pokerUserId, Long pokedUserId, String pokeMessage, Boolean isAnonymous) {
        User pokedUser = userRepository.findUserById(pokedUserId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        PokeHistory pokeByApplyingReply = createPokeByApplyingReply(pokerUserId, pokedUserId, pokeMessage, isAnonymous);

        eventPublisher.raise(PokeEvent.of(pokedUser.getPlaygroundId()));
        return pokeByApplyingReply;
    }

    private PokeHistory createPokeByApplyingReply(
            Long pokerUserId, Long pokedUserId, String pokeMessage, Boolean isAnonymous
    ) {
        List<PokeHistory> latestPokeFromPokedIsReplyFalse = historyRepository.findAllByPokerIdAndPokedIdAndIsReplyIsFalse(
                pokedUserId, pokerUserId
        );

        if (!latestPokeFromPokedIsReplyFalse.isEmpty()) {
            latestPokeFromPokedIsReplyFalse.getFirst().activateReply();
        }
        return historyRepository.save(PokeHistory.builder()
                .pokerId(pokerUserId)
                .pokedId(pokedUserId)
                .message(pokeMessage)
                .isReply(false)
                .isAnonymous(isAnonymous)
                .build());
    }
}
