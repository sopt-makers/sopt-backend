package org.sopt.app.domain.entity.fortune;

import java.time.LocalDate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserFortune {

    @Id
    @NotNull
    private Long userId;

    @NotNull
    private Long fortuneId;

    @NotNull
    private LocalDate checkedAt;
}
