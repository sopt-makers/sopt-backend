package org.sopt.app.application.fortune;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.fortune.UserFortune;
import org.sopt.app.interfaces.postgres.fortune.FortuneCardRepository;
import org.sopt.app.interfaces.postgres.fortune.FortuneWordRepository;
import org.sopt.app.interfaces.postgres.fortune.UserFortuneRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FortuneProvider {

    private final FortuneCardRepository fortuneCardRepository;
    private final FortuneWordRepository fortuneWordRepository;
    private final UserFortuneRepository userFortuneRepository;
    private final FortuneGenerator fortuneGenerator;

    @Transactional
    public FortuneWordInfo getTodayFortuneWordByUserId(final Long userId, final LocalDate todayDate) {
        UserFortune userFortune = userFortuneRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserFortune generatedUserFortune = fortuneGenerator.generateNewUserFortune(userId, todayDate);
                    return userFortuneRepository.save(generatedUserFortune);
                });

        if(!userFortune.getCheckedAt().equals(todayDate)) {
            fortuneGenerator.updateTodayUserFortune(userFortune, todayDate);
        }

        return fortuneWordRepository.findById(userFortune.getFortuneId())
                .map(FortuneWordInfo::of)
                .orElseThrow(() -> new BadRequestException(ErrorCode.FORTUNE_NOT_FOUND));
    }

    public FortuneCardInfo getTodayFortuneCardByUserId(final Long userId) {
        return fortuneCardRepository.findByRelatedUserId(userId)
                .map(FortuneCardInfo::of)
                .orElseThrow(() -> new BadRequestException(ErrorCode.FORTUNE_NOT_FOUND_FROM_USER));
    }
}
