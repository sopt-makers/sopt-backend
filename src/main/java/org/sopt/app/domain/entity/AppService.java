package org.sopt.app.domain.entity;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;
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

    @NotNull
    @Column(columnDefinition = "text[]")
    @ColumnDefault("'{}'::text[]")
    @Type(value= ListArrayType.class)
    private List<String> messages;

    @NotNull
    @Column(columnDefinition = "text[]")
    @ColumnDefault("'{}'::text[]")
    @Type(value= ListArrayType.class)
    private List<String> messageColors;
}
