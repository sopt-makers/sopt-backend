package org.sopt.app.interfaces.alert;

import org.sopt.app.domain.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertRepository extends JpaRepository<Alert, Integer> {
}
