package org.sopt.app.interfaces.postgres;

import java.util.List;
import org.sopt.app.domain.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MissionRepository extends JpaRepository<Mission, Long> {

  @Query("select m from Mission m where m.id in :missions")
  List<Mission> findMissionIn(@Param("missions") List<Long> missions);

}
