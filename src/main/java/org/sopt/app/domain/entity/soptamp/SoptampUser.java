package org.sopt.app.domain.entity.soptamp;

import jakarta.persistence.*;
import lombok.*;
import org.sopt.app.domain.entity.BaseEntity;
import org.sopt.app.domain.enums.SoptPart;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SoptampUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String profileMessage;

    private Long totalPoints;

    private String nickname;

    private Long generation;

    @Enumerated(EnumType.STRING)
    private SoptPart part;

    public void initTotalPoints() {
        this.totalPoints = 0L;
    }

    public static SoptampUser createNewSoptampUser(Long userId, String nickname, Long generation, SoptPart part) {
        return SoptampUser.builder()
                .userId(userId)
                .nickname(nickname)
                .generation(generation)
                .part(part)
                .totalPoints(0L)
                .profileMessage("")
                .build();
    }

    public void updateChangedGenerationInfo(Long generation, SoptPart part, String nickname) {
        this.generation = generation;
        this.part = part;
        this.nickname = nickname;
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
