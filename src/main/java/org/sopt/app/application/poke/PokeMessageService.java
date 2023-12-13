package org.sopt.app.application.poke;

import lombok.RequiredArgsConstructor;
import org.sopt.app.domain.entity.PokeMessage;
import org.sopt.app.domain.enums.PokeMessageType;
import org.sopt.app.interfaces.postgres.PokeMessageRepository;
import org.sopt.app.application.poke.PokeInfo.PokeMessageDetail;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PokeMessageService {

    private static final int MESSAGES_QUANTITY_AT_ONCE = 5;
    private final PokeMessageRepository messageRepository;

    public List<Long> pickRandomMessageIdsTypeOf(String type) {
        PokeMessageType messageType = PokeMessageType.ofParam(type);
        List<Long> allMessageIds = messageRepository.findAllByType(messageType).stream()
                .map(PokeMessage::getId)
                .collect(Collectors.toList());
        Collections.shuffle(allMessageIds, new Random());
        return allMessageIds.stream()
                .limit(MESSAGES_QUANTITY_AT_ONCE)
                .toList();
    }

    public List<PokeMessageDetail> getMessagesDetail(List<Long> messageIds) {
        return messageRepository.findAllByIdIn(messageIds).stream()
                .map(pokeMessage -> PokeMessageDetail.builder()
                            .id(pokeMessage.getId())
                            .content(pokeMessage.getContent())
                            .build())
                .collect(Collectors.toList());
    }

}

