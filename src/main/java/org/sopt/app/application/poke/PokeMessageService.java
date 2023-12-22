package org.sopt.app.application.poke;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.domain.entity.PokeMessage;
import org.sopt.app.domain.enums.PokeMessageType;
import org.sopt.app.interfaces.postgres.PokeMessageRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PokeMessageService {

    private static final int MESSAGES_QUANTITY_AT_ONCE = 5;
    private final PokeMessageRepository messageRepository;

    public List<PokeMessage> pickRandomMessageByTypeOf(String type) {
        PokeMessageType messageType = PokeMessageType.ofParam(type);
        val messages = messageRepository.findAllByType(messageType);
        Collections.shuffle(messages, new Random());
        return messages.stream()
                .limit(MESSAGES_QUANTITY_AT_ONCE)
                .toList();
    }
}

