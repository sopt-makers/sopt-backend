package org.sopt.app.domain.entity.soptamp;

import javax.persistence.*;
import lombok.AccessLevel;
import lombok.*;
import org.sopt.app.domain.entity.BaseEntity;
import org.sopt.app.domain.enums.PlaygroundPart;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateGenerationAndPart(Long generation, PlaygroundPart part) {
        this.generation = generation;
        this.part = part.name();
    }
}
