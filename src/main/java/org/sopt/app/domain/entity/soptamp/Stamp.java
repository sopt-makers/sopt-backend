package org.sopt.app.domain.entity.soptamp;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.Type;
import org.sopt.app.common.exception.BadRequestException;
import org.sopt.app.common.response.ErrorCode;
import org.sopt.app.domain.entity.BaseEntity;
import org.springframework.util.StringUtils;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Stamp extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contents;

    @Column(columnDefinition = "text[]")
    @Type(value= ListArrayType.class)
    private List<String> images;

    private Long userId;

    private Long missionId;

    @Column(length = 10)
    private String activityDate;

    private int clapCount = 0;

    private int viewCount = 0;

    @Version
    private Long version;

    public void changeContents(String contents) {
        this.contents = contents;
    }

    public void changeImages(List<String> images) {
        this.images = images;
    }

    public void changeActivityDate(String activityDate) {
        this.activityDate = activityDate;
    }

    public void validate() {
        if (!StringUtils.hasText(this.contents)) {
            throw new BadRequestException(ErrorCode.INVALID_STAMP_CONTENTS);
        }
        if (this.images == null || this.images.isEmpty()) {
            throw new BadRequestException(ErrorCode.INVALID_STAMP_IMAGES);
        }
        if (this.activityDate == null) {
            throw new BadRequestException(ErrorCode.INVALID_STAMP_ACTIVITY_DATE);
        }
        if (this.missionId == null) {
            throw new BadRequestException(ErrorCode.INVALID_STAMP_MISSION_ID);
        }
    }

    public void incrementClapCount(int increment) {
        if (increment <= 0) return;
        this.clapCount += increment;
    }

    public void incrementViewCount() {
        this.viewCount += 1;
    }

    @Builder
    private Stamp(Long id, String activityDate, Long userId, Long missionId,
        String contents, List<String> images) {
        this.id = id;
        this.activityDate = activityDate;
        this.userId = userId;
        this.missionId = missionId;
        this.contents = contents;
        this.images = images;
        this.clapCount = 0;
        this.viewCount = 0;
    }
}
