package org.sopt.app.domain.entity;

import javax.persistence.*;
import lombok.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friend extends BaseEntity {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private Long friendUserId;

    @ColumnDefault("1")
    private Integer pokeCount;

    @Column(length = 30)
    private String anonymousName;

    public void addPokeCount() {
        this.pokeCount++;
    }

}