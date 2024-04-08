package org.sopt.app.domain.entity;


import com.vladmihalcea.hibernate.type.array.ListArrayType;
import java.util.List;
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
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@Table(name = "stamp", schema = "app_dev")
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

    @Column
    private String contents;

    @Type(type = "list-array")
    @Column(
            columnDefinition = "text[]"
    )
    private List<String> images;

    @Column
    private Long userId;

    @Column
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
