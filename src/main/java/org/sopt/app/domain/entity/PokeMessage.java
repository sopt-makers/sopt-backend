package org.sopt.app.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.sopt.app.domain.enums.PokeMessageType;
import org.sopt.app.domain.enums.PostgreSqlMessageType;

@Getter
@Setter
@Entity
@Table(name = "poke_message", schema = "app_dev")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(name = "message_type", typeClass = PostgreSqlMessageType.class)
public class PokeMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "content", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String content;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Type(type = "message_type")
    @Column(name = "type")
    private PokeMessageType type;
}