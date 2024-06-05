package org.sopt.app.interfaces.postgres;

import org.sopt.app.domain.entity.Friend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long>, FriendCustomRepository  {
    Optional<Friend> findByUserIdAndFriendUserId(Long userId, Long friendId);

    @Query("SELECT f.friendUserId FROM Friend f WHERE f.userId = :userId")
    List<Long> findAllOfFriendIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT f.userId FROM Friend f WHERE f.friendUserId = :friendId")
    List<Long> findAllIfUserIdsByFriendId(Long friendId);

    List<Friend> findAllByFriendUserId(Long friendUserId);

    List<Friend> findAllByUserId(Long userId);

    List<Friend> findAllByUserIdAndFriendUserIdIn(Long userId, List<Long> friendIdsPokeMe);

    Page<Friend> findAllByUserIdAndFriendUserIdInOrderByPokeCount(Long userId, List<Long> friendIdsPokeMe, Pageable pageable);
}
