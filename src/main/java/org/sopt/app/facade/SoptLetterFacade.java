package org.sopt.app.facade;

import lombok.RequiredArgsConstructor;
import org.sopt.app.application.soptletter.SoptLetterInfo.Nickname;
import org.sopt.app.application.soptletter.SoptLetterService;
import org.sopt.app.common.exception.ConflictException;
import org.sopt.app.common.response.ErrorCode;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SoptLetterFacade {

    private final SoptLetterService soptLetterService;

    public Nickname generateProfileNickname(Long userId) {
        if(soptLetterService.isOnboarded(userId)){
            throw new ConflictException(ErrorCode.ALREADY_ONBOARDED_SOPT_LETTER);
        }

        String nickname = soptLetterService.generateNickname();
        return Nickname.of(nickname);
    }
}
