package org.sopt.app.interfaces.postgres;

import java.util.List;
import org.sopt.app.domain.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByUserId(Long userId, Pageable pageable);

}
