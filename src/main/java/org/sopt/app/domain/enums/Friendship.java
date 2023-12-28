package org.sopt.app.domain.enums;

import lombok.*;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Friendship {
    NON_FRIEND("nonfriend","새로운 친구", 0,2),
    NEW_FRIEND("new","친한친구",2, 4),
    BEST_FRIEND("bestfriend","단짝친구",5,10),
    SOULMATE("soulmate","천생연분",11,99)
    ;

    private final String typeFlag;
    private final String friendshipName;
    private final Integer lowerLimit;
    private final Integer upperLimit;

    public static Friendship getFriendshipByValue(String type) {
        return Arrays.stream(values())
                .filter(friendship -> friendship.getTypeFlag().equals(type))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(ErrorCode.FRIENDSHIP_NOT_FOUND.getMessage()));
    }
}
