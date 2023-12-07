package org.sopt.app.interfaces.postgres;

import org.sopt.app.domain.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Integer> {
    Optional<Friend> findByUserIdAndAndFriendUserId(Long userId, Long friendId);

}