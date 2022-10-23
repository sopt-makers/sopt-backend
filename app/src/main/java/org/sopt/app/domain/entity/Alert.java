package org.sopt.app.domain.entity;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ALERT")
@TypeDef(
        name = "list-array",
        typeClass = ListArrayType.class
)
@Getter
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long tmpUserId;

    @Type(type = "list-array")
    @Column(
            columnDefinition = "text[]"
    )
    private List<String> topic;

    @Column
    private Boolean active;

    @Builder
    public Alert(Long tmpUserId, List<String> topic, Boolean active) {
        this.tmpUserId = tmpUserId;
        this.topic = topic;
        this.active = active;
    }

    public Alert() {

    }
}
