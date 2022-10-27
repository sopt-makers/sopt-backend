package org.sopt.app.application.user;

import lombok.RequiredArgsConstructor;
import org.sopt.app.domain.entity.User;
import org.sopt.app.interfaces.postgres.UserJpaRepository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserJpaRepository userJpaRepository;
    private final EntityManager em;

    public Optional<User> findAllById(Long userId){
        return userJpaRepository.findById(userId);
    }
}
