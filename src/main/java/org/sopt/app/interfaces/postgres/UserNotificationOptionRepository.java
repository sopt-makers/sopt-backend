package org.sopt.app.interfaces.postgres;

import org.sopt.app.domain.entity.User;
import org.sopt.app.domain.entity.UserNotificationOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserNotificationOptionRepository extends JpaRepository<UserNotificationOption, Long> {
    Optional<UserNotificationOption> findByUser(User user);
}
