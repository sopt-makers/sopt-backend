package org.sopt.app.interfaces.postgres;

import java.util.List;
import java.util.Optional;
import org.sopt.app.domain.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByPlaygroundId(Long userId);

    List<Notification> findAllByPlaygroundId(Long userId, Pageable pageable);

    Optional<Notification> findByIdAndPlaygroundId(Long id, Long playgroundId);
}
