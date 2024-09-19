package org.sopt.app.application.fortune;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.sopt.app.interfaces.postgres.fortune.FortuneCardRepository;
import org.sopt.app.interfaces.postgres.fortune.FortuneRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FortuneProvider {

    private final FortuneCardRepository fortuneCardRepository;
    private final FortuneRepository fortuneRepository;

    public FortuneInfo getTodayFortuneByUserId(final Long userId, final LocalDate todayDate) {
        return FortuneInfo.of(fortuneRepository.findByRelatedUserIdAndCheckedAt(userId, todayDate).get());
    }

    public FortuneCardInfo getTodayFortuneCardByUserId(final Long userId) {
        return FortuneCardInfo.of(fortuneCardRepository.findByRelatedUserId(userId).get());
    }
}
