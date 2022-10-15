package org.sopt.app.domain.entity;


import com.vladmihalcea.hibernate.type.array.ListArrayType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "NOTICE")
@TypeDef(
        name = "list-array",
        typeClass = ListArrayType.class
)
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column()
    private String title;

    @Column
    private String contents;

    @Type(type = "list-array")
    @Column(
            columnDefinition = "text[]"
    )
    private List<String> images;

    @Column
    private String part;

    @Column
    private String creator;

    @Column
    private String scope;  // ALL, MEMBER (전체공개, 회원공개)

    public Notice() {
    }
}
