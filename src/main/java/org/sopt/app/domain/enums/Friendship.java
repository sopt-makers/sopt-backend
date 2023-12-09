package org.sopt.app.domain.enums;

import lombok.*;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Friendship {
    NON_FRIEND(""), NEW_FRIEND("친한친구"), BEST_FRIEND("단짝친구"), SOULMATE("천생연분")
    ;

    private final String value;

}
