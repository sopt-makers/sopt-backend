package org.sopt.app.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

@Getter
@Setter
@Entity
@Table(name = "poke_history", schema = "app_dev")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PokeHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "poker_id", nullable = false)
    private Long pokerId;

    @NotNull
    @Column(name = "poked_id", nullable = false)
    private Long pokedId;

    @Column(name = "message")
    @Type(type = "org.hibernate.type.TextType")
    private String message;

    @Column(name = "is_reply")
    private Boolean isReply;

    @Column(name = "is_anonymous", nullable = false)
    private Boolean isAnonymous;

    public void activateReply() {
        this.isReply = true;
    }
}