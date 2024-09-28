package org.sopt.app.domain.entity.fortune;

import java.time.LocalDate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserFortune {

    @Id
    @NotNull
    private Long userId;

    @NotNull
    private Long fortuneId;

    @NotNull
    private LocalDate checkedAt;

    public void updateTodayFortune(final Long fortuneId, final LocalDate checkedAt) {
        this.fortuneId = fortuneId;
        this.checkedAt = checkedAt;
    }
}
