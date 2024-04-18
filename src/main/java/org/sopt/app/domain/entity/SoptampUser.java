package org.sopt.app.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.app.domain.enums.PlaygroundPart;

@Entity
@Table(name = "soptamp_user", schema = "app_dev")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SoptampUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "profile_message")
    private String profileMessage;

    @Column(name = "total_points")
    private Long totalPoints;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "generation")
    private Long generation;

    @Column(name = "part")
    private String part;

    public void initTotalPoints() {
        this.totalPoints = 0L;
    }

    public void updateGenerationAndPart(Long generation, PlaygroundPart part) {
        this.generation = generation;
        this.part = part.name();
    }
}
