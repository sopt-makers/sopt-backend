package org.sopt.app.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.app.domain.enums.TeamNumber;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppjamUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private String teamName;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TeamNumber teamNumber;
}
