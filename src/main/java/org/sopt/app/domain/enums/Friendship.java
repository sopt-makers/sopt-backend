package org.sopt.app.domain.enums;

import lombok.*;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Friendship {
    NON_FRIEND("", 0,1), NEW_FRIEND("친한친구",2, 4), BEST_FRIEND("단짝친구",5,10), SOULMATE("천생연분",11,99)
    ;

    private final String value;
    private final Integer lowerLimit;
    private final Integer upperLimit;

    public static Friendship getFriendshipByValue(String value) {
        return Arrays.stream(values())
                .filter(friendship -> friendship.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(ErrorCode.FRIENDSHIP_NOT_FOUND.getMessage()));
    }
}
