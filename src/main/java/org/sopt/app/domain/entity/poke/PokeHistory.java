package org.sopt.app.domain.entity.poke;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.app.domain.entity.BaseEntity;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PokeHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
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