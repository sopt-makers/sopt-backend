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

    public SoptLetterInfo.MessageResult createSoptLetter(Long userId, Long topicId, String content) {
        return soptLetterService.createSoptLetter(userId, topicId, content);
    }

    public SoptLetterInfo.MessageResult updateSoptLetter(Long userId, Long messageId, String content) {
        return soptLetterService.updateSoptLetter(userId, messageId, content);
    }

    public void deleteSoptLetter(Long userId, Long messageId) {
        soptLetterService.deleteSoptLetter(userId, messageId);
    }
}
