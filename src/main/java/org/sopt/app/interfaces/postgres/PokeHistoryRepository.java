package org.sopt.app.interfaces.postgres;

import java.util.List;
import java.util.Optional;

import org.sopt.app.domain.entity.PokeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PokeHistoryRepository extends JpaRepository<PokeHistory, Long> {

    Optional<PokeHistory> findByPokerIdAndIsReplyIsFalse(Long pokedId);
    List<PokeHistory> findAllByPokerId(Long userId);
    List<PokeHistory> findAllByPokerIdAndPokedId(Long pokerId, Long pokedId);

    List<PokeHistory> findAllByPokerIdAndIsReply(Long userId, boolean isReply);

    List<PokeHistory> findAllByPokedIdAndIsReply(Long userId, boolean isReply);
}