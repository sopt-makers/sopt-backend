package org.sopt.app.domain.entity;

import lombok.Getter;
import org.sopt.app.domain.enums.Authority;
import org.sopt.app.domain.enums.OsType;

import javax.persistence.*;

@Entity
@Table(name = "TMP_USER")
@Getter
public class User {

    @Id
    private Long id;

    @Column
    private String email;

    @Column
    @Enumerated(EnumType.STRING)
    private Authority auth;

    @Column
    private String clientToken;

    @Column
    @Enumerated(EnumType.STRING)
    private OsType osType;
}
