package org.sopt.app.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "main_description", schema = "app_dev")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainDescription extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column
    private String activeTopDescription;

    @Column
    private String activeBottomDescription;

    @Column
    private String inactiveTopDescription;

    @Column
    private String inactiveBottomDescription;
}
