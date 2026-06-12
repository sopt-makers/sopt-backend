package org.sopt.app.facade;

import lombok.RequiredArgsConstructor;
import org.sopt.app.application.soptletter.SoptLetterInfo;
import org.sopt.app.application.soptletter.SoptLetterInfo.Profile;
import org.sopt.app.application.soptletter.SoptLetterService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SoptLetterFacade {

    private final SoptLetterService soptLetterService;

    public Profile getOrCreateOnboardingProfile(Long userId) {
        return soptLetterService.getOrCreateProfile(userId);
    }

    public Profile completeOnboardingProfile(Long userId) {
        return soptLetterService.completeOnboarding(userId);
    }

    public SoptLetterInfo.TopicMessageListResult getTopicMessages(Long userId, Long topicId, Long cursor, Integer size) {
        return soptLetterService.getTopicMessages(userId, topicId, cursor, size);
    }

    public SoptLetterInfo.ReportFormResult getReportForm() {
        return soptLetterService.getReportForm();
    }

    public SoptLetterInfo.TopicListResult getTopics() {
        return soptLetterService.getTopics();
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

    public SoptLetterInfo.MessageResult getMessageDetail(Long userId, Long topicId, Long messageId) {
        return soptLetterService.getMessageDetail(userId, topicId, messageId);
    }
}
