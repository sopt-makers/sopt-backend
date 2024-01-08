package org.sopt.app.interfaces.postgres;

import java.time.LocalDateTime;
import java.util.List;

import org.sopt.app.domain.entity.PokeHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PokeHistoryRepository extends JpaRepository<PokeHistory, Long> {

    List<PokeHistory> findAllByPokerIdAndPokedIdAndIsReplyIsFalse(Long pokerId, Long pokedId);

    List<PokeHistory> findAllByPokerId(Long userId);
    List<PokeHistory> findAllByPokedId(Long userId);
    List<PokeHistory> findAllByPokerIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);

    List<PokeHistory> findAllByPokerIdAndIsReply(Long userId, boolean isReply);
    List<PokeHistory> findAllByPokedIdAndIsReply(Long userId, boolean isReply);

    List<PokeHistory> findAllByPokerIdAndPokedIdOrderByCreatedAtDesc(Long pokerId, Long pokedId);

    Page<PokeHistory> findAllByIdIsInOrderByCreatedAt(List<Long> historyIds, Pageable pageable);

    @Query("SELECT ph FROM PokeHistory ph WHERE ((ph.pokerId = :userId AND ph.pokedId = :friendId) OR (ph.pokerId = :friendId AND ph.pokedId = :userId)) AND ph.isReply = false ORDER BY ph.createdAt DESC ")
    List<PokeHistory> findAllWithFriendOrderByCreatedAtDesc(@Param("userId") Long userId, @Param("friendId") Long friendId);
}