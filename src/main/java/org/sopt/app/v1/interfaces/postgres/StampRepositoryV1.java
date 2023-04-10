package org.sopt.app.v1.interfaces.postgres;

import java.util.List;
import org.sopt.app.domain.entity.Stamp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StampRepositoryV1 extends JpaRepository<Stamp, Long> {

    List<Stamp> findAllByUserId(Long userId);

    Stamp findByUserIdAndMissionId(Long userId, Long missionId);

    void deleteAllByUserId(Long userId);

}
