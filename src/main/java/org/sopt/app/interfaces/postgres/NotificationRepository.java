package org.sopt.app.interfaces.postgres;

import java.util.List;
import java.util.Optional;
import org.sopt.app.domain.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByUserId(Long userId);

    List<Notification> findAllByUserId(Long userId, Pageable pageable);

    Optional<Notification> findByNotificationIdAndUserId(String notificationId, Long userId);

    @Query("DELETE FROM Notification n WHERE n.userId = :userId")
    void deleteByUserIdInQuery(@Param("userId") Long userId);
}
