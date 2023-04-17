package org.sopt.app.v1.interfaces.postgres;

import java.util.List;
import org.sopt.app.domain.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MissionRepositoryV1 extends JpaRepository<Mission, Long> {

    @Query("select m from Mission m where m.id in :missions")
    List<Mission> findMissionIn(@Param("missions") List<Long> missions);

}
