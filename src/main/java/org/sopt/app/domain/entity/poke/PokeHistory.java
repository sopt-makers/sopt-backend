package org.sopt.app.domain.entity.poke;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @Column(columnDefinition = "TEXT")
    private String message;

    private Boolean isReply;

    @NotNull
    private Boolean isAnonymous;

    public void activateReply() {
        this.isReply = true;
    }
}