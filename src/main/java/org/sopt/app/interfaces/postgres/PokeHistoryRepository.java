package org.sopt.app.interfaces.postgres;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.sopt.app.domain.entity.PokeHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PokeHistoryRepository extends JpaRepository<PokeHistory, Long> {

    Optional<PokeHistory> findByPokerIdAndPokedIdAndIsReplyIsFalse(Long pokerId, Long pokedId);

    List<PokeHistory> findAllByPokerId(Long userId);
    List<PokeHistory> findAllByPokerIdAndCreatedAt(Long userId, LocalDateTime date);

    List<PokeHistory> findAllByPokerIdAndIsReply(Long userId, boolean isReply);
    List<PokeHistory> findAllByPokedIdAndIsReply(Long userId, boolean isReply);

    List<PokeHistory> findAllByPokedIdOrderByCreatedAtDesc(Long pokedId);
    Page<PokeHistory> findAllByPokedIdOrderByCreatedAtDesc(Long pokedId, Pageable pageable);

    List<PokeHistory> findAllByPokerIdAndPokedIdOrderByCreatedAtDesc(Long pokerId, Long pokedId);

    @Query("SELECT ph FROM PokeHistory ph WHERE (ph.pokerId = :userId AND ph.pokedId = :friendId) OR (ph.pokerId = :friendId AND ph.pokedId = :userId) ORDER BY ph.createdAt DESC ")
    List<PokeHistory> findAllWithFriendOrderByCreatedAtDesc(@Param("userId") Long userId, @Param("friendId") Long friendId);
}