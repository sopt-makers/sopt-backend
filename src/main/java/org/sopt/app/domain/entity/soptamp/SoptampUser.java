package org.sopt.app.domain.entity.soptamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.sopt.app.domain.entity.BaseEntity;
import org.sopt.app.domain.enums.PlaygroundPart;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SoptampUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private String profileMessage;

    @NotNull
    private Long totalPoints;

    @NotNull
    private String nickname;

    @NotNull
    private Long generation;

    @NotNull
    private String part;

    public void initTotalPoints() {
        this.totalPoints = 0L;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateGenerationAndPart(Long generation, PlaygroundPart part) {
        this.generation = generation;
        this.part = part.name();
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
