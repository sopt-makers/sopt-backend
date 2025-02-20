package org.sopt.app.application.fortune;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.user.UserWithdrawEvent;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.common.utils.CurrentDate;
import org.sopt.app.domain.entity.fortune.UserFortune;
import org.sopt.app.interfaces.postgres.fortune.FortuneCardRepository;
import org.sopt.app.interfaces.postgres.fortune.FortuneWordRepository;
import org.sopt.app.interfaces.postgres.fortune.UserFortuneRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FortuneService {

    private final FortuneCardRepository fortuneCardRepository;
    private final FortuneWordRepository fortuneWordRepository;
    private final UserFortuneRepository userFortuneRepository;
    private final FortuneWordIdGenerator fortuneWordIdGenerator;

    @Transactional
    public FortuneWordInfo getTodayFortuneWordByUserId(final Long userId, final LocalDate todayDate) {
        UserFortune userFortune = this.getTodayUserFortune(userId, todayDate);

        return fortuneWordRepository.findById(userFortune.getFortuneWordId())
                .map(FortuneWordInfo::of)
                .orElseThrow(() -> new BadRequestException(ErrorCode.FORTUNE_NOT_FOUND));
    }

    private UserFortune getTodayUserFortune(final Long userId, final LocalDate todayDate) {
        return userFortuneRepository.findByUserId(userId)
                .map(userFortune -> validate(userFortune, todayDate))
                .orElseGet(() -> createNewUserFortune(userId, todayDate));
    }

    private UserFortune validate(UserFortune userFortune, LocalDate todayDate) {
        if (!userFortune.getCheckedAt().equals(todayDate)) {
            userFortune.updateTodayFortune(fortuneWordIdGenerator.generateRandomFortuneWordId(), todayDate);
        }
        return userFortune;
    }

    private UserFortune createNewUserFortune(Long userId, LocalDate todayDate) {
        return userFortuneRepository.save(
                UserFortune.builder()
                        .userId(userId)
                        .checkedAt(todayDate)
                        .fortuneWordId(fortuneWordIdGenerator.generateRandomFortuneWordId())
                        .build()
        );
    }

    public FortuneCardInfo getTodayFortuneCardByUserId(final Long userId) {
        return fortuneCardRepository.findByRelatedUserId(userId)
                .map(FortuneCardInfo::of)
                .orElseThrow(() -> new BadRequestException(ErrorCode.FORTUNE_NOT_FOUND_FROM_USER));
    }

    @EventListener(UserWithdrawEvent.class)
    public void handleUserWithdrawEvent(final UserWithdrawEvent event) {
        userFortuneRepository.deleteAllByUserIdInQuery(event.getUserId());
    }

    public boolean isExistTodayFortune(final Long userId) {
        return userFortuneRepository.findByUserId(userId)
                .map(userFortune -> userFortune.getCheckedAt().equals(CurrentDate.now))
                .orElse(false);
    }
}
