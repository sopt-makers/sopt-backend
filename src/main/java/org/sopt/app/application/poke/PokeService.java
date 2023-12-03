package org.sopt.app.application.poke;

import lombok.RequiredArgsConstructor;
import org.sopt.app.common.event.Events;
import org.sopt.app.common.exception.NotFoundException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.PokeHistory;
import org.sopt.app.domain.entity.PokeMessage;
import org.sopt.app.domain.entity.User;
import org.sopt.app.interfaces.postgres.PokeHistoryRepository;
import org.sopt.app.interfaces.postgres.PokeMessageRepository;
import org.sopt.app.interfaces.postgres.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PokeService {

    private final UserRepository userRepository;
    private final PokeHistoryRepository historyRepository;
    private final PokeMessageRepository messageRepository;

    @Transactional
    public void poke(Long pokerUserId, Long pokedUserId, Long pokeMessageId) {
        User pokedUser = userRepository.findUserById(pokedUserId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND.getMessage()));
        PokeMessage pokeMessage = messageRepository.findById(pokeMessageId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POKE_MESSAGE_NOT_FOUND.getMessage()));

        PokeHistory createdPoke = PokeHistory.builder()
                .pokerId(pokerUserId)
                .pokedId(pokedUserId)
                .message(pokeMessage.getContent())
                .isReply(false)
                .build();
        historyRepository.save(createdPoke);

        Events.raise(PokeEvent.of(pokedUser.getPlaygroundId()));
    }
}
