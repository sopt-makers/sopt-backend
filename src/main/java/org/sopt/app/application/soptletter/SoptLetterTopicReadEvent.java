package org.sopt.app.application.soptletter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.sopt.app.common.event.Event;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SoptLetterTopicReadEvent extends Event {

    private Long userId;
    private Long topicId;
    private Long lastReadLetterId;

    public static SoptLetterTopicReadEvent of(Long userId, Long topicId, Long lastReadLetterId) {
        return new SoptLetterTopicReadEvent(userId, topicId, lastReadLetterId);
    }
}
