package org.sopt.app.application.poke;

import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.sopt.app.domain.entity.poke.PokeMessage;
import org.sopt.app.domain.enums.PokeMessageType;
import org.sopt.app.interfaces.postgres.PokeMessageRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PokeMessageService {

    private static final int MESSAGES_QUANTITY_AT_ONCE = 4;
    private static final String MESSAGES_HEADER_FOR_POKE = "함께 보낼 메시지를 선택해주세요";
    private static final String MESSAGES_HEADER_FOR_REPLY = "답장하고 싶은 메시지를 선택해주세요";
    private static final String FIXED_MESSAGE = "콕 \uD83D\uDC48";
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
                .collect(Collectors.toList());
    }

    public PokeMessage getFixedMessage() {
        return PokeMessage.builder()
                .id(0L).content(FIXED_MESSAGE).type(PokeMessageType.POKE_ALL)
                .build();
    }
}

