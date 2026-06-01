package org.sopt.app.application.soptletter;

import lombok.RequiredArgsConstructor;
import org.sopt.app.common.utils.AnonymousNameGenerator;
import org.sopt.app.interfaces.postgres.soptletter.SoptLetterProfileRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SoptLetterService {

    private final SoptLetterProfileRepository soptLetterProfileRepository;
    private final AnonymousNameGenerator anonymousNameGenerator;

    public boolean isOnboarded(Long userId) {
        return soptLetterProfileRepository.existsByUserId(userId);
    }

    public String generateNickname() {
        return anonymousNameGenerator.generate();
    }

}
