package org.sopt.app.domain.entity.soptamp;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.Type;
import org.sopt.app.domain.entity.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stamp extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String contents;

    @Column(columnDefinition = "text[]")
    @Type(value= ListArrayType.class)
    private List<String> images;

    @NotNull
    private Long userId;

    @NotNull
    private Long missionId;

    @NotNull
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
}
