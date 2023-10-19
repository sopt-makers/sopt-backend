package org.sopt.app.domain.entity;

import lombok.*;
import org.sopt.app.domain.enums.PushTokenPlatform;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "push_token", schema = "app_dev")
public class PushToken extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "playground_id", nullable = false)
    private Long playgroundId;

    @Column( name = "token", columnDefinition = "TEXT", nullable = false)
    private String token;

    @Column(name = "platform", nullable = false)
    @Enumerated(EnumType.STRING)
    private PushTokenPlatform platform;

}
