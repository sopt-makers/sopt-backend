package org.sopt.app.application.rank;

import java.util.EnumMap;
import java.util.Map;
import org.sopt.app.domain.enums.Part;

public class PartScores {

    private final Map<Part, Long> scores = new EnumMap<>(Part.class);

    protected PartScores() {
        Part.getAllParts().forEach(part -> scores.put(part, 0L));
    }

    protected void addPartScore(final Part part, final Long point) {
        scores.put(part, scores.get(part) + point);
    }

    protected Long getPoints(final Part part) {
        return scores.get(part);
    }

    protected int getRank(final Part part) {
        Long targetPartPoint = getPoints(part);
        int rankPoint = 1;

        for (Long partScore : scores.values()) {
            if (targetPartPoint < partScore) {
                rankPoint++;
            }
        }
        return rankPoint;
    }
}
