package org.sopt.app.application.poke;

import lombok.RequiredArgsConstructor;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.common.exception.NotFoundException;
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
        PokeMessageType messageType = Arrays.stream(PokeMessageType.values())
                .filter(pokeMessageType -> pokeMessageType.name().equals(type))
                .findAny()
                .orElseThrow(() -> new NotFoundException(ErrorCode.POKE_MESSAGE_NOT_FOUND.getMessage()));
        List<Long> allMessageIds = messageRepository.findAllByType(messageType).stream()
                .map(PokeMessage::getId)
                .collect(Collectors.toList());
        Collections.shuffle(allMessageIds, new Random());
        return allMessageIds.stream()
                .limit(MESSAGES_QUANTITY_AT_ONCE)
                .toList();
    }

    public List<PokeMessageDetail> getMessagesDetail(List<Long> messageIds) {
        return messageIds.stream()
                .map(id -> {
                    PokeMessage pokeMessage = messageRepository.findById(id)
                            .orElseThrow(() -> new NotFoundException(ErrorCode.POKE_MESSAGE_NOT_FOUND.getMessage()));
                    return PokeMessageDetail.builder()
                            .id(pokeMessage.getId())
                            .content(pokeMessage.getContent())
                            .build();
                })
                .collect(Collectors.toList());
    }

}
