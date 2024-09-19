package org.sopt.app.domain.entity.soptamp;


import com.vladmihalcea.hibernate.type.array.ListArrayType;
import java.util.List;
import javax.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.sopt.app.domain.entity.BaseEntity;

@Entity
@TypeDef(
        name = "list-array",
        typeClass = ListArrayType.class
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Stamp extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contents;

    @Type(type = "list-array")
    @Column(columnDefinition = "text[]")
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
}
