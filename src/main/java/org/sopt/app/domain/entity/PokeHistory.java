package org.sopt.app.domain.entity;

import javax.persistence.*;
import lombok.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Type;

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

    @Type(type = "org.hibernate.type.TextType")
    private String message;

    private Boolean isReply;

    @NotNull
    private Boolean isAnonymous;

    public void activateReply() {
        this.isReply = true;
    }
}