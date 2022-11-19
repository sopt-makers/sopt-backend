package org.sopt.app.domain.entity;

import lombok.Builder;
import lombok.Getter;
import org.sopt.app.domain.enums.OsType;

import javax.persistence.*;

@Entity
@Table(name = "app_users", schema = "app_dev")
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
