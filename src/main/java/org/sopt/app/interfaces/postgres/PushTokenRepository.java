package org.sopt.app.interfaces.postgres;

import org.sopt.app.domain.entity.PushToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PushTokenRepository extends JpaRepository<PushToken, Long> {
    boolean existsByUserIdAndToken(Long userId, String pushToken);

    Optional<PushToken> findByUserIdAndToken(Long userId, String token);

    List<PushToken> findAllByUserId(Long userId);

}
