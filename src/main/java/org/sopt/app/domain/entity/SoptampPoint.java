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
@Table(name = "soptamp_point", schema = "app_dev")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SoptampPoint extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "generation")
    private Long generation;

    @Column(name = "soptamp_user_id")
    private Long soptampUserId;

    @Column(name = "points")
    private Long points;
}
