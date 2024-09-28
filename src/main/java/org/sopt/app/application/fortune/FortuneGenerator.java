package org.sopt.app.application.fortune;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.sopt.app.domain.entity.fortune.UserFortune;
import org.sopt.app.interfaces.postgres.fortune.FortuneCardRepository;
import org.sopt.app.interfaces.postgres.fortune.UserFortuneRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FortuneGenerator {

    private final UserFortuneRepository userFortuneRepository;
    private final FortuneCardRepository fortuneCardRepository;
    private final Random random = new Random();

    public UserFortune generateNewUserFortune(Long userId, LocalDate todayDate) {
        return UserFortune.builder()
                .userId(userId)
                .checkedAt(todayDate)
                .fortuneId(this.generateRandomFortuneCardId())
                .build();
    }

    public void generateTodayUserFortune(final Long userId, final LocalDate todayDate) {
        Optional<UserFortune> userFortune = userFortuneRepository.findByUserId(userId);
        if (userFortune.isPresent()) {
            userFortune.get().updateTodayFortune(this.generateRandomFortuneCardId(), todayDate);
        } else {
            userFortuneRepository.save(this.generateNewUserFortune(userId, todayDate));
        }
    }

    private Long generateRandomFortuneCardId() {
        List<Long> fortuneCardIds = fortuneCardRepository.findAllIds();
        return fortuneCardIds.get(random.nextInt(fortuneCardIds.size()));
    }

}
