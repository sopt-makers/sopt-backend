package org.sopt.app.domain.entity.soptamp;


import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.Type;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_id")
    private Long id;

    private String title;

    private Integer level;

    private boolean display;

    @Type(value= ListArrayType.class)
    @Column(columnDefinition = "text[]")
    private List<String> profileImage;
}
