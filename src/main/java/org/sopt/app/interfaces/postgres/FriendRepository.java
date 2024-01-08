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

    List<Friend> findAllByFriendUserId(Long friendUserId);

    @Query(value = "SELECT f FROM Friend f WHERE f.userId = :userId AND f.pokeCount BETWEEN :lowerLimit AND :upperLimit ORDER BY f.pokeCount")
    List<Friend> findAllByUserIdAndPokeCountBetweenOrderByPokeCountDesc(@Param("userId") Long userId, @Param("lowerLimit") Integer lowerLimit, @Param("upperLimit") Integer upperLimit);
    @Query(value = "SELECT f FROM Friend f WHERE f.userId = :userId AND f.pokeCount BETWEEN :lowerLimit AND :upperLimit ORDER BY f.pokeCount")
    Page<Friend> findAllByUserIdAndPokeCountBetweenOrderByPokeCountDesc(@Param("userId") Long userId, @Param("lowerLimit") Integer lowerLimit, @Param("upperLimit") Integer upperLimit, Pageable pageable);

    @Query(value = "SELECT count(f) FROM Friend f WHERE f.userId = :userId AND f.pokeCount BETWEEN :lowerLimit AND :upperLimit")
    int findSizeByUserIdAndPokeCountBetween(Long userId, Integer lowerLimit, Integer upperLimit);

    List<Friend> findAllByUserId(Long userId);

    List<Friend> findAllByUserIdAndFriendUserIdIn(Long userId, List<Long> friendIdsPokeMe);

    Page<Friend> findAllByUserIdAndFriendUserIdIn(Long userId, List<Long> friendIdsPokeMe, Pageable pageable);
}
