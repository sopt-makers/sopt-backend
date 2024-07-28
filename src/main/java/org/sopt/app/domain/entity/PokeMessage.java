package org.sopt.app.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Type;
import org.sopt.app.domain.enums.PokeMessageType;
import org.sopt.app.domain.enums.PokeMessageTypeConverter;

@Getter
@Entity
@Table(name = "poke_message", schema = "app_dev")
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @Convert(converter = PokeMessageTypeConverter.class)
    @Column(name = "type")
    private PokeMessageType type;
}