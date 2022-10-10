package org.sopt.app.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ALERT")
public class Alert {
    @Id
    private Long id;

    @Column
    private Long tmpUserId;

    @Column
    private String topic;
}
