package org.sopt.app.interfaces.postgres;

import java.util.*;
import org.sopt.app.domain.entity.Friend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface FriendRepository extends JpaRepository<Friend, Long> {

    Optional<Friend> findByUserIdAndFriendUserId(Long userId, Long friendId);

    @Query("SELECT f.friendUserId FROM Friend f WHERE f.userId = :userId")
    Set<Long> findAllOfFriendIdsByUserId(@Param("userId") Long userId);

    List<Friend> findAllByFriendUserId(Long friendUserId);

    List<Friend> findAllByUserId(Long userId);

    List<Friend> findAllByUserIdAndFriendUserIdIn(Long userId, List<Long> friendIdsPokeMe);

    Page<Friend> findAllByUserIdAndFriendUserIdInOrderByPokeCount(Long userId, List<Long> friendIdsPokeMe,
            Pageable pageable);

    @Query("DELETE From Friend f WHERE f.friendUserId = :friendUserId")
    void deleteAllByFriendUserIdInQuery(@Param("friendUserId") Long friendUserId);

    @Query("DELETE From Friend f WHERE f.userId = :userId")
    void deleteAllByUserIdInQuery(@Param("userId") Long userId);
}
