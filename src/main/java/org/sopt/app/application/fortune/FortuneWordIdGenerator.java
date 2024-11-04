package org.sopt.app.application.fortune;

import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.sopt.app.interfaces.postgres.fortune.FortuneWordRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FortuneWordIdGenerator {

    private final FortuneWordRepository fortuneWordRepository;
    private final Random random = new Random();

    public Long generateRandomFortuneWordId() {
        List<Long> fortuneWordIds = fortuneWordRepository.findAllIds();
        return fortuneWordIds.get(random.nextInt(fortuneWordIds.size()));
    }
}
