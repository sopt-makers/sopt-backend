package org.sopt.app.domain.enums;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.sopt.app.common.exception.NotFoundException;
import org.sopt.app.common.response.ErrorCode;

import java.util.Arrays;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum PokeMessageType {
    POKE_SOMEONE("pokeSomeone"),
    POKE_FRIEND("pokeFriend"),
    REPLY_NEW("replyNew"),
    REPLY_FRIEND("replyFriend"),
    ;

    private final String parameter;

    public static PokeMessageType ofParam(String parameter) {
        return Arrays.stream(values())
                .filter(value -> value.parameter.equals(parameter))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(ErrorCode.POKE_MESSAGE_TYPE_NOT_FOUND.getMessage()));
    }
}
