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
    private static final String MESSAGES_HEADER_FOR_POKE = "함께 보낼 메시지를 선택해주세요";
    private static final String MESSAGES_HEADER_FOR_REPLY = "답장하고 싶은 메시지를 선택해주세요";
    private final PokeMessageRepository messageRepository;

    public String getMessagesHeaderComment(String type) {
        PokeMessageType pokeMessageType = PokeMessageType.ofParam(type);
        if (pokeMessageType.equals(PokeMessageType.REPLY_NEW)) {
            return MESSAGES_HEADER_FOR_REPLY;
        }
        return MESSAGES_HEADER_FOR_POKE;
    }
    public List<PokeMessage> pickRandomMessageByTypeOf(String type) {
        PokeMessageType messageType = PokeMessageType.ofParam(type);
        val messages = messageRepository.findAllByType(messageType);
        Collections.shuffle(messages, new Random());
        return messages.stream()
                .limit(MESSAGES_QUANTITY_AT_ONCE)
                .toList();
    }
}

