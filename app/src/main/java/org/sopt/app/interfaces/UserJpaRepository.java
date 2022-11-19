package org.sopt.app.interfaces;

import org.sopt.app.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    
}
