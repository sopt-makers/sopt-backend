package org.sopt.app.interfaces.postgres;

import org.sopt.app.domain.entity.PushToken;
import org.sopt.app.domain.entity.PushTokenPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PushTokenRepository extends JpaRepository<PushToken, PushTokenPK> {
    void deleteAllByUserId(Long userId);

    boolean existsById(PushTokenPK pushTokenPK);
}
