package org.sopt.app.application.fortune;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.sopt.app.domain.entity.fortune.UserFortune;
import org.sopt.app.interfaces.postgres.fortune.FortuneWordRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FortuneGenerator {

    private final FortuneWordRepository fortuneWordRepository;
    private final Random random = new Random();

    public UserFortune generateNewUserFortune(Long userId, LocalDate todayDate) {
        return UserFortune.builder()
                .userId(userId)
                .checkedAt(todayDate)
                .fortuneId(this.generateRandomFortuneWordId())
                .build();
    }

    public void updateTodayUserFortune(UserFortune userFortune, final LocalDate todayDate) {
        userFortune.updateTodayFortune(this.generateRandomFortuneWordId(), todayDate);
    }

    private Long generateRandomFortuneWordId() {
        List<Long> fortuneWordIds = fortuneWordRepository.findAllIds();
        return fortuneWordIds.get(random.nextInt(fortuneWordIds.size()));
    }

}
