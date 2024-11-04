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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private Long fortuneWordId;

    @NotNull
    private LocalDate checkedAt;

    public void updateTodayFortune(final Long fortuneWordId, final LocalDate checkedAt) {
        this.fortuneWordId = fortuneWordId;
        this.checkedAt = checkedAt;
    }
}
