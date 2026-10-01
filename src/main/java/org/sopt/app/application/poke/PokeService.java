package org.sopt.app.application.poke;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.sopt.app.common.event.EventPublisher;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.exception.NotFoundException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.poke.PokeHistory;
import org.sopt.app.domain.entity.User;
import org.sopt.app.interfaces.postgres.PokeHistoryRepository;
import org.sopt.app.interfaces.postgres.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
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

        eventPublisher.raise(PokeEvent.of(pokedUser.getId()));
        return pokeByApplyingReply;
    }

    private static final String UNREPLIED_POKE_CONSTRAINT_NAME = "uk_poke_history_poker_poked_unreplied";

    private PokeHistory createPokeByApplyingReply(
            Long pokerUserId, Long pokedUserId, String pokeMessage, Boolean isAnonymous
    ) {
        List<PokeHistory> latestPokeFromPokedIsReplyFalse = historyRepository.findAllByPokerIdAndPokedIdAndIsReplyIsFalse(
                pokedUserId, pokerUserId
        );

        if (!latestPokeFromPokedIsReplyFalse.isEmpty()) {
            latestPokeFromPokedIsReplyFalse.getFirst().activateReply();
        }
        try {
            return historyRepository.saveAndFlush(PokeHistory.builder()
                    .pokerId(pokerUserId)
                    .pokedId(pokedUserId)
                    .message(pokeMessage)
                    .isReply(false)
                    .isAnonymous(isAnonymous)
                    .build());
        } catch (DataIntegrityViolationException e) {
            if (isUnrepliedPokeConstraintViolation(e)) {
                throw new BadRequestException(ErrorCode.DUPLICATE_POKE);
            }
            throw e;
        }
    }

    private boolean isUnrepliedPokeConstraintViolation(DataIntegrityViolationException e) {
        return e.getCause() instanceof ConstraintViolationException cve
                && UNREPLIED_POKE_CONSTRAINT_NAME.equals(cve.getConstraintName());
    }

    @Transactional(readOnly = true)
    public Long getUserPokeCount(Long userId) {
        return historyRepository.countByPokerId(userId);
    }
}
