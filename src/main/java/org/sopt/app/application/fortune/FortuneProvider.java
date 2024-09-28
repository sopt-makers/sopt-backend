package org.sopt.app.application.fortune;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.sopt.app.application.s3.S3Service;
import org.sopt.app.domain.entity.fortune.FortuneCard;
import org.sopt.app.domain.entity.fortune.FortuneWord;
import org.sopt.app.domain.enums.FortuneColorType;
import org.sopt.app.interfaces.postgres.fortune.FortuneCardRepository;
import org.sopt.app.interfaces.postgres.fortune.FortuneWordRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FortuneProvider {

    private final FortuneCardRepository fortuneCardRepository;
    private final FortuneWordRepository fortuneWordRepository;
    private final S3Service s3Service;
    public FortuneWordInfo getTodayFortuneByUserId(final Long userId, final LocalDate todayDate) {
        FortuneWord fortuneWord = fortuneWordRepository.findByUserIdAndCheckAt(userId, todayDate)
                .orElseGet(() -> {
                    FortuneColorType fortuneColorType = createRandomColorType();
                    String url = getRandomUrlByColor(fortuneColorType);
                    FortuneCard fortuneCard = FortuneCard
                            .of(getNameFromFile(url), createRandomDescription() , url,fortuneColorType.getColorCode());
                    FortuneCard card = fortuneCardRepository.save(fortuneCard);
                    return FortuneWord.of(
                            userId, createRandomFortuneTitle(), card.getId(), todayDate);
                });
        fortuneWordRepository.save(fortuneWord);
        return FortuneWordInfo.of(fortuneWord);
    }

    public FortuneCardInfo getTodayFortuneCardByUserId(final Long userId) {
        LocalDate todayDate = LocalDate.now();
        Optional<FortuneWord> fortuneWord = Optional.ofNullable(
                fortuneWordRepository.findByUserIdAndCheckAt(userId, todayDate)
                        .orElseThrow(
                                EntityNotFoundException::new
                        ));
        return FortuneCardInfo.of(
                fortuneCardRepository.findById(fortuneWord.get().getFortuneCardId())
                        .orElseThrow(
                                EntityNotFoundException::new
                        )
        );
    }

    private String createRandomFortuneTitle(){
        // Random title generation logic herer
        return "단순하게 생각하면 일이 술술 풀리겠솝!";
    }

    private FortuneColorType createRandomColorType(){
        Random random = new Random();
        FortuneColorType[] values = FortuneColorType.values();
        return values[random.nextInt(values.length)];
    }

    private String getRandomUrlByColor(FortuneColorType fortuneColorType) {
        Random random = new Random();
        List<String> urls = s3Service.getAllImageUrls(fortuneColorType.getColorType().toLowerCase(Locale.ROOT));
        return urls.get(random.nextInt(urls.size()));
    }

    private String createRandomDescription() {
        return "어려움을 전부 극복할";
    }

    public String getNameFromFile(String url) {
        String fileNameWithExtension = s3Service.getFileNameList(List.of(url)).getFirst();
        int extensionIndex = fileNameWithExtension.lastIndexOf(".");
        String fileNameWithoutExtension;
        if (extensionIndex != -1) {
            fileNameWithoutExtension = fileNameWithExtension.substring(0, extensionIndex); // 확장자 제거
        } else {
            fileNameWithoutExtension = fileNameWithExtension; // 확장자가 없는 경우
        }
        return fileNameWithoutExtension;
    }
}
