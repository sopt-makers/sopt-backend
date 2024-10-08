package org.sopt.app.domain.entity.soptamp;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
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

    @NotNull
    private String title;

    @NotNull
    private Integer level;

    @NotNull
    @ColumnDefault("false")
    private boolean display;

    @Type(value= ListArrayType.class)
    @Column(columnDefinition = "text[]")
    private List<String> profileImage;
}
