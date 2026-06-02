package org.sopt.app.facade;

import lombok.RequiredArgsConstructor;
import org.sopt.app.application.soptletter.SoptLetterInfo.Profile;
import org.sopt.app.application.soptletter.SoptLetterService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SoptLetterFacade {

    private final SoptLetterService soptLetterService;

    public Profile generateProfileNickname(Long userId) {
        return soptLetterService.getOrCreateProfile(userId);
    }
}
