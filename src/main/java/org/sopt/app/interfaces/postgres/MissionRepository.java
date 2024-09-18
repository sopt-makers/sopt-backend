package org.sopt.app.interfaces.postgres;

import java.util.List;
import org.sopt.app.domain.entity.soptamp.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MissionRepository extends JpaRepository<Mission, Long> {

    @Query("SELECT m FROM Mission m WHERE m.id IN :missions AND m.display = true ORDER BY m.level, m.title")
    List<Mission> findMissionInOrderByLevelAndTitleAndDisplayTrue(@Param("missions") List<Long> missions);

    @Query("SELECT m FROM Mission m WHERE m.id IN :missions ORDER BY m.level, m.title")
    List<Mission> findMissionInOrderByLevelAndTitle(@Param("missions") List<Long> missions);

    List<Mission> findAllByDisplay(boolean display);
}
