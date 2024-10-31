package org.sopt.app.application.user;

import lombok.RequiredArgsConstructor;
import org.sopt.app.common.event.EventPublisher;
import org.sopt.app.interfaces.postgres.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserWithdrawService {

    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public void withdrawUser(Long userId) {
        eventPublisher.raise(new UserWithdrawEvent(userId));
        userRepository.deleteById(userId);
    }
}
