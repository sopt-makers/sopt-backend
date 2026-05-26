package org.sopt.app.domain.entity.soptletter;

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
import org.hibernate.validator.constraints.Length;
import org.sopt.app.domain.entity.BaseEntity;
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

    private Double degree;

    private String message;

    @Length(max = 7)
    private String hexCode;

    @Enumerated(EnumType.STRING)
    private SoptLetterShapeType shapeType;
}
