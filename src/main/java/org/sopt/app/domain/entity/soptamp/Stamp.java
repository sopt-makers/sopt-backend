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
@Builder
@NoArgsConstructor
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

}
