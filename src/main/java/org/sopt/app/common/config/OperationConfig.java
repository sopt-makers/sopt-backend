package org.sopt.app.common.config;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.app.domain.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "operation_configs" ,
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_operation_config_category_key",
                    columnNames = {"operation_config_category", "key"}
            )
})

public class OperationConfig extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String key;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OperationConfigType operationConfigType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OperationConfigCategory operationConfigCategory;

    @Column(nullable = false)
    private String description;
}
