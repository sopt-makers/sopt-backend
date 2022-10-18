package org.sopt.app.domain.entity;


import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "NOTICE")
@TypeDef(
        name = "list-array",
        typeClass = ListArrayType.class
)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notice extends BaseEntity implements Serializable {
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


    // 수정하기 위해 메소드 생성
    public void changeTitle(String title){
        this.title = title;
    }

    public void changeContents(String contents){
        this.contents = contents;
    }

    public void changePart(String part){
        this.part = part;
    }

    public void changeScope(String scope){
        this.scope = scope;
    }

    public void changeCreator(String creator){
        this.creator = creator;
    }

}
