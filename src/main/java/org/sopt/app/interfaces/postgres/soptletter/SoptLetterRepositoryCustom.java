package org.sopt.app.interfaces.postgres.soptletter;

import java.util.List;

public interface SoptLetterRepositoryCustom {

    List<TopicUnreadCount> countUnreadLettersByTopic(Long userId, Long profileId);

    record TopicUnreadCount(Long topicId, Long unreadCount) {}
}
