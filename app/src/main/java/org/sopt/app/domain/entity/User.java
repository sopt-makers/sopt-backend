package org.sopt.app.domain.entity;

import org.sopt.app.domain.enums.Authority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TMP_USER")
public class User {

    @Id
    private Long id;

    @Column
    private String clientToken;

    @Column
    private Authority auth;
}
