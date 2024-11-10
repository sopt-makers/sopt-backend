package org.sopt.app.domain.entity;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Calendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Integer generation;

    @NotNull
    private String title;

    @NotNull
    private Boolean isOneDaySchedule;

    @NotNull
    private Boolean isOnlyActiveGeneration;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;
}
