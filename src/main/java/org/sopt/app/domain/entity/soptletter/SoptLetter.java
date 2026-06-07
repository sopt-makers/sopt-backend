package org.sopt.app.domain.entity.soptletter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.app.common.exception.ForbiddenException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.BaseEntity;
import org.sopt.app.domain.enums.SoptLetterColor;
import org.sopt.app.domain.enums.SoptLetterShapeType;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SoptLetter extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long authorProfileId;

    private Long topicId;

    @Column(nullable = false)
    private Double degree;

    @Column(length = 350, nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    private SoptLetterColor color;

    @Enumerated(EnumType.STRING)
    private SoptLetterShapeType shapeType;

    private Integer likeCount;

    public void updateMessage(Long requesterProfileId, String message) {
        validateAuthor(requesterProfileId);
        this.message = message;
    }

    public void validateDeletable(Long requesterProfileId) {
        validateAuthor(requesterProfileId);
    }

    private void validateAuthor(Long profileId) {
        if (!this.authorProfileId.equals(profileId)) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }
    }
}
