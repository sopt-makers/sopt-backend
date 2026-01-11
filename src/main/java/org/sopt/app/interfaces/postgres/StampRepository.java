package org.sopt.app.interfaces.postgres;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.sopt.app.domain.entity.soptamp.Stamp;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StampRepository extends JpaRepository<Stamp, Long>, StampRepositoryCustom {

    List<Stamp> findAllByUserId(Long userId);

    Optional<Stamp> findByUserIdAndMissionId(Long userId, Long missionId);

    void deleteAllByUserId(Long userId);

    Optional<Stamp> findByIdAndUserId(Long id, Long userId);

    List<Stamp> findAllByUserIdIn(Collection<Long> userIds);

    boolean existsByUserIdInAndMissionId(Collection<Long> userIds, Long missionId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
      update Stamp s set s.viewCount = s.viewCount + 1
           where s.id = :stampId
    """)
    void increaseViewCount(@Param("stampId") Long stampId);

    @Query("""
    select s
    from Stamp s
    inner join Mission m on s.missionId = m.id
    where m.display = true
    order by s.createdAt desc
    """)
    List<Stamp> findDisplayedLatestStamps(Pageable pageable);
}
