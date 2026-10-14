package org.sopt.app.application.soptletter;

public record SoptLetterLikeEvent(Long likerUserId, Long authorUserId, Long topicId, Long messageId) {
}
