package org.sopt.app.interfaces.postgres;

import java.util.List;
import org.sopt.app.domain.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

public interface FriendRepository extends JpaRepository<Friend, Long>, FriendCustomRepository {
    Optional<Friend> findByUserIdAndAndFriendUserId(Long userId, Long friendId);

    List<Friend> findAllByUserId(Long userId);

    // SELECT * FROM app_dev.friend
    //WHERE friend.user_id = 19
    //AND friend_user_id not in (1)
    //AND friend_user_id not in (30)
    @Query(value = "SELECT f.friendUserId FROM Friend as f WHERE f.userId = :userId AND f.friendUserId not in :pokedFriendIds AND f.friendUserId not in :pokeFriendIds")
    List<Long> findAllByUserIdAndFriendUserIdNotInAndFriendUserIdNotIn(Long userId, List<Long> pokedFriendIds, List<Long> pokeFriendIds);
}