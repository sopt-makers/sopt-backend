package org.sopt.app.facade;

import lombok.RequiredArgsConstructor;
import org.sopt.app.application.soptletter.SoptLetterInfo;
import org.sopt.app.application.soptletter.SoptLetterInfo.Profile;
import org.sopt.app.application.soptletter.SoptLetterService;
import org.sopt.app.application.soptletter.SoptLetterTopicReadEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SoptLetterFacade {

    private final SoptLetterService soptLetterService;
    private final ApplicationEventPublisher eventPublisher;

    public Profile getOrCreateOnboardingProfile(Long userId) {
        return soptLetterService.getOrCreateProfile(userId);
    }

    public Profile completeOnboardingProfile(Long userId) {
        return soptLetterService.completeOnboarding(userId);
    }

    public SoptLetterInfo.TopicMessageListResult getTopicMessages(Long userId, Long topicId, Long cursor, Integer size) {
        SoptLetterInfo.TopicMessageListResult result = soptLetterService.getTopicMessages(userId, topicId, cursor, size);
        publishTopicReadEvent(userId, cursor, result);
        return result;
    }

    public SoptLetterInfo.TopicMessageListResult getDefaultTopicMessages(Long userId, Long cursor, Integer size) {
        SoptLetterInfo.TopicMessageListResult result = soptLetterService.getDefaultTopicMessages(userId, cursor, size);
        publishTopicReadEvent(userId, cursor, result);
        return result;
    }

    public SoptLetterInfo.ReportFormResult getReportForm() {
        return soptLetterService.getReportForm();
    }

    public SoptLetterInfo.TopicListResult getTopics(Long userId, String type) {
        return soptLetterService.getTopics(userId, type);
    }

    public SoptLetterInfo.CtaResult getCta() {
        return soptLetterService.getCta();
    }

    public SoptLetterInfo.TopicDetail getTopic(Long topicId) {
        return soptLetterService.getTopic(topicId);
    }

    public SoptLetterInfo.MessageResult createSoptLetter(Long userId, Long topicId, String content) {
        return soptLetterService.createSoptLetter(userId, topicId, content);
    }

    public SoptLetterInfo.MessageResult updateSoptLetter(Long userId, Long topicId, Long soptLetterId, String content) {
        return soptLetterService.updateSoptLetter(userId, topicId, soptLetterId, content);
    }

    public void deleteSoptLetter(Long userId, Long topicId, Long soptLetterId) {
        soptLetterService.deleteSoptLetter(userId, topicId, soptLetterId);
    }

    public void addLike(Long userId, Long topicId, Long soptLetterId) {
        soptLetterService.addLike(userId, topicId, soptLetterId);
    }

    public void removeLike(Long userId, Long topicId, Long soptLetterId) {
        soptLetterService.removeLike(userId, topicId, soptLetterId);
    }

    public SoptLetterInfo.MessageResult getMessageDetail(Long userId, Long topicId, Long messageId) {
        return soptLetterService.getMessageDetail(userId, topicId, messageId);
    }

    private void publishTopicReadEvent(Long userId, Long cursor, SoptLetterInfo.TopicMessageListResult result) {
        if (cursor != null || result.getMessages().isEmpty()) {
            return;
        }
        Long latestLetterId = result.getMessages().get(0).getMessageId();
        eventPublisher.publishEvent(SoptLetterTopicReadEvent.of(userId, result.getTopicId(), latestLetterId));
    }
}
