package org.sopt.app.interfaces.postgres;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.sopt.app.domain.entity.PokeHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PokeHistoryRepository extends JpaRepository<PokeHistory, Long> {

    Optional<PokeHistory> findByPokerIdAndPokedIdAndIsReplyIsFalse(Long pokerId, Long pokedId);
    List<PokeHistory> findAllByPokerId(Long userId);
    List<PokeHistory> findAllByPokerIdAndCreatedAt(Long userId, LocalDateTime date);
    List<PokeHistory> findAllByPokerIdAndPokedId(Long pokerId, Long pokedId);

    List<PokeHistory> findAllByPokerIdAndIsReply(Long userId, boolean isReply);

    List<PokeHistory> findAllByPokedIdAndIsReply(Long userId, boolean isReply);
    List<PokeHistory> findAllByPokedId(Long userId);
    List<PokeHistory> findAllByPokedId(Long userId, Pageable pageable);
    List<PokeHistory> findAllByPokerIdAndPokedIdOrderByCreatedAtDesc(Long pokerId, Long pokedId);
}