package org.sopt.app.domain.entity.soptamp;

import jakarta.persistence.*;
import lombok.*;
import org.sopt.app.domain.entity.BaseEntity;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SoptampPoint extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long generation;

    private Long soptampUserId;

    private Long points;

    public void addPointsByLevelValue(Integer level) {
        this.points += level;
    }

    public void subtractPointsByLevelValue(Integer level) {
        this.points -= level;
    }

    public void initPoint() {
        this.points = 0L;
    }

    public static SoptampPoint createNewSoptampPoint(Long generation, Long soptampUserId) {
        return SoptampPoint.builder()
                .generation(generation)
                .soptampUserId(soptampUserId)
                .points(0L)
                .build();
    }
}
