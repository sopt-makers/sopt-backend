package org.sopt.app.interfaces.postgres;

import java.util.List;
import java.util.Optional;
import org.sopt.app.domain.entity.soptamp.Stamp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StampRepository extends JpaRepository<Stamp, Long> {

    List<Stamp> findAllByUserId(Long userId);

    Optional<Stamp> findByUserIdAndMissionId(Long userId, Long missionId);

    void deleteAllByUserId(Long userId);

    Optional<Stamp> findByIdAndUserId(Long id, Long userId);

}
