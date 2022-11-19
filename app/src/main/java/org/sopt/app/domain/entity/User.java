package org.sopt.app.domain.entity;

import lombok.Builder;
import lombok.Getter;
import org.sopt.app.domain.enums.OsType;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "app_users")
@Getter
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;

    @Column
    private String clientToken;

    @Column
    @Enumerated(EnumType.STRING)
    private OsType osType;

    @Column
    private LocalDate createdAt;

    @Column
    private LocalDate updatedAt;

    @Builder
    public User(String email, String nickname, String clientToken, OsType osType, String password) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.osType = osType;
        this.clientToken = clientToken;
    }

    public User() {

    }
}
