package org.sopt.app.interfaces.postgres;

import org.sopt.app.domain.entity.UserWithdrawHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserWithdrawHistoryRepository extends JpaRepository<UserWithdrawHistory, Long> {

}
