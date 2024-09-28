package org.sopt.app.domain.entity.fortune;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PROTECTED)
public class FortuneWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private String title;

    @NotNull
    private Long fortuneCardId;

    @NotNull
    private LocalDate checkAt;

    public static FortuneWord of(Long userId, String title, Long fortuneCardId, LocalDate checkAt) {
        return FortuneWord.builder()
                .userId(userId)
                .title(title)
                .fortuneCardId(fortuneCardId)
                .checkAt(checkAt).build();
    }
}
