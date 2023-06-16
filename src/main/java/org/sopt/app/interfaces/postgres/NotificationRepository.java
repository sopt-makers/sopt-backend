package org.sopt.app.interfaces.postgres;

import org.sopt.app.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
