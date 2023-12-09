package org.sopt.app.interfaces.postgres;

import org.sopt.app.domain.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long>, FriendCustomRepository  {
    Optional<Friend> findByUserIdAndFriendUserId(Long userId, Long friendId);

    // SELECT * FROM app_dev.friend
    //WHERE friend.user_id = 19
    //AND friend_user_id not in (1)
    //AND friend_user_id not in (30)
    @Query(value = "SELECT f.friendUserId FROM Friend as f WHERE f.userId = :userId AND f.friendUserId not in :pokedFriendIds AND f.friendUserId not in :pokeFriendIds")
    List<Long> findAllByUserIdAndFriendUserIdNotInAndFriendUserIdNotIn(Long userId, List<Long> pokedFriendIds, List<Long> pokeFriendIds);
    @Query("SELECT f.friendUserId FROM Friend f WHERE f.userId = :userId")
    List<Long> findAllOfFriendIdsByUserId(@Param("userId") Long userId);
}