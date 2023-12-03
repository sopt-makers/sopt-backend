package org.sopt.app.interfaces.postgres;

import java.util.List;
import java.util.Optional;

import org.sopt.app.domain.entity.PokeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PokeHistoryRepository extends JpaRepository<PokeHistory, Long> {

    boolean existsByPokerIdAndPokedId(Long pokerId, Long pokedId);
    Optional<PokeHistory> findByPokerIdAndIsReplyIsFalse(Long pokedId);
    List<PokeHistory> findAllByPokerId(Long userId);
}