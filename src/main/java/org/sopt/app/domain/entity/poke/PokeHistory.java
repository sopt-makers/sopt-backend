package org.sopt.app.domain.entity.poke;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnDefault;
import org.sopt.app.domain.entity.BaseEntity;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PokeHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long pokerId;

    @NotNull
    private Long pokedId;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String message;

    @NotNull
    @ColumnDefault("false")
    private boolean isReply;

    @NotNull
    @ColumnDefault("false")
    private boolean isAnonymous;

    public void activateReply() {
        this.isReply = true;
    }
}