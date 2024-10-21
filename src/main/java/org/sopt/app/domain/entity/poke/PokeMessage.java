package org.sopt.app.domain.entity.poke;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.sopt.app.domain.entity.BaseEntity;
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
    private Long id;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String content;

    @NotNull
    @Convert(converter = PokeMessageTypeConverter.class)
    private PokeMessageType type;
}