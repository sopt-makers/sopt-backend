package org.sopt.app.domain.entity;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "ALERT")
@TypeDef(
        name = "list-array",
        typeClass = ListArrayType.class
)
public class Alert {
    @Id
    private Long id;

    @Column
    private Long tmpUserId;

    @Type(type = "list-array")
    @Column(
            columnDefinition = "text[]"
    )
    private List<String> topic;
}
