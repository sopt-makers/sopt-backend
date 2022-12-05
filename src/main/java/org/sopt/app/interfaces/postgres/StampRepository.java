package org.sopt.app.interfaces.postgres;

import org.sopt.app.domain.entity.Stamp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StampRepository extends JpaRepository<Stamp, Long> {

  List<Stamp> findAllByUserId(Long userId);

  Stamp findByUserIdAndMissionId(Long userId, Long missionId);

}
