package org.sopt.app.domain.entity.fortune;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Fortune {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private Long fortuneCardId;

}
