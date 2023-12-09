package org.sopt.app.domain.enums;

import lombok.*;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Friendship {
    NON_FRIEND("", 0,1), NEW_FRIEND("친한친구",2, 4), BEST_FRIEND("단짝친구",5,10), SOULMATE("천생연분",11,99)
    ;

    private final String value;
    private final Integer lowerLimit;
    private final Integer upperLimit;

}
