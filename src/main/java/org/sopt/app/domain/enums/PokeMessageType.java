package org.sopt.app.domain.enums;

import java.util.Arrays;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.sopt.app.common.exception.NotFoundException;
import org.sopt.app.common.response.ErrorCode;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum PokeMessageType {

    // 친구가 아닌 사람을 내가 먼저 찌를 때 필요한 찌르기 메시지
    POKE_SOMEONE("pokeSomeone"),

    // 친구와 찔렀을 때 & 친구에게 찔렸을 때 필요한 메시지
    POKE_FRIEND("pokeFriend"),

    // 친구가 아닌 사람이 나를 찔렀을 때 필요한 답찌르기 메시지
    REPLY_NEW("replyNew"),

    // 모든 상황에서 사용하는 메시지
    POKE_ALL("pokeAll");

    private final String parameter;

    public static PokeMessageType ofParam(String parameter) {
        return Arrays.stream(values())
                .filter(value -> value.parameter.equals(parameter))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(ErrorCode.POKE_MESSAGE_TYPE_NOT_FOUND.getMessage()));
    }
}
