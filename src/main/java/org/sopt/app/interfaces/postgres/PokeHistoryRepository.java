package org.sopt.app.interfaces.postgres;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.sopt.app.domain.entity.poke.PokeHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PokeHistoryRepository extends JpaRepository<PokeHistory, Long> {

    List<PokeHistory> findAllByPokerIdAndPokedIdAndIsReplyIsFalse(Long pokerId, Long pokedId);

    List<PokeHistory> findAllByPokedId(Long userId);

    List<PokeHistory> findAllByPokerIdAndIsReply(Long userId, boolean isReply);

    List<PokeHistory> findAllByPokedIdAndIsReply(Long userId, boolean isReply);

    List<PokeHistory> findAllByPokerIdAndPokedIdOrderByCreatedAtDesc(Long pokerId, Long pokedId);

    Page<PokeHistory> findAllByIdIsInOrderByCreatedAtDesc(List<Long> historyIds, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE From PokeHistory ph WHERE ph.pokedId = :userId")
    void deleteAllByPokedIdInQuery(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE From PokeHistory ph WHERE ph.pokerId = :userId")
    void deleteAllByPokerIdInQuery(@Param("userId") Long userId);

    @Query("SELECT ph FROM PokeHistory ph WHERE ((ph.pokerId = :userId AND ph.pokedId = :friendId) OR (ph.pokerId = :friendId AND ph.pokedId = :userId)) AND ph.isReply = false ORDER BY ph.createdAt DESC ")
    List<PokeHistory> findAllWithFriendOrderByCreatedAtDescIsReplyFalse(@Param("userId") Long userId,
            @Param("friendId") Long friendId);

    @Query("SELECT ph FROM PokeHistory ph WHERE ((ph.pokerId = :userId AND ph.pokedId = :friendId) OR (ph.pokerId = :friendId AND ph.pokedId = :userId)) ORDER BY ph.createdAt DESC")
    List<PokeHistory> findAllPokeHistoryByUsers(@Param("userId") Long userId, @Param("friendId") Long friendId);

    Long countByPokedIdAndIsReplyIsFalse(Long pokedId);

    Long countByPokerIdOrPokedId(@NotNull Long pokerId, @NotNull Long pokedId);

    @Query("SELECT ph FROM PokeHistory ph WHERE ph.id IN (SELECT MAX(ph2.id) FROM PokeHistory ph2 WHERE ph2.pokedId = :userId AND ph2.isReply = false AND EXISTS (SELECT 1 FROM User u WHERE u.id = ph2.pokerId) GROUP BY ph2.pokerId) ORDER BY function('RANDOM')")
    List<PokeHistory> findRandomUnRepliedPokeMeHistory(@Param("userId") Long userId, Pageable pageable);
}
