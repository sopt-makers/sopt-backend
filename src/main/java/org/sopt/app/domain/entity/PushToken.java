package org.sopt.app.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(PushTokenPK.class)
@Table(name = "push_token", schema = "app_dev")
public class PushToken extends BaseEntity{

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "token_id")
//    private Long id;

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(columnDefinition = "TEXT", name = "token", nullable = false)
    private String token;


    public void updatePushToken(String pushToken) {
        this.token = pushToken;
    }


}
