package org.sopt.app.application.user;

import lombok.RequiredArgsConstructor;
import org.sopt.app.domain.entity.UserWithdrawHistory;
import org.sopt.app.interfaces.postgres.UserWithdrawHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserWithdrawHistoryService {

    private final UserWithdrawHistoryRepository userWithdrawHistoryRepository;

    @Transactional
    public void recordWithdrawRequest(Long userId) {
        userWithdrawHistoryRepository.save(UserWithdrawHistory.of(userId));
    }
}
