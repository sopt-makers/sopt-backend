package org.sopt.app.domain.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Type;
import org.sopt.app.domain.enums.PokeMessageType;
import org.sopt.app.domain.enums.PokeMessageTypeConverter;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PokeMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private Long id;

    @NotNull
    @Type(type = "org.hibernate.type.TextType")
    private String content;

    @NotNull
    @Convert(converter = PokeMessageTypeConverter.class)
    private PokeMessageType type;
}