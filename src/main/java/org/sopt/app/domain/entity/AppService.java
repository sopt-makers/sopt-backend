package org.sopt.app.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.sopt.app.application.app_service.AppServiceName;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppService extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AppServiceName serviceName;

    @NotNull
    private Boolean activeUser;

    @NotNull
    private Boolean inactiveUser;
}
