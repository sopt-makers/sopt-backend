package org.sopt.app.domain.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class PushTokenPK implements Serializable {
    private Long playgroundId;
    private String token;

    public static PushTokenPK of(Long playgroundId, String token) {
        return new PushTokenPK(playgroundId, token);
    }
}
