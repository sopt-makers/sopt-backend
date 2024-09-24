package org.sopt.app.domain.entity;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.sopt.app.domain.enums.PushTokenPlatform;

import jakarta.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PushToken extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private Long playgroundId;

    @Column(columnDefinition = "TEXT")
    @NotNull
    private String token;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PushTokenPlatform platform;

}
