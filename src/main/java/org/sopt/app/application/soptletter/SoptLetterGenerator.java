package org.sopt.app.application.soptletter;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.sopt.app.domain.entity.soptletter.SoptLetter;
import org.sopt.app.domain.enums.SoptLetterColor;
import org.sopt.app.domain.enums.SoptLetterShapeType;
import org.springframework.stereotype.Component;

@Component
public class SoptLetterGenerator {

    private static final List<Double> ROTATION_DEGREES = List.of(-10.0, 0.0, 10.0);

    private static class ShapeHolder {
        private static final SoptLetterShapeType[] VALUES = SoptLetterShapeType.values();
    }


    public SoptLetter generate(Long authorProfileId, Long topicId, String message, SoptLetterColor previousColor) {
        return SoptLetter.builder()
                .authorProfileId(authorProfileId)
                .topicId(topicId)
                .degree(getRandomDegree())
                .message(message)
                .color(getNextColor(previousColor))
                .shapeType(getRandomShapeType())
                .likeCount(0)
                .build();
    }

    private SoptLetterColor getNextColor(SoptLetterColor currentColor) {
        if (currentColor == null) {
            return SoptLetterColor.BLUE_50;
        }
        return switch (currentColor) {
            case BLUE_50 -> SoptLetterColor.GREEN_50;
            case GREEN_50 -> SoptLetterColor.YELLOW_50;
            case YELLOW_50 -> SoptLetterColor.RED_50;
            case RED_50 -> SoptLetterColor.BLUE_50;
        };
    }

    private Double getRandomDegree() {
        int randomIndex = ThreadLocalRandom.current().nextInt(ROTATION_DEGREES.size());
        return ROTATION_DEGREES.get(randomIndex);
    }


    private SoptLetterShapeType getRandomShapeType() {
        return ShapeHolder.VALUES[ThreadLocalRandom.current().nextInt(ShapeHolder.VALUES.length)];
    }

}
