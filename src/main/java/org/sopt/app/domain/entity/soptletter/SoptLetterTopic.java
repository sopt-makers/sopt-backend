package org.sopt.app.domain.entity.soptletter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.app.domain.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SoptLetterTopic extends BaseEntity {

    private static final int NEW_TOPIC_DAYS = 7;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String subtitle;

    @Column(length = 50)
    private String ctaText;

    @Column(nullable = false)
    private boolean isDefault;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private LocalDateTime endedAt;

    public boolean isActiveAt(LocalDateTime now) {
        if (startedAt == null || endedAt == null) {
            return false;
        }
        return !now.isBefore(startedAt) && !now.isAfter(endedAt);
    }

    public boolean isNewAt(LocalDateTime now) {
        if (getCreatedAt() == null) {
            return false;
        }
        return now.isBefore(getCreatedAt().plusDays(NEW_TOPIC_DAYS));
    }
}
