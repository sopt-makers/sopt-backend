package org.sopt.app.domain.entity.soptamp;

import jakarta.persistence.*;
import lombok.*;
import org.sopt.app.domain.entity.BaseEntity;
import org.sopt.app.domain.enums.PlaygroundPart;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(schema = "app_dev")
public class SoptampUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String profileMessage;

    private Long totalPoints;

    private String nickname;

    private Long generation;

    private String part;

    public void initTotalPoints() {
        this.totalPoints = 0L;
    }

    public static SoptampUser createNewSoptampUser(Long userId, String nickname, Long generation, PlaygroundPart part) {
        return SoptampUser.builder()
                .userId(userId)
                .nickname(nickname)
                .generation(generation)
                .part(part.getPartName())
                .totalPoints(0L)
                .profileMessage("")
                .build();
    }

    public void updateGenerationAndPart(Long generation, PlaygroundPart part) {
        this.generation = generation;
        this.part = part.getPartName();
    }

    public void addPointsByLevel(Integer level) {
        this.totalPoints += level;
    }

    public void subtractPointsByLevel(Integer level) {
        this.totalPoints -= level;
    }

    public void updateProfileMessage(String profileMessage) {
        this.profileMessage = profileMessage;
    }
}
